package me.piepers.trader.client.binance;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.NewOrder;
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
  public static final String BINANCE_CLIENT_CREATE_ORDER = "binanceclient.createOrder";

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

    vertx
      .eventBus()
      .<JsonObject>consumer(BINANCE_CLIENT_CREATE_ORDER, this::handleCreateOrder);

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

  /**
   * Note that the asyncRestClient facility doesn't implement the onFailure itself. We must do that here explicitly
   * otherwise we won't get any feedback in case something fails.
   *
   * @param message, the message this message handles. Wille reply in case ok otherwise fail.
   */
  private void handleGetAccountBalances(Message<JsonObject> message) {
    asyncRestClient.getAccount(new BinanceApiCallback<>() {
      @Override
      public void onResponse(com.binance.api.client.domain.account.Account account) {
        message.reply(Account.with(account).toJson());
      }

      @Override
      public void onFailure(Throwable cause) {
        message.fail(500,
          "Something went wrong while calling the Binance REST API: " + cause.getMessage());
      }
    });
  }

  private void handleCreateOrder(Message<JsonObject> jsonObjectMessage) {
    Objects.requireNonNull(jsonObjectMessage.body());
    BinanceOrderRequest bor = new BinanceOrderRequest(jsonObjectMessage.body());
    if (bor.isTest()) {
      NewOrder newOrder = bor.toNewOrder();
      asyncRestClient.newOrderTest(newOrder, new BinanceApiCallback<Void>() {
        @Override
        public void onResponse(Void unused) {
          jsonObjectMessage.reply(new JsonObject().put("result", "ok"));
        }

        @Override
        public void onFailure(Throwable cause) {
          jsonObjectMessage.fail(500, "Something went wrong while calling the Binance REST API: " + cause.getMessage());
        }
      });
    } else {
      jsonObjectMessage.fail(1, "Not implemented.");
    }
  }

  @Override
  public Completable rxStop() {
    return Completable
      .fromAction(this::tryCloseWebsocket);
  }
}
