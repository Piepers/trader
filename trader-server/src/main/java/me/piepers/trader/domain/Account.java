package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONArray;

import java.time.Instant;
import java.util.ArrayList;
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
  private static final String NOTHING_SMALLER = "0.00";
  private Exchange exchange;
  private boolean canTrade;
  private boolean canWithdraw;
  private boolean canDeposit;
  private Instant updateTime;
  private List<AssetBalance> assetBalances;

  public Account(JsonObject jsonObject) {
    this.exchange = new Exchange(jsonObject);
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
      .filter(Account::hasBalance)
      .map(AssetBalance::with)
      .collect(Collectors.toList())
      : Collections.EMPTY_LIST;
    return new Account(Exchange.with("Binance"), account.isCanTrade(), account.isCanWithdraw(), account.isCanDeposit(),
      Instant.ofEpochMilli(account.getUpdateTime()), assetBalances);
  }

  private static boolean hasBalance(com.binance.api.client.domain.account.AssetBalance assetBalance) {
    return (!(assetBalance
      .getFree()
      .equals(NOTHING)) || !(assetBalance
      .getLocked()
      .equals(NOTHING))) &&
      (!assetBalance
        .getFree()
        .equals(NOTHING_SMALLER) || !assetBalance
        .getLocked()
        .equals(NOTHING_SMALLER));
  }

  public static Account with(JSONArray bitvavoBalanceResult) {
    List<AssetBalance> assetBalances = new ArrayList<>();
    // FIXME: use Observable
    for(int i = 0; i < bitvavoBalanceResult.length(); i ++) {
      AssetBalance ab = AssetBalance.with(bitvavoBalanceResult.getJSONObject(i));
      assetBalances.add(ab);
    }

    // FIXME: translate parameters to something we can use here.
    return new Account(Exchange.with("Bitvavo"), true, true, true, Instant.now(), assetBalances);
  }
}
