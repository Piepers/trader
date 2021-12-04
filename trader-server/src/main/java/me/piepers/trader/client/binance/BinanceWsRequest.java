package me.piepers.trader.client.binance;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import me.piepers.trader.domain.Jsonable;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@DataObject
public class BinanceWsRequest implements Jsonable {
  private int id;
  private WsMethod method;
  private JsonArray params;

  public BinanceWsRequest(JsonObject jsonObject) {
    this.id = jsonObject.getInteger("id");
    this.method = WsMethod.resolve(jsonObject.getString("method"));
    this.params = jsonObject
      .getJsonArray("params");
  }

  public BinanceWsRequest(int id, WsMethod method, String... params) {
    this.id = id;
    this.method = method;
    this.params = new JsonArray(Arrays
      .stream(params)
      .collect(Collectors.toList()));
  }
}
