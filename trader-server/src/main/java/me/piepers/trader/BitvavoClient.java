package me.piepers.trader;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Test client for BitVavo.
 */
public class BitvavoClient extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BitvavoClient.class);

  HttpClient httpClient;
  BitvavoConfig config;

  @Override
  public void start(Promise<Void> startFuture) {
    this.config  = new BitvavoConfig(this.vertx.getOrCreateContext().config().getJsonObject("bitvavo-read-only-client"));
    LOGGER.debug("Bitvavo read only wsurl: {}", config.getWsurl());
    if (Objects.isNull(config) ) {
      startFuture.fail("Unable to fetch configuration for Bitvavo.");
    } else {
      LOGGER.debug("Started the bitvavo client.");
      httpClient.rxWebSocket(this.config.getWsurl())
      startFuture.complete();
    }
  }
}
