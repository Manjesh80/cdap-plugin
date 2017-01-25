package com.test.cdap.common;

import co.cask.cdap.api.data.format.StructuredRecord;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public abstract class AbstractReceiver extends Receiver<StructuredRecord> {

    public AbstractReceiver(StorageLevel storageLevel) {
        super(storageLevel);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
