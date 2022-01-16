package me.piepers.trader.adapter.exchange.client.bitvavo;

import com.bitvavo.api.Bitvavo;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Supplier;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import me.piepers.trader.adapter.exchange.client.ClientConfig;
import me.piepers.trader.domain.Account;
import me.piepers.trader.domain.AssetBalance;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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
    ClientConfig bitvavoNonPublicConfig = new ClientConfig(config.getJsonObject("bitvavo-api-client"));

    this.bitvavo = new Bitvavo(new JSONObject("{" +
      "APIKEY: '" + bitvavoNonPublicConfig.getApikey() + "', " +
      "APISECRET: '" + bitvavoNonPublicConfig.getSecret() + "', " +
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
    LOGGER.debug("Requesting balance from Bitvavo");
    Observable
      .fromIterable(bitvavo.balance(new JSONObject()))
      .map(o -> (JSONObject)o)
      .map(AssetBalance::with)
      .collect((Supplier<ArrayList<AssetBalance>>) ArrayList::new, ArrayList::add)
      .map(Account::fromBitvavo)
      .subscribe(account -> jsonObjectMessage.reply(account.toJson()),
        throwable -> jsonObjectMessage.fail(500, "Something went wrong getting the account information from Bitvavo."));
  }
}
