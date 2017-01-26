package com.test.cdap.plugins.streamingsource.dmaap;

import co.cask.cdap.api.data.schema.Schema;
import com.google.inject.assistedinject.Assisted;
import com.test.cdap.plugins.streamingsource.common.AbstractReceiver;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import org.apache.spark.storage.StorageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/24/2017.
 */
public class DMaaPReceiver extends AbstractReceiver {

    private static final long serialVersionUID = -7114956462480794482L;
    private static final Logger LOG = LoggerFactory.getLogger(DMaaPReceiver.class);

    public DMaaPReceiver(@Assisted StorageLevel storageLevel, @Assisted DMaaPStreamingConfig dmaapStreamingConfig,
                         CustomReceiver customReceiver) {
        super(storageLevel, customReceiver, dmaapStreamingConfig);
    }
}
