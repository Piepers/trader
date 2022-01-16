package me.piepers.trader.adapter.cmc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import lombok.Value;
import me.piepers.trader.domain.Jsonable;

import java.time.Instant;

/**
 * When listings are retrieved, the coin has a "quote" part with a certain currency in it. Within it has some valuable
 * data about how much certain numbers have incresed and/or decreased.
 */
@DataObject
@Value
public class USD implements Jsonable {
  private final double price;
  @JsonProperty("volume_24h")
  private final double volume24h;
  @JsonProperty("volume_change_24h")
  private final double volumeChange24h;
  @JsonProperty("percent_change_1h")
  private final double percentChange1h;
  @JsonProperty("percent_change_24h")
  private final double percentChange24h;
  @JsonProperty("percent_change_7d")
  private final double percentChange7d;
  @JsonProperty("percent_change_30d")
  private final double percentChange30d;
  @JsonProperty("percent_change_60d")
  private final double percentChange60d;
  @JsonProperty("percent_change_90d")
  private final double percentChange90d;
  @JsonProperty("market_cap")
  private final double marketCap;
  @JsonProperty("market_cap_dominance")
  private final double marketCapDominance;
  @JsonProperty("fully_diluted_market_cap")
  private final double fullyDilutedMarketCap;
  @JsonProperty("last_updated")
  private final Instant lastUpdated;
}
