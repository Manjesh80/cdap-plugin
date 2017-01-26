package com.test.cdap.plugins.streamingsource.dmaap.guice;

import com.google.inject.AbstractModule;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.receiver.FastHttpReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public class DMaaPPluginModule extends AbstractModule{

    private static final Logger LOG = LoggerFactory.getLogger(DMaaPPluginModule.class);

    @Override
    protected void configure() {
        bind(CustomReceiver.class).to(FastHttpReceiver.class);
    }
}
