package me.piepers.trader.client.coinbase;

import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.client.WebClient;
import me.piepers.trader.client.ClientConfig;
import me.piepers.trader.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

/**
 * Coinbase Pro Client. Coinbase doesn't provide an official Java API so we have to implement our calls including
 * signing and calculating security aspects ourselves.
 * <p>
 * See this documentation for reference: https://docs.cloud.coinbase.com/exchange/reference/
 */
public class CoinbaseProClient extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoinbaseProClient.class);
  private static final String HMAC_SHA256 = "HmacSHA256";
  public static final String COINBASE_CLIENT_GET_ACCOUNT_DATA = "coinbase.getAccount";
  private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
  private static final String CONTENT_TYPE_HEADER_VALUE = "application/json";
  private static final String CB_ACCESS_KEY_HEADER_NAME = "cb-access-key";
  private static final String CB_ACCESS_PASSPHRASE_NAME = "cb-access-passphrase";
  private static final String CB_ACCESS_SIGN_HEADER_NAME = "cb-access-sign";
  private static final String CB_ACCESS_TIMESTAMP_HEADER_NAME = "cb-access-timestamp";

  private WebClient webClient;
  private String host;
  private String apiKey;
  private String secret;
  private String passphrase;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    vertx.eventBus().<JsonObject>consumer(COINBASE_CLIENT_GET_ACCOUNT_DATA, this::handleGetAccountData);
  }

  @Override
  public void start(Promise<Void> startFuture) {
    JsonObject config = this.vertx.getOrCreateContext().config();

    JsonObject coinbasePublicConfig = config.getJsonObject("coinbase-pro-public-config");
    ClientConfig coinbaseNonPublicConfig = new ClientConfig(config.getJsonObject("coinbase-pro-api-client"));
    this.apiKey = coinbaseNonPublicConfig.getApikey();
    this.secret = coinbaseNonPublicConfig.getSecret();
    this.passphrase = coinbaseNonPublicConfig.getPassphrase();
    if (Objects.isNull(this.apiKey) || Objects.isNull(this.secret) || Objects.isNull(passphrase)) {
      startFuture.fail("Could not retrieve api key or secret while starting the Coinbase Pro API Client");
    } else {
      this.webClient = WebClient
        .create(this.vertx, new WebClientOptions()
          .setMaxPoolSize(2));

      this.host = coinbasePublicConfig.getString("host");
      startFuture.complete();
    }

  }

  private void handleGetAccountData(Message<JsonObject> jsonObjectMessage) {
    long timestamp = Instant.now().getEpochSecond();
    String uri = "/accounts";
    String preSign = timestamp + "GET" + uri;
    Mac sha256Hmac;
    String result;
    try {
      final byte[] byteKey = Base64.getDecoder().decode(secret);
      sha256Hmac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
      sha256Hmac.init(keySpec);
      byte[] macData = sha256Hmac.doFinal(preSign.getBytes(StandardCharsets.UTF_8));

      result = Base64.getEncoder().encodeToString(macData);

      webClient
        .get(this.host, uri)
        .port(443)
        .ssl(true)
        .putHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
        .putHeader(CB_ACCESS_KEY_HEADER_NAME, this.apiKey)
        .putHeader(CB_ACCESS_SIGN_HEADER_NAME, result)
        .putHeader(CB_ACCESS_TIMESTAMP_HEADER_NAME, String.valueOf(timestamp))
        .putHeader(CB_ACCESS_PASSPHRASE_NAME, passphrase)
        .putHeader("Accept", "application/json")
        .rxSend()
        .doOnError(throwable -> LOGGER.error("Failure while fetching account data from Coinbase Pro API", throwable))
        .map(response -> Account.with(response.bodyAsJsonArray()).toJson())
        .subscribe(jsonObjectMessage::reply,
          throwable -> jsonObjectMessage.fail(500, "Could not receive response: " + throwable.getMessage()));

    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      LOGGER.error("Could not calculate sign for account retrieval in the Coinbase Pro Client", e);
      jsonObjectMessage.fail(500, "Could not sign the message to be sent to Coinbase Pro API.");
    }
  }

  private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

  public static String bytesToHex(byte[] bytes) {
    byte[] hexChars = new byte[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars, StandardCharsets.UTF_8);
  }
}
