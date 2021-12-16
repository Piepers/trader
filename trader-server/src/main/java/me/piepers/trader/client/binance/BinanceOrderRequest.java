package me.piepers.trader.client.binance;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import me.piepers.trader.domain.Jsonable;
import me.piepers.trader.domain.Order;
import me.piepers.trader.domain.Side;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents an instruction to the BinanceClient to create a new order.
 */
@DataObject
@Data
public class BinanceOrderRequest implements Jsonable {
  private final boolean test;
  private final Order order;

  public BinanceOrderRequest(JsonObject jsonObject) {
    this.test = jsonObject.getBoolean("test");
    this.order = new Order(jsonObject.getJsonObject("order"));
  }

  public NewOrder toNewOrder() {
    return new NewOrder(order.getPair().format(false),
      this.map(order.getSide()), this.map(order.getType()), Objects.nonNull(order.getTimeInForce()) ? this.map(order.getTimeInForce()) : null,
    order.getQuantity().toString());
  }

  private OrderSide map(Side side) {
    return Arrays
      .stream(OrderSide.values())
      .filter(os -> os.name().equalsIgnoreCase(side.name()))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }

  private OrderType map(me.piepers.trader.domain.OrderType type) {
    return Arrays
      .stream(OrderType.values())
      .filter(ot -> ot.name().equalsIgnoreCase(type.name()))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }

  private TimeInForce map(me.piepers.trader.domain.TimeInForce timeInForce) {
    return Arrays
      .stream(TimeInForce.values())
      .filter(tif -> tif.name().equalsIgnoreCase(timeInForce.name()))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }
}
