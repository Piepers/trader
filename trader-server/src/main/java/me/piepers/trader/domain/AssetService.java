package me.piepers.trader.domain;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;

/**
 * Gets the assets that we maintain in this application. This is derived from the top x assets on
 * CoinMarketCap (cmc) but can also contain assets that are added by users. Cmc is the base of this service at the
 * moment as that is where this service gets its data from. It keeps a list of assets in a cache and makes sure the
 * API is polled once a day to update the assets based on their performance.
 *
 * Note: in the future this service also makes sure that assets that are used in the application are not automatically
 * 'discarded' from the system because it was suddenly ranked lower than the top x assets we want to maintain.
 */
@VertxGen
@ProxyGen
public interface AssetService {
}
