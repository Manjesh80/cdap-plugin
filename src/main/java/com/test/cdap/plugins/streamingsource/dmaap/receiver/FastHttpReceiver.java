package com.test.cdap.plugins.streamingsource.dmaap.receiver;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.test.cdap.plugins.streamingsource.common.AbstractReceiver;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import org.apache.spark.storage.StorageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/26/2017.
 */
public class FastHttpReceiver implements CustomReceiver {

    AbstractReceiver abstractReceiver;
    DMaaPStreamingConfig dmaapStreamingConfig;

    @Inject
    public FastHttpReceiver(@Assisted DMaaPStreamingConfig dmaapStreamingConfig) {
        this.dmaapStreamingConfig = dmaapStreamingConfig;
    }

    private static final Logger LOG = LoggerFactory.getLogger(FastHttpReceiver.class);

    private static final Schema OUTPUT_SCHEMA =
            Schema.recordOf("outputSchema",
                    Schema.Field.of("MESSAGE_NUM", Schema.of(Schema.Type.STRING)),
                    Schema.Field.of("MESSAGE", Schema.of(Schema.Type.STRING)));

    @Override
    public void start() {

        new Thread() {
            @Override
            public void run() {
                AtomicInteger serialNumber = new AtomicInteger(200);


                //while (!abstractReceiver.isStopped()) {
                for (int i = 0; i < 5; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                        LOG.error("Version 2");
                        LOG.error("!!!!!!!!!!!!! Emitting message !!!!!!!!!!!!!!");
                        abstractReceiver.store(StructuredRecord.builder(OUTPUT_SCHEMA).
                                set("MESSAGE_NUM", Long.toString(System.currentTimeMillis())).
                                set("MESSAGE", getGoodMessage())
                                .build());

                        TimeUnit.MILLISECONDS.sleep(1000);
                        abstractReceiver.store(StructuredRecord.builder(OUTPUT_SCHEMA).
                                set("MESSAGE_NUM", Long.toString(System.currentTimeMillis())).
                                set("MESSAGE", getBadMessage())
                                .build());

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

    String getGoodMessage() {
        return "{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NJ\",\"country\":\"USA\"}}}";
        /*return "{\n" +
                "  \"cef\": {\n" +
                "    \"event\": {\n" +
                "      \"id\": \"id-1\",\n" +
                "      \"city\": \"Princeton\",\n" +
                "      \"state\": \"NJ\",\n" +
                "      \"country\": \"USA\"\n" +
                "    }\n" +
                "  }\n" +
                "}";*/

    }

    String getBadMessage() {
        return "{\"cef\":{\"event\":{\"id\":\"id-1\",\"country\":\"USA\"}}}";
        /*return "{\n" +
                "  \"cef\": {\n" +
                "    \"event\": {\n" +
                "      \"id\": \"id-1\",\n" +
                "      \"country\": \"USA\"\n" +
                "    }\n" +
                "  }\n" +
                "}";*/
    }

    @Override
    public void stop() {

    }

    @Override
    public void init(AbstractReceiver abstractReceiver) {
        this.abstractReceiver = abstractReceiver;
    }
}

