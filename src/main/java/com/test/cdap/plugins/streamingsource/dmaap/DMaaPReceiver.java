package com.test.cdap.plugins.streamingsource.dmaap;

import com.test.cdap.plugins.streamingsource.common.AbstractReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import com.test.cdap.plugins.streamingsource.dmaap.guice.DMaaPPluginFactory;
import org.apache.spark.storage.StorageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/24/2017.
 */
public class DMaaPReceiver extends AbstractReceiver {

    private static final long serialVersionUID = -7114956462480794482L;
    private static final Logger LOG = LoggerFactory.getLogger(DMaaPReceiver.class);

    public DMaaPReceiver(StorageLevel storageLevel, DMaaPStreamingConfig dmaapStreamingConfig) {
        super(storageLevel, dmaapStreamingConfig);
        customReceiver = DMaaPPluginFactory.create().createCustomReceiver(dmaapStreamingConfig);
    }
}
