package me.piepers.trader.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A collection of accounts.
 */
@DataObject
@Getter
@AllArgsConstructor
public class Accounts implements Jsonable {
  private List<Account> accounts;

  public Accounts(JsonObject jsonObject) {
    this.accounts = jsonObject
      .getJsonArray("accounts")
      .stream()
      .map(o -> new Account((JsonObject) o))
      .collect(Collectors.toList());
  }

  public static final Accounts with(Account... account) {
    return new Accounts(Arrays.stream(account).collect(Collectors.toList()));
  }
}
