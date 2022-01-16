package me.piepers.trader.adapter.exchange.client;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import me.piepers.trader.domain.Jsonable;

/**
 * Generic configuration items to connect to apis. This would typically reside in non-public configuration files.
 */
@DataObject
@Getter
public class ClientConfig implements Jsonable {
  private final String apikey;
  private final String secret;
  private String passphrase;

  public ClientConfig(JsonObject jsonObject) {
    this.apikey = jsonObject.getString("apikey");
    this.secret = jsonObject.getString("secret");
    this.passphrase = jsonObject.getString("passphrase");
  }
}
