package com.test.cdap.plugins.streamingsource.dmaap;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.etl.api.streaming.StreamingSource;
import com.test.cdap.plugins.streamingsource.common.AbstractStreamingSource;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cdap on 10/14/16.
 */

@Plugin(type = StreamingSource.PLUGIN_TYPE)
@Name("DMaapStream")
@Description("Fetch data by performing a PULL request to DMaaP at a regular interval.")
public class DMaapStreamSource extends AbstractStreamingSource {
    private static final Logger LOG = LoggerFactory.getLogger(DMaapStreamSource.class);
    private final DMaaPStreamingConfig conf;

    public DMaapStreamSource(DMaaPStreamingConfig conf) {
        this.conf = conf;
    }

    @Override
    public Receiver<StructuredRecord> getReceiver() {
        LOG.error("!!!!!!!!!!!! hostname " + this.conf.getDMaapHostName());
        LOG.error("!!!!!!!!!!!! topicName " + this.conf.getDmaapTopicName());
        return new DMaaPReceiver(StorageLevel.MEMORY_ONLY(), this.conf);
    }
}

