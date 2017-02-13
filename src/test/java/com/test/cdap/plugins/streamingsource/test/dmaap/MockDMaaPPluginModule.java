package com.test.cdap.plugins.streamingsource.test.dmaap;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.guice.CustomReceiverFactory;
import com.test.cdap.plugins.streamingsource.dmaap.receiver.FastHttpReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.receiver.MockHttpReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public class MockDMaaPPluginModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(MockDMaaPPluginModule.class);

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(CustomReceiver.class, MockHttpReceiver.class)
                .build(CustomReceiverFactory.class)
        );
    }
}
