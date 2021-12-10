package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents how much is available in the account's wallet for a given asset.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DataObject
public class AssetBalance implements Jsonable {
  private String asset;
  private String free;
  private String locked;

  public AssetBalance(JsonObject jsonObject) {
    this.asset = jsonObject.getString("asset");
    this.free = jsonObject.getString("free");
    this.locked = jsonObject.getString("locked");
  }

  public static AssetBalance with(com.binance.api.client.domain.account.AssetBalance assetBalance) {
    return new AssetBalance(assetBalance.getAsset(), assetBalance.getFree(), assetBalance.getLocked());
  }

}
