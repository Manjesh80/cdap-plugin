package com.test.cdap.plugins.streamingsource.common;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.plugin.PluginConfig;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public abstract class AbstractReceiver extends Receiver<StructuredRecord> {

    protected CustomReceiver customReceiver;
    protected PluginConfig pluginConfig;

    public AbstractReceiver(StorageLevel storageLevel, PluginConfig pluginConfig) {
        super(storageLevel);
        this.pluginConfig = pluginConfig;
    }

    @Override
    public void onStart() {
        getCustomReceiver().init(this);
        getCustomReceiver().start();
    }

    @Override
    public void onStop() {
        getCustomReceiver().stop();
    }

    public void onData(StructuredRecord data) {
        store(data);
    }

    public boolean isReceiverStopped() {
        return isStopped();
    }

    public CustomReceiver getCustomReceiver() {
        return customReceiver;
    }
}
