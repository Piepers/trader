package me.piepers.trader.client.bitvavo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class BitvavoConfig {
  private final String token;
  private final  String secret;
  private final String wsurl;
  private final int accessWindow;

  public BitvavoConfig(JsonObject jsonObject){
    this.token = jsonObject.getString("token");
    this.secret = jsonObject.getString("secret");
    this.wsurl = jsonObject.getString("wsurl");
    this.accessWindow = jsonObject.getInteger("accessWindow");
  }

  public String getToken() {
    return token;
  }

  public String getSecret() {
    return secret;
  }

  public String getWsurl() {
    return wsurl;
  }

  public int getAccessWindow() {
    return accessWindow;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BitvavoConfig that = (BitvavoConfig) o;

    if (accessWindow != that.accessWindow) return false;
    if (!token.equals(that.token)) return false;
    if (!secret.equals(that.secret)) return false;
    return wsurl.equals(that.wsurl);
  }

  @Override
  public int hashCode() {
    int result = token.hashCode();
    result = 31 * result + secret.hashCode();
    result = 31 * result + wsurl.hashCode();
    result = 31 * result + accessWindow;
    return result;
  }

  @Override
  public String toString() {
    return "BitvavoConfig{" +
      "token='" + token + '\'' +
      ", secret='" + secret + '\'' +
      ", wsurl='" + wsurl + '\'' +
      ", accessWindow=" + accessWindow +
      '}';
  }
}
