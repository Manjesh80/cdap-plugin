package com.test.cdap.plugins.streamingsource.dmaap.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
        try {
            /*URL url = DMaaPPluginFactory.class.getClassLoader().getResource("guice.module");
            String moduleClassName;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                moduleClassName = reader.readLine();
            }

            AbstractModule module = (AbstractModule) Class.forName(moduleClassName).newInstance();
            final DMaaPPluginFactory dMaaPPluginFactory = new DMaaPPluginFactory(module);*/

            final DMaaPPluginFactory dMaaPPluginFactory = new DMaaPPluginFactory(new DMaaPPluginModule());

            return dMaaPPluginFactory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
