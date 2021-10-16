package me.piepers.trader;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraderApplication extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(TraderApplication.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // Set the main configuration for our application to be used.
    LOGGER.debug("Reading configuration...");
    ConfigStoreOptions mainConfigStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject()
        .put("path", "config/app-conf.json"));

    // In this case for instance the configuration to connect to BitVavo
    ConfigStoreOptions nonPublicConfigStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject()
        .put("path", "config/non-public-conf.json"));

    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
      .addStore(mainConfigStore)
      .addStore(nonPublicConfigStore);

    ConfigRetriever configRetriever = ConfigRetriever.create(this.vertx, options);
    configRetriever
      .rxGetConfig()
      .flatMapCompletable(configuration -> Completable.fromAction(() -> LOGGER.info("Deploying application components"))
        .andThen(this.deployWithConfigAndName(BitvavoClient.class.getName(), configuration)))
      .doOnComplete(() -> LOGGER.info("Application deployed successfully."))
      .subscribe(() -> startPromise.complete(),
        throwable -> startPromise.fail(throwable));

  }

  private Completable deployWithConfigAndName(String name, JsonObject config) {
    return this.vertx.rxDeployVerticle(name, new DeploymentOptions().setConfig(config)).ignoreElement();
  }
}
