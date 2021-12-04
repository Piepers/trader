package me.piepers.trader.client.binance;

import java.util.Arrays;

public enum WsMethod {
  SUBSCRIBE, UNSUBSCRIBE, UNKNOWN;

  public static WsMethod resolve(String value) {
    return Arrays
      .stream(WsMethod.values())
      .filter(v -> v.name().equalsIgnoreCase(value))
      .findFirst()
      .orElse(UNKNOWN);
  }
}
