package me.piepers.trader.domain;

import lombok.Getter;

import java.util.Arrays;

/**
 * The side of the order: 'SELL' or 'BUY'.
 */
@Getter
public enum Side {
  SELL, BUY;

  public static Side resolve(String side) {
    return Arrays
      .stream(Side.values())
      .filter(s -> s.name().equalsIgnoreCase(side))
      .findFirst()
      .orElseThrow(IllegalStateException::new);
  }
}
