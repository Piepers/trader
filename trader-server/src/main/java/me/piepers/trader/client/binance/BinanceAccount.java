package me.piepers.trader.client.binance;

import com.binance.api.client.domain.account.Account;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.piepers.trader.domain.Jsonable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@DataObject
public class BinanceAccount implements Jsonable {
  private int makerCommission;
  private int takerCommission;
  private int buyerCommission;
  private int sellerCommission;
  private boolean canTrade;
  private boolean canWithdraw;
  private boolean canDeposit;
  private long updateTime;
  private List<BinanceAssetBalance> assetBalances;

  public BinanceAccount(JsonObject jsonObject) {
    this.makerCommission = jsonObject.getInteger("makerCommission");
    this.takerCommission = jsonObject.getInteger("takerCommission");
    this.buyerCommission = jsonObject.getInteger("buyerCommission");
    this.sellerCommission = jsonObject.getInteger("sellerCommission");
    this.canTrade = jsonObject.getBoolean("canTrade");
    this.canWithdraw = jsonObject.getBoolean("canWithdraw");
    this.canDeposit = jsonObject.getBoolean("canDeposit");
    this.updateTime = jsonObject.getLong("updateTime");
    this.assetBalances = jsonObject
      .getJsonArray("assetBalances")
      .stream()
      .map(ab -> new BinanceAssetBalance((JsonObject) ab))
      .collect(Collectors.toList());
  }

  public static BinanceAccount with(Account account) {
    return new BinanceAccount(account.getMakerCommission(),
      account.getTakerCommission(),
      account.getBuyerCommission(),
      account.getSellerCommission(),
      account.isCanTrade(),
      account.isCanWithdraw(),
      account.isCanDeposit(),
      account.getUpdateTime(),
      Objects.nonNull(account.getBalances()) ? account.getBalances()
        .stream()
        .map(BinanceAssetBalance::with)
        .collect(Collectors.toList()) : null);
  }
}
