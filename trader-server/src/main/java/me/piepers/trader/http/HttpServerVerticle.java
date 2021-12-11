package me.piepers.trader.http;

import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static me.piepers.trader.client.binance.BinanceClient.*;
import static me.piepers.trader.client.bitvavo.BitvavoClient.BITVAVO_CLIENT_GET_ACCOUNT_ASSETS;
import static me.piepers.trader.client.coinbase.CoinbaseProClient.COINBASE_CLIENT_GET_ACCOUNT_DATA;

/**
 * Primarily to enable a user interface but also offers the ability to react on certain webhooks from
 * third parties.
 */
public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
  private static final int DEFAULT_HTTP_PORT = 8080;
  protected static final String CONTENT_TYPE_HEADER = "Content-Type";
  protected static final String JSON_CONTENT_TYPE = "application/json; " + StandardCharsets.UTF_8.name();
  private int port;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    JsonObject httpServerConfig = context.config().getJsonObject("http_server");
    this.port = Objects.nonNull(httpServerConfig) ? httpServerConfig.getInteger("port", DEFAULT_HTTP_PORT) : DEFAULT_HTTP_PORT;

  }

  @Override
  public void start(Promise promise) {
    Router router = Router.router(vertx);
    Router subRouter = Router.router(vertx);
    subRouter.route(HttpMethod.POST, "/tv").handler(this::handleTvWebHook);
    subRouter.route(HttpMethod.PUT, "/subscribe").handler(this::handleSubscribeToWs);
    subRouter.route(HttpMethod.PUT, "/unsubscribe").handler(this::handleUnSubscribeToWs);
    subRouter.route(HttpMethod.GET, "/account/binance").handler(routingContext -> this.handleGetAccount(routingContext, BINANCE_CLIENT_GET_ACCOUNT_DATA));
    subRouter.route(HttpMethod.GET, "/account/bitvavo").handler(routingContext -> this.handleGetAccount(routingContext, BITVAVO_CLIENT_GET_ACCOUNT_ASSETS));
    subRouter.route(HttpMethod.GET, "/account/coinbase").handler(routingContext -> this.handleGetAccount(routingContext, COINBASE_CLIENT_GET_ACCOUNT_DATA));

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

  private void handleGetAccount(RoutingContext routingContext, String accountAddress) {
    vertx.eventBus()
      .<JsonObject>rxRequest(accountAddress, new JsonObject())
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
}
