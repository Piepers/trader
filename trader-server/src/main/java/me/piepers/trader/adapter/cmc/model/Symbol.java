package me.piepers.trader.adapter.cmc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import lombok.Value;
import me.piepers.trader.domain.Jsonable;

/**
 * Represents a listing item from the Coin Market Cap API. In principle it's just a coin with an id (assigned by
 * CMC) with some metadata that we can use to do subsequent calls.
 */
@DataObject
@Value
public class Symbol implements Jsonable {
  private final Integer id;
  private final String name;
  private final String symbol;
  private final String slug;
  @JsonProperty("max_supply")
  private final Long maxSupply;
  @JsonProperty("circulating_supply")
  private final Long circulatingSupply;
  @JsonProperty("total_supply")
  private final Long totalSupply;
  @JsonProperty("cmc_rank")
  private final Integer cmcRank;
  private final Quote quote;
}
