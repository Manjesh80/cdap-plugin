package com.test.cdap.plugins.streamingsource.dmaap.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/26/2017.
 */
public class DMaaPPluginFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DMaaPPluginFactory.class);

    private final Injector injector;

    public DMaaPPluginFactory(AbstractModule guiceModule) {
        injector = Guice.createInjector(guiceModule);
    }

    public CustomReceiver createCustomReceiver(@Assisted DMaaPStreamingConfig dMaaPStreamingConfig) {
        final CustomReceiverFactory customReceiverFactory = injector.getInstance(CustomReceiverFactory.class);
        final CustomReceiver customReceiver = customReceiverFactory.create(dMaaPStreamingConfig);
        return customReceiver;
    }

    public static DMaaPPluginFactory create() {
        final DMaaPPluginFactory dMaaPPluginFactory = new DMaaPPluginFactory(new DMaaPPluginModule());
        return dMaaPPluginFactory;
    }
}
