package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import lombok.Value;

/**
 * The asset that we are trading with this application. This is based on the best
 * performing coins based on its ranking (market cap, prices).
 */
@Value
@DataObject
public class Asset implements Jsonable{
  String cmcId;
  Integer rank;
  String name;
  String symbol;
  double price;
  double marketCap;
}
