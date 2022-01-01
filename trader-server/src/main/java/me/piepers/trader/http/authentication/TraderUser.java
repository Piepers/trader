package me.piepers.trader.http.authentication;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;

public class TraderUser extends AbstractUser {
  private String username;
  private JsonObject principal;
  AuthProvider authProvider;

  public TraderUser(JsonObject principal) {
    this.principal = principal;
    this.username = principal.getString("username", "unknown");
  }

  @Override
  protected void doIsPermitted(String s, Handler<AsyncResult<Boolean>> handler) {
    handler.handle(Future.succeededFuture(true));
  }

  @Override
  public JsonObject attributes() {
    return null;
  }

  @Override
  public User isAuthorized(Authorization authorization, Handler<AsyncResult<Boolean>> handler) {
    handler.handle(Future.succeededFuture(Boolean.TRUE));
    return this;
  }

  @Override
  public JsonObject principal() {
    if (this.principal == null) {
      this.principal = (new JsonObject()).put("username", this.username);
    }

    return this.principal;
  }

  @Override
  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }
}
