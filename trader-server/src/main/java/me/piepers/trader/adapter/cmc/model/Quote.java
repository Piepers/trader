package me.piepers.trader.adapter.cmc.model;

import io.vertx.codegen.annotations.DataObject;
import lombok.Value;
import me.piepers.trader.domain.Jsonable;

/**
 * Represents the 'Quote' part of the response that comes back from the CoinMarketCap API when retrieving the
 * listings from their API.
 */
@DataObject
@Value
public class Quote implements Jsonable {
  private final USD usd;
}
