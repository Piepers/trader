package me.piepers.trader.adapter.http;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.rxjava3.ext.auth.User;
import io.vertx.rxjava3.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.handler.CorsHandler;
import me.piepers.trader.domain.Account;
import me.piepers.trader.domain.Accounts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static me.piepers.trader.adapter.exchange.client.binance.BinanceClient.*;
import static me.piepers.trader.adapter.exchange.client.bitvavo.BitvavoClient.BITVAVO_CLIENT_GET_ACCOUNT_ASSETS;
import static me.piepers.trader.adapter.exchange.client.coinbase.CoinbaseProClient.COINBASE_CLIENT_GET_ACCOUNT_DATA;

/**
 * Primarily to enable a user interface but also offers the ability to react on certain webhooks from
 * third parties.
 */
public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
  private static final int DEFAULT_HTTP_PORT = 8080;
  protected static final String CONTENT_TYPE_HEADER = "Content-Type";
  protected static final String JSON_CONTENT_TYPE = "application/json; " + StandardCharsets.UTF_8.name();
  private static final Integer DEFAULT_JWT_EXPIRATION = 60;
  private int port;
  private AuthenticationProvider authProvider;
  private JWTAuth jwtProvider;
  private Integer jwtExpiration;
  private boolean authenticationEnabled = false;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    JsonObject httpServerConfig = context.config().getJsonObject("http_server");
    this.port = Objects.nonNull(httpServerConfig) ? httpServerConfig.getInteger("port", DEFAULT_HTTP_PORT) : DEFAULT_HTTP_PORT;
  }

  @Override
  public void start(Promise promise) {
    JsonObject authConfig = context.config().getJsonObject("authentication", new JsonObject());
    if (authConfig.isEmpty()) {
      promise.fail("Unable to obtain authentication configuration.");
    } else {
      this.initializeAuth(authConfig);

      Router router = Router.router(vertx);
      router.route().handler(BodyHandler.create());

      router.route().handler(CorsHandler.create("*")
        .allowedHeader("*")
        .allowedMethod(HttpMethod.GET)
        .allowedMethod(HttpMethod.POST)
        .allowedMethod(HttpMethod.OPTIONS)
        .allowedMethod(HttpMethod.DELETE)
        .allowedMethod(HttpMethod.PATCH)
        .allowedMethod(HttpMethod.PUT));

      router.post("/api/login").handler(routingContext -> this.handleJsonLogin(routingContext, authProvider, jwtProvider));
      // Protect everything with user authentication behind /api.
      router.route("/api/*").handler(routingContext -> this.authenticationHandler(routingContext));
      Router subRouter = Router.router(vertx);
      subRouter.route(HttpMethod.POST, "/tv").handler(this::handleTvWebHook);
      subRouter.route(HttpMethod.PUT, "/subscribe").handler(this::handleSubscribeToWs);
      subRouter.route(HttpMethod.PUT, "/unsubscribe").handler(this::handleUnSubscribeToWs);
      subRouter.route(HttpMethod.GET, "/account/binance").handler(routingContext -> this.handleGetAccount(routingContext, BINANCE_CLIENT_GET_ACCOUNT_DATA));
      subRouter.route(HttpMethod.GET, "/account/bitvavo").handler(routingContext -> this.handleGetAccount(routingContext, BITVAVO_CLIENT_GET_ACCOUNT_ASSETS));
      subRouter.route(HttpMethod.GET, "/account/coinbase").handler(routingContext -> this.handleGetAccount(routingContext, COINBASE_CLIENT_GET_ACCOUNT_DATA));
      subRouter.route(HttpMethod.GET, "/accounts").handler(this::handleGetAllAvailableAccounts);
      subRouter.route(HttpMethod.POST, "/order/create").handler(routingContext -> this.handleCreateNewOrder(routingContext, BINANCE_CLIENT_CREATE_ORDER));
      subRouter.route(HttpMethod.GET, "/user/info").handler(this::handleGetUserInfo);

      router.mountSubRouter("/api", subRouter);

      // Start the Http Server
      vertx.createHttpServer(new HttpServerOptions()
          .setCompressionSupported(true))
        .requestHandler(router)
        .rxListen(port)
        .subscribe(result -> {
          LOGGER.debug("Http server has started on port {}", this.port);
          promise.complete();
        }, throwable -> {
          LOGGER.error("Http server start failed.");
          promise.fail(throwable);
        });
    }
  }

  private void handleGetUserInfo(RoutingContext routingContext) {
    if (this.authenticationEnabled) {
      this.getUserInfoFromBearer(routingContext)
        .subscribe(user -> {
            String username = user.principal().getString("username", "unknown");
            String usernameUrl = "";
            this.jsonResponse(routingContext, new JsonObject()
              .put("username", username)
              .put("thumb", usernameUrl));
          },
          throwable -> routingContext.fail(401));
    } else {
      // Just return an empty response if we're not authenticating.
      this.jsonResponse(routingContext, new JsonObject());
    }
  }

  private Single<User> getUserInfoFromBearer(RoutingContext routingContext) {
    // Obtain the jwt token from the header
    String bearer = this.getBearerFromHeader(routingContext);

    if (StringUtils.isEmpty(bearer)) {
      return Single.error(new IllegalStateException("Unable to find bearer token in the headers."));
    } else {
      return this.jwtAuth(bearer);
    }
  }

  private void handleJsonLogin(RoutingContext routingContext, AuthenticationProvider authProvider, JWTAuth jwtProvider) {
    if (this.authenticationEnabled) {
      JsonObject usernameAndPassword = routingContext.getBodyAsJson();
      if (Objects.isNull(usernameAndPassword)) {
        routingContext.fail(401);
      } else {
        String username = usernameAndPassword.getString("username");
        String password = usernameAndPassword.getString("password");
        if (Objects.nonNull(username) && Objects.nonNull(password)) {
          authProvider
            .authenticate(usernameAndPassword, result -> {
              if (result.succeeded()) {
                String token = jwtProvider.generateToken(result.result().principal(),
                  new JWTOptions()
                    .setAlgorithm("HS256")
                    .setExpiresInMinutes(this.jwtExpiration));
                LOGGER.debug("Principal logged in: {}", result.result().principal().encodePrettily());
                this.jsonResponse(routingContext, new JsonObject().put("token", token));
              } else {
                LOGGER.debug("Something went wrong when trying to authenticate: {}", result.cause().getMessage());
                result.cause().printStackTrace();
                routingContext.fail(403);
              }
            });

        } else {
          LOGGER.debug("No username and password were present in the body of the request.");
          routingContext.fail(400);
        }
      }
    } else {
      LOGGER.debug("Login attempt while authentication is disabled. Returning empty token.");
      this.jsonResponse(routingContext, new JsonObject().put("token", ""));
    }
  }

  protected void jsonResponse(RoutingContext routingContext, JsonObject jsonObject) {
    JsonObject response = jsonObject;
    if (Objects.isNull(response)) {
      response = this.genericOkResponse();
    }

    routingContext
      .response()
      .setStatusCode(200)
      .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
      .end(response.encode(), StandardCharsets.UTF_8.name());
  }

  protected JsonObject genericOkResponse() {
    return new JsonObject().put("message", "ok");
  }

  private void authenticationHandler(RoutingContext routingContext) {
    if (this.authenticationEnabled) {
      // Obtain the jwt token from the header
      String bearer = this.getBearerFromHeader(routingContext);

      if (StringUtils.isEmpty(bearer)) {
        routingContext.fail(401);
      } else {
        this.jwtAuth(bearer)
          .doOnSuccess(user -> LOGGER.trace("User is authenticated to proceed to {}", routingContext.request().uri()))
          .subscribe(user -> routingContext.next(),
            throwable ->
              routingContext.fail(401));
      }
    } else {
      routingContext.next();
    }
  }

  private Single<User> jwtAuth(String bearer) {
    return this.jwtProvider
      .rxAuthenticate(new JsonObject()
        .put("token", bearer))
      .doOnError(throwable -> LOGGER.debug("Unable to authenticate user with bearer {}", bearer))
      .doOnError(Throwable::printStackTrace);

  }

  private String getBearerFromHeader(RoutingContext routingContext) {
    String aut = routingContext
      .request()
      .headers()
      .get("Authorization");
    if (StringUtils.isNotEmpty(aut)) {
      return aut.substring(7);
    } else {
      return null;
    }
  }

  private void handleCreateNewOrder(RoutingContext routingContext, String address) {
    vertx
      .eventBus()
      .<JsonObject>rxRequest(address, routingContext.getBodyAsJson())
      .doOnError(throwable -> LOGGER.error("Could not create new order.", throwable))
      .subscribe(message -> routingContext
        .response()
        .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .end(message
          .body()
          .encode()), throwable -> routingContext
        .response()
        .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .setStatusCode(500)
        .setStatusMessage(throwable.getMessage())
        .end(new JsonObject().put("error", throwable.getMessage()).encode()));
  }

  private void handleGetAccount(RoutingContext routingContext, String accountAddress) {
    vertx.eventBus()
      .<JsonObject>rxRequest(accountAddress, new JsonObject())
      .doOnError(throwable -> LOGGER.error("Could not retrieve account data.", throwable))
      .subscribe(message -> routingContext
          .response()
          .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
          .end(message.body().encode()),
        throwable -> routingContext
          .response()
          .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
          .setStatusCode(500)
          .setStatusMessage(throwable.getMessage())
          .end(new JsonObject().put("error", throwable.getMessage()).encode()));
  }

  private void handleGetAllAvailableAccounts(RoutingContext routingContext) {
    Single<Message<JsonObject>> bitvavoAccountResponse = vertx.eventBus().<JsonObject>rxRequest(BITVAVO_CLIENT_GET_ACCOUNT_ASSETS, new JsonObject());
    Single<Message<JsonObject>> binanceAccountResponse = vertx.eventBus().<JsonObject>rxRequest(BINANCE_CLIENT_GET_ACCOUNT_DATA, new JsonObject());
    Single<Message<JsonObject>> coinbaseAccountResponse = vertx.eventBus().<JsonObject>rxRequest(COINBASE_CLIENT_GET_ACCOUNT_DATA, new JsonObject());

    Observable.zip(bitvavoAccountResponse.toObservable(),
        binanceAccountResponse.toObservable(),
        coinbaseAccountResponse.toObservable(),
        (bitvavoResponse, binanceResponse, coinbaseResponse) ->
          Accounts.with(new Account(bitvavoResponse.body()), new Account(binanceResponse.body()), new Account(coinbaseResponse.body())))
      .subscribe(accounts -> routingContext.response().putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .end(accounts.toJson().encode()), throwable -> routingContext
        .response()
        .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .setStatusCode(500)
        .setStatusMessage(throwable.getMessage())
        .end(new JsonObject().put("error", throwable.getMessage()).encode()));
  }

  private void handleUnSubscribeToWs(RoutingContext routingContext) {
    vertx
      .eventBus()
      .<JsonObject>rxRequest(BINANCE_CLIENT_UNSUBSCRIBE_ADDRESS, new JsonObject())
      .subscribe(message -> routingContext
        .response()
        .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .end(message
          .body()
          .encode()), throwable -> routingContext
        .response()
        .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .setStatusCode(500)
        .setStatusMessage(throwable.getMessage())
        .end(new JsonObject().put("error", throwable.getMessage()).encode()));
  }

  private void handleSubscribeToWs(RoutingContext routingContext) {
    vertx
      .eventBus()
      .<JsonObject>rxRequest(BINANCE_CLIENT_SUBSCRIBE_ADDRESS, new JsonObject())
      .subscribe(message -> routingContext
        .response()
        .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .end(message
          .body()
          .encode()), throwable -> routingContext
        .response()
        .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
        .setStatusCode(500)
        .setStatusMessage(throwable.getMessage())
        .end(new JsonObject().put("error", throwable.getMessage()).encode()));
  }

  private void handleTvWebHook(RoutingContext routingContext) {
    LOGGER.debug("Received something on the webhook endpoint:\n{}",
      routingContext.getBodyAsJson().encodePrettily());
    routingContext
      .response()
      .setStatusCode(200)
      .end();
  }

  private void initializeAuth(JsonObject authConfig) {
    boolean production = authConfig.getBoolean("production", true);
    this.authenticationEnabled = authConfig.getBoolean("enabled", false);

    String jwtKey = authConfig.getString("jwt_key");
    this.jwtExpiration = authConfig.getInteger("jwt_key_expiration", DEFAULT_JWT_EXPIRATION);

    this.authProvider = TraderAuthProvider.create(authConfig);

    this.jwtProvider = JWTAuth
      .create(vertx, new JWTAuthOptions()
        .addPubSecKey(new PubSecKeyOptions()
          .setAlgorithm("HS256")
          .setBuffer(jwtKey)
//          .setPublicKey(jwtKey)
          .setSymmetric(true)));

  }
}
