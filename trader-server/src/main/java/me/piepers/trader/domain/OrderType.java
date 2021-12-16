package me.piepers.trader.domain;

import lombok.Getter;

import java.util.Arrays;

/**
 * Order types are named differently across exchanges and not all
 * exchanges offer the same type and amount of types with their API
 * (or their entire platform). It is up to the application to send
 * the correct order type with the order request based on the exchange
 * against which the order is placed.
 */
@Getter
public enum OrderType {
  MARKET("Market"),
  LIMIT("Limit"),
  STOP_LOSS("Stop loss"),
  STOP_LOSS_LIMIT("Stop loss limit"),
  TAKE_PROFIT("Take profit"),
  TAKE_PROFIT_LIMIT("Take profit limit"),
  LIMIT_MAKER("Limit maker");

  private final String name;

  OrderType(String name) {
    this.name = name;
  }

  public static OrderType resolve(String type) {
    return Arrays
      .stream(OrderType.values())
      .filter(orderType -> orderType.name().equalsIgnoreCase(type))
      .findFirst()
      .orElseThrow(IllegalStateException::new);
  }
}
