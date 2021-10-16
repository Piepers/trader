package me.piepers.trader;

import io.vertx.core.Promise;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * Test client for BitVavo.
 */
public class BitvavoClient extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BitvavoClient.class);

  HttpClient httpClient;
  BitvavoConfig config;
  WebSocket webSocket;

  @Override
  public void start(Promise<Void> startFuture) {
    this.config = new BitvavoConfig(this.vertx.getOrCreateContext().config().getJsonObject("bitvavo-read-only-client"));
    LOGGER.debug("Bitvavo read only wsurl: {}", config.getWsurl());
    if (Objects.isNull(config)) {
      startFuture.fail("Unable to fetch configuration for Bitvavo.");
    } else {
      LOGGER.debug("Started the bitvavo client.");
      httpClient = vertx.createHttpClient(new HttpClientOptions().setMaxWebSocketFrameSize(524288));
      LOGGER.debug("HttpClient created...");
      WebSocketConnectOptions wsc = new WebSocketConnectOptions()
//        .setHost("ws-feed.exchange.coinbase.com")
        .setHost("stream.binance.com")
        .setSsl(true)
//        .setPort(443)
        .setPort(9443)
        .setURI("/ws/btcusd@kline_1m");
      httpClient
//        .rxWebSocket(443, "ws-feed.exchange.coinbase.com", "/")
        .rxWebSocket(wsc)
//        .rxWebSocket("wss://ws.bitvavo.com/v2/")
        .doOnSuccess(s -> startFuture.complete())
        .subscribe(websocket -> this.initWebsocket(websocket),
          throwable -> startFuture.fail(throwable));
    }
  }

  private void trySendSubscription() {
    LOGGER.debug("Trying to subscribe to ticker...");
    JsonArray markets = new JsonArray(Arrays.asList("BTC-EUR"));
    JsonArray interval = new JsonArray(Arrays.asList("1m"));
    JsonObject candlesChannel = new JsonObject()
      .put("name", "candles")
      .put("interval", interval)
      .put("markets", markets);
    String json = "{\n" +
      "    \"type\": \"subscribe\",\n" +
      "    \"product_ids\": [\n" +
      "        \"ETH-USD\",\n" +
      "        \"ETH-EUR\"\n" +
      "    ],\n" +
      "    \"channels\": [\n" +
      "        \"level2\",\n" +
      "        \"heartbeat\",\n" +
      "        {\n" +
      "            \"name\": \"ticker\",\n" +
      "            \"product_ids\": [\n" +
      "                \"ETH-BTC\",\n" +
      "                \"ETH-USD\"\n" +
      "            ]\n" +
      "        }\n" +
      "    ]\n" +
      "}";
    JsonObject coinBaseSub = new JsonObject(json);
    JsonObject subscribeToTickerMessage = new JsonObject()
      .put("action", "subscribe")
      .put("channels", new JsonArray(Arrays.asList(candlesChannel)));
    String binanceSubscribe = "{\n" +
      "  \"method\": \"SUBSCRIBE\",\n" +
      "  \"params\": [\n" +
      "    \"btcusdt@kline_1m\"\n" +
      "  ],\n" +
      "  \"id\": 1\n" +
      "}";
    JsonObject subscribeToCandleStick = new JsonObject(binanceSubscribe);

//    LOGGER.debug("Sending message\n{}", subscribeToTickerMessage.encodePrettily());
    LOGGER.debug("Sending message\n{}", subscribeToCandleStick.encodePrettily());

    this.webSocket
//      .rxWriteTextMessage(subscribeToTickerMessage
//      .rxWriteTextMessage(coinBaseSub.encode())
      .rxWriteTextMessage(subscribeToCandleStick.encode())
      .subscribe(() -> LOGGER.debug("Sent message"),
        throwable -> LOGGER.error("Something went wrong when sending message.", throwable));
  }

  private void initWebsocket(WebSocket websocket) {
    this.webSocket = websocket;

    this.webSocket
      .handler(this::handle)
      .endHandler(this::end)
      .closeHandler(this::close)
      .exceptionHandler(this::handleException);
    this.trySendSubscription();
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
}
