package me.piepers.trader.client.binance;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BinanceClient extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BinanceClient.class);
  public static final String BINANCE_CLIENT_SUBSCRIBE_ADDRESS = "binanceclient.subscribe";
  public static final String BINANCE_CLIENT_UNSUBSCRIBE_ADDRESS = "binanceclient.unsubscribe";

  HttpClient httpClient;
  WebSocket webSocket;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);

    vertx
      .eventBus()
      .<JsonObject>consumer(BINANCE_CLIENT_SUBSCRIBE_ADDRESS, this::handleSubscribeMessage);

    vertx
      .eventBus()
      .<JsonObject>consumer(BINANCE_CLIENT_UNSUBSCRIBE_ADDRESS, this::handleUnsubscribeMessage);
  }

  @Override
  public void start(Promise<Void> startFuture) {
    JsonObject config = this.vertx
      .getOrCreateContext()
      .config()
      .getJsonObject("binance-public-config");

    LOGGER.debug("Binance wsurl: {}", config.getString("uri"));
    if (Objects.isNull(config)) {
      startFuture.fail("Unable to fetch configuration for Binance.");
    } else {
      LOGGER.debug("Started the Binance client.");
      httpClient = vertx.createHttpClient(new HttpClientOptions().setMaxWebSocketFrameSize(524288));
      LOGGER.debug("HttpClient for Binance created...");
      String host = config.getString("host");
      String uri = config.getString("uri");
      Integer port = config.getInteger("port");

      WebSocketConnectOptions wsc = new WebSocketConnectOptions()
        .setHost(host)
        .setSsl(true)
        .setPort(port)
        .setURI(uri);

      httpClient
        .rxWebSocket(wsc)
        .doOnSuccess(s -> startFuture.complete())
        .subscribe(websocket -> this.initWebsocket(websocket),
          throwable -> startFuture.fail(throwable));
    }
  }

  private Completable trySendSubscription() {
    LOGGER.debug("Trying to subscribe to ticker...");
    BinanceWsRequest request = new BinanceWsRequest(WsMethod.SUBSCRIBE, "btcusdt@kline_1m");
    LOGGER.debug("Sending message\n{}", request.toJson().encode());

    return this.webSocket
      .rxWriteTextMessage(request.toJson().encode());
  }

  private Completable trySendUnsubscribe() {
    LOGGER.debug("Trying to unsubscribe from ticker...");
    BinanceWsRequest request = new BinanceWsRequest(WsMethod.UNSUBSCRIBE, "btcusdt@kline_1m");
    LOGGER.debug("Sending message\n{}", request.toJson().encode());
    return this.webSocket
      .rxWriteTextMessage(request.toJson().encode());
  }

  private void initWebsocket(WebSocket websocket) {
    this.webSocket = websocket;

    this.webSocket
      .handler(this::handle)
      .endHandler(this::end)
      .closeHandler(this::close)
      .exceptionHandler(this::handleException);
  }

  private void handleException(Throwable throwable) {
    LOGGER.error("Received error?", throwable);
    throwable.printStackTrace();
  }

  private void close(Void unused) {
    LOGGER.debug("Close handler called.");
    this.httpClient
      .rxClose()
      .subscribe(() -> LOGGER.debug("Httpclient close called."),
        throwable -> LOGGER.error("Could not close HttpClient", throwable));
  }

  private void end(Void unused) {
    LOGGER.debug("End handler called...");
  }

  private void handle(Buffer buffer) {
    LOGGER.debug("Received {}", buffer.toString());
  }

  private void handleUnsubscribeMessage(Message<JsonObject> message) {
    this.trySendUnsubscribe()
      .subscribe(() -> message
          .reply(new JsonObject().put("result", "ok")),
        throwable -> message
          .fail(500, throwable.getMessage()));
  }

  private void handleSubscribeMessage(Message<JsonObject> message) {
    this.trySendSubscription()
      .subscribe(() -> message
          .reply(new JsonObject().put("result", "ok")),
        throwable -> message
          .fail(500, throwable.getMessage()));
  }
}
