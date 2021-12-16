package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.Objects;

/**
 * Represents an order as it was sent to the exchange. It contains a generated Id (typically a uuid), an ordertype,
 * with which coins the order has to be executed and all the other data that is necessary to store.
 */
@DataObject
@Data
public class Order implements Jsonable {
  private String id;
  private OrderType type;
  private CoinPair pair;
  private Side side;
  private Double quantity;
  private Double price;
  private TimeInForce timeInForce;

  public Order(JsonObject jsonObject) {
    this.id = jsonObject.getString("id");
    this.type = OrderType.resolve(jsonObject.getString("type"));
    this.pair = new CoinPair(jsonObject.getJsonObject("pair"));
    this.side = Side.resolve(jsonObject.getString("side"));
    this.timeInForce = Objects
      .nonNull(jsonObject.getString("timeInForce")) ?
      TimeInForce
        .resolve(jsonObject.getString("timeInForce")) : null;
    this.quantity = jsonObject.getDouble("quantity");
    this.price = jsonObject.getDouble("price");
  }
}
