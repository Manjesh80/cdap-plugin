package com.test.cdap.plugins.streamingsource.dmaap.guice;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.test.cdap.plugins.streamingsource.dmaap.receiver.FastHttpReceiver;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public class DMaaPPluginModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(DMaaPPluginModule.class);

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(CustomReceiver.class, FastHttpReceiver.class)
                .build(CustomReceiverFactory.class)
        );
    }
}
