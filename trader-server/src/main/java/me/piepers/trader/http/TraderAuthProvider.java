package me.piepers.trader.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import me.piepers.trader.http.authentication.TraderUser;

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

  @Override
  public Future<User> authenticate(JsonObject credentials) {
    return AuthenticationProvider.super.authenticate(credentials);
  }

  @Override
  public void authenticate(Credentials credentials, Handler<AsyncResult<User>> resultHandler) {
    AuthenticationProvider.super.authenticate(credentials, resultHandler);
  }

  @Override
  public Future<User> authenticate(Credentials credentials) {
    return AuthenticationProvider.super.authenticate(credentials);
  }
}
