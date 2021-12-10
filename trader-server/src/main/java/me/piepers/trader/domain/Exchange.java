package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An exchange is a location where the system will send orders and pull data from. Can also represent a broker or
 * some other entity with which the system can interface.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DataObject
public class Exchange {
  private String name;

  public static Exchange with(final String name) {
    return new Exchange(name);
  }

  public Exchange(JsonObject jsonObject) {
    this.name = jsonObject.getString("name");
  }
}
