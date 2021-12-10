package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a collection of coins and their value on a particular exchange.
 */
@Data
@DataObject
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account implements Jsonable {
  private static final String NOTHING = "0.00000000";
  private boolean canTrade;
  private boolean canWithdraw;
  private boolean canDeposit;
  private Instant updateTime;
  private List<AssetBalance> assetBalances;

  public Account(JsonObject jsonObject) {
    this.canTrade = jsonObject.getBoolean("canTrade");
    this.canWithdraw = jsonObject.getBoolean("canWithdraw");
    this.canDeposit = jsonObject.getBoolean("canDeposit");
    this.updateTime = jsonObject.getInstant("updateTime");
    this.assetBalances = jsonObject
      .getJsonArray("assetBalances")
      .stream()
      .map(ab -> new AssetBalance((JsonObject) ab))
      .collect(Collectors.toList());
  }

  public static Account with(com.binance.api.client.domain.account.Account account) {

    List<AssetBalance> assetBalances = Objects.nonNull(account.getBalances()) ? account
      .getBalances()
      .stream()
      .filter(assetBalance -> !(assetBalance
        .getFree()
        .equals(NOTHING)) && !(assetBalance
        .getLocked()
        .equals(NOTHING)))
      .map(AssetBalance::with)
      .collect(Collectors.toList())
      : Collections.EMPTY_LIST;
    return new Account(account.isCanTrade(), account.isCanWithdraw(), account.isCanDeposit(),
      Instant.ofEpochMilli(account.getUpdateTime()), assetBalances);
  }
}
