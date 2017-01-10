package com.test.cdap;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.etl.api.streaming.StreamingContext;
import co.cask.cdap.etl.api.streaming.StreamingSource;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.receiver.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import co.cask.cdap.api.data.schema.Schema;

/**
 * Created by cdap on 10/14/16.
 */

@Plugin(type = StreamingSource.PLUGIN_TYPE)
@Name("DMaapStream")
@Description("Fetch data by performing a PULL request to DMaaP at a regular interval.")
public class DMaapStreamSource extends StreamingSource<StructuredRecord>{
    public static long messageCount = 1;
    private static final Logger LOG = LoggerFactory.getLogger(DMaapStreamSource.class);
    private final DMaapStreamConfig conf;

    private static final Schema OUTPUT_SCHEMA =
            Schema.recordOf("outputSchema",
                    Schema.Field.of("MESSAGE_NUM", Schema.of(Schema.Type.LONG)),
                    Schema.Field.of("MESSAGE", Schema.of(Schema.Type.STRING)));


    public DMaapStreamSource(DMaapStreamConfig conf) {
        this.conf = conf;
        LOG.error( "Ganesh ==> " + DMaapStreamSource.class.getClassLoader().toString() );
    }

    @Override
    public JavaDStream<StructuredRecord> getStream(StreamingContext streamingContext) throws Exception {

        LOG.error("Test conf " + conf.getDMaapHostName());
        Receiver<StructuredRecord> dmaapReceiver = new Receiver<StructuredRecord>(StorageLevel.MEMORY_ONLY()) {

            @Override
            public StorageLevel storageLevel() {
                return StorageLevel.MEMORY_ONLY();
            }

            @Override
            public void onStart() {
                new Thread() {

                    @Override
                    public void run() {
                        //HTTPRequestor httpRequestor = new HTTPRequestor(conf);

                        while (!isStopped()) {
                            DMaapStreamSource.messageCount = DMaapStreamSource.messageCount + 1;
                            try {
                                // Create dummy Structure Record
                                StructuredRecord recordForNow = StructuredRecord.builder(OUTPUT_SCHEMA).
                                        set("MESSAGE_NUM", DMaapStreamSource.messageCount).
                                        set("MESSAGE", Long.toString(System.currentTimeMillis()))
                                        .build();
                                store(recordForNow);

                            } catch (Exception e) {
                                LOG.error("Error getting content from {}.", e);
                            }

                            try {
                                TimeUnit.SECONDS.sleep(conf.getInterval());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
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
        };

        return streamingContext.getSparkStreamingContext().receiverStream(dmaapReceiver);
    }

}
