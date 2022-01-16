package me.piepers.trader.adapter.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import me.piepers.trader.adapter.http.authentication.TraderUser;

import java.util.Objects;

public class TraderAuthProvider implements AuthenticationProvider {

  public static final TraderAuthProvider create(JsonObject config) {
    return new TraderAuthProvider();
  }


  @Override
  public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {
    Objects.requireNonNull(jsonObject);
    Objects.requireNonNull(jsonObject.getString("username"));
    Objects.requireNonNull(jsonObject.getString("password"));
    var username = jsonObject.getString("username");
    var password = jsonObject.getString("password");
    var user = new TraderUser(new JsonObject().put("username", "Jon Doe"));
    handler.handle(Future.succeededFuture(user));
  }

}
