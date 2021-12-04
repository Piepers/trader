package me.piepers.trader.client.bitvavo;

import io.vertx.core.Promise;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    this.config = new BitvavoConfig(this.vertx
      .getOrCreateContext()
      .config()
      .getJsonObject("bitvavo-read-only-client"));
    startFuture.complete();

  }
}
