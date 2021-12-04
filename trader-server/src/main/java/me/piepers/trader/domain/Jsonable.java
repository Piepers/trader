package me.piepers.trader.domain;

import io.vertx.core.json.JsonObject;

public interface Jsonable {
  default JsonObject toJson() {
    return JsonObject.mapFrom(this);
  }
}
