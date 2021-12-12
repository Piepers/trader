package me.piepers.trader.client.bitvavo;

import com.bitvavo.api.Bitvavo;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.WebSocket;
import me.piepers.trader.domain.Account;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test client for BitVavo.
 */
public class BitvavoClient extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BitvavoClient.class);
  public static final String BITVAVO_CLIENT_GET_ACCOUNT_ASSETS = "bitvavo.getAssets";

  Bitvavo bitvavo;

  @Override
  public void start(Promise<Void> startFuture) {
    JsonObject config = this.vertx.getOrCreateContext().config();
    JsonObject bitvavoPublicConfig = config.getJsonObject("bitvavo-public-config");
    JsonObject bitvavoNonPublicConfig = config.getJsonObject("bitvavo-api-client");

    this.bitvavo = new Bitvavo(new JSONObject("{" +
      "APIKEY: '" + bitvavoNonPublicConfig.getString("apikey") + "', " +
      "APISECRET: '" + bitvavoNonPublicConfig.getString("apisecret") + "', " +
      "RESTURL: '" + bitvavoPublicConfig.getString("resturl") + "'," +
      "WSURL: '" + bitvavoPublicConfig.getString("wsurl") + "'," +
      "ACCESSWINDOW:" + bitvavoPublicConfig.getInteger("accesswindow") + ", " +
      "DEBUGGING: " + bitvavoPublicConfig.getBoolean("debugging", false).toString() + " }"));
    LOGGER.debug("Started the Bitvavo client.");
    startFuture.complete();
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);

    vertx
      .eventBus()
      .<JsonObject>consumer(BITVAVO_CLIENT_GET_ACCOUNT_ASSETS, this::handleGetAccountAssets);

  }

  private void handleGetAccountAssets(Message<JsonObject> jsonObjectMessage) {
//    Observable.fromIterable(bitvavo.balance(new JSONObject())).map()
    LOGGER.debug("Requesting balance from Bitvavo");
    JSONArray balanceResult = bitvavo.balance(new JSONObject());
    Account account = Account.with(balanceResult);
    jsonObjectMessage.reply(account.toJson());
//     Observable
//      .just(bitvavo.balance(new JSONObject()))
//      .map(jsonarray -> Single.just(Account.with((JSONArray)jsonarray))
//        .doOnSuccess(account -> LOGGER.debug("Retrieved account information"))
//      .subscribe(jsonObjectMessage::reply,
//        throwable -> jsonObjectMessage.fail(500, "Something went wrong getting assets from Bitvavo.")));
  }
}
