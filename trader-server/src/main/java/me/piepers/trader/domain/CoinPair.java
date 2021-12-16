package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A coin pair is a representation of the coins that are used in  an order or trade. Some exchanges refer to this as the
 * 'symbol' (eg. BTCETH). It depends on the exchange how this is formatted exactly. Sometimes it's with a dash between
 * the two pairs and in most cases they are all uppercase.
 */
@DataObject
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CoinPair implements Jsonable {
  private String left;
  private String right;

  public CoinPair(JsonObject jsonObject) {
    this.left = jsonObject.getString("left");
    this.right = jsonObject.getString("right");
  }

  public static CoinPair with(String left, String right) {
    return new CoinPair(left, right);
  }

  public String format(boolean withDash) {
    return withDash ? left + "-" + right : left + right;
  }
}
