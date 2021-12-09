package me.piepers.trader.client.binance;

import com.binance.api.client.domain.account.AssetBalance;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.piepers.trader.domain.Jsonable;

@Data
@AllArgsConstructor
@DataObject
public class BinanceAssetBalance implements Jsonable {
  private String asset;
  private String free;
  private String locked;

  public BinanceAssetBalance(JsonObject jsonObject) {
    this.asset = jsonObject.getString("asset");
    this.free = jsonObject.getString("free");
    this.locked = jsonObject.getString("locked");
  }

  public static BinanceAssetBalance with(AssetBalance assetBalance) {
    return new BinanceAssetBalance(assetBalance.getAsset(), assetBalance.getFree(), assetBalance.getLocked());
  }
}
