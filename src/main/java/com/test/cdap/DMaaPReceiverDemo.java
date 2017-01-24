package com.test.cdap;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/24/2017.
 */
public class DMaaPReceiverDemo extends Receiver<StructuredRecord> {

    private static final long serialVersionUID = -7114956462480794482L;
    private static final Logger LOG = LoggerFactory.getLogger(DMaaPReceiverDemo.class);
    private final DMaaPStreamingConfig conf;

    private static final Schema OUTPUT_SCHEMA =
            Schema.recordOf("outputSchema",
                    Schema.Field.of("MESSAGE_NUM", Schema.of(Schema.Type.STRING)),
                    Schema.Field.of("MESSAGE", Schema.of(Schema.Type.STRING)));

    public DMaaPReceiverDemo(StorageLevel storageLevel, DMaaPStreamingConfig conf) {
        super(storageLevel);
        this.conf = conf;
    }

    public DMaaPStreamingConfig getConf() {
        return conf;
    }

    //region Standard methods override from CDAP Receiver Class
    @Override
    public StorageLevel storageLevel() {
        return StorageLevel.MEMORY_ONLY();
    }

    @Override
    public void onStart() {
        new Thread() {
            @Override
            public void run() {
                AtomicInteger serialNumber = new AtomicInteger();

                while (!isStopped()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);

                        StructuredRecord recordForNow = StructuredRecord.builder(OUTPUT_SCHEMA).
                                set("MESSAGE_NUM", Long.toString(System.currentTimeMillis())).
                                set("MESSAGE", "Message " + Integer.toString(serialNumber.incrementAndGet()))
                                .build();

                        store(recordForNow);

                    } catch (Exception e) {
                        LOG.error("Error getting content from {}.", e);
                    }
                }
            }

            @Override
            public void interrupt() {
                super.interrupt();
            }
        }.start();
    }

    @Override
    public void onStop() {

    }

    //endregion
}
