package me.piepers.trader.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TimeInForce {
  GTC("Good-Till-Cancel"),
  IOC("Immediate-Or-Cancel"),
  FOK("Fill-Or-Kill");

  private final String name;

  TimeInForce(String name) {
    this.name = name;
  }

  public static TimeInForce resolve(String value) {
    return Arrays
      .stream(TimeInForce.values())
      .filter(tif -> tif.name().equalsIgnoreCase(value))
      .findFirst()
      .orElseThrow(IllegalStateException::new);
  }
}
