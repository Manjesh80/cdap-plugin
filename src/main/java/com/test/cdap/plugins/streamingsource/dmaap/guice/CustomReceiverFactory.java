package com.test.cdap.plugins.streamingsource.dmaap.guice;

import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.DMaaPReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/26/2017.
 */
public interface CustomReceiverFactory {
    CustomReceiver create(DMaaPStreamingConfig dmaapStreamingConfig);
}