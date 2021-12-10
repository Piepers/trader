package me.piepers.trader.client.binance;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import me.piepers.trader.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

public class BinanceClient extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BinanceClient.class);
  public static final String BINANCE_CLIENT_SUBSCRIBE_ADDRESS = "binanceclient.subscribe";
  public static final String BINANCE_CLIENT_UNSUBSCRIBE_ADDRESS = "binanceclient.unsubscribe";
  public static final String BINANCE_CLIENT_GET_ACCOUNT_DATA = "binanceclient.getAccount";

  BinanceApiWebSocketClient bwsClient;
  Closeable ws;
  BinanceApiAsyncRestClient asyncRestClient;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);

    vertx
      .eventBus()
      .<JsonObject>consumer(BINANCE_CLIENT_SUBSCRIBE_ADDRESS, this::handleSubscribeMessage);

    vertx
      .eventBus()
      .<JsonObject>consumer(BINANCE_CLIENT_UNSUBSCRIBE_ADDRESS, this::handleUnsubscribeMessage);

    vertx
      .eventBus()
      .<JsonObject>consumer(BINANCE_CLIENT_GET_ACCOUNT_DATA, this::handleGetAccountBalances);

  }

  @Override
  public void start(Promise<Void> startFuture) {
    JsonObject config = this.vertx.getOrCreateContext().config();
    JsonObject binancePublicConfig = config
      .getJsonObject("binance-public-config");

    JsonObject binanceNonPublicConfig = config
      .getJsonObject("binance-api-client");

    BinanceApiClientFactory factory = BinanceApiClientFactory
      .newInstance(binanceNonPublicConfig.getString("api-key"), binanceNonPublicConfig.getString("secret"));

    this.bwsClient = factory.newWebSocketClient();

    this.asyncRestClient = factory.newAsyncRestClient();

    LOGGER.debug("Binance wsurl: {}", binancePublicConfig.getString("uri"));
    LOGGER.debug("Started the Binance client.");
    startFuture.complete();
  }

  private void trySendSubscription() {
    if (Objects.isNull(this.ws)) {
      LOGGER.debug("Subscribing");
      ws = bwsClient.onAggTradeEvent("etcbtc", this::handleAggTradeEvent);
    } else {
      LOGGER.warn("Already subscribed.");
    }
  }

  private void handleAggTradeEvent(AggTradeEvent aggTradeEvent) {
    LOGGER.debug("Received: " + aggTradeEvent.toString());
  }

  private void tryCloseWebsocket() {
    if (Objects.nonNull(ws)) {
      try {
        LOGGER.debug("Closing websocket.");
        ws.close();
      } catch (IOException e) {
        LOGGER.error("Could not close socket.", e);
      } finally {
        this.ws = null;
      }
    } else {
      LOGGER.debug("No websocket was set.");
    }
  }

  private void handleUnsubscribeMessage(Message<JsonObject> message) {
    this.tryCloseWebsocket();
    message.reply(new JsonObject().put("result", "ok"));
  }

  private void handleSubscribeMessage(Message<JsonObject> message) {
    this.trySendSubscription();
    message.reply(new JsonObject().put("result", "ok"));
  }

  private void handleGetAccountBalances(Message<JsonObject> message) {
    asyncRestClient.getAccount(response -> {
      message
        .reply(Account
          .with(response)
          .toJson());
    });
  }

  @Override
  public Completable rxStop() {
    return Completable
      .fromAction(() -> {
        this.tryCloseWebsocket();
      });
  }
}
