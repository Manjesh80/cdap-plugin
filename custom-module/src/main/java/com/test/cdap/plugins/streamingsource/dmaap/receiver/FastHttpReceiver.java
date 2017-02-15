package com.test.cdap.plugins.streamingsource.dmaap.receiver;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.test.cdap.plugins.streamingsource.common.AbstractReceiver;
import com.test.cdap.plugins.streamingsource.common.CustomReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
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
                boolean messagesEmitted = false;

                LOG.error("!!!!!!!!!!!!! Emitting message Version 10 !!!!!!!!!!!!!!");

                while (!abstractReceiver.isStopped()) {

                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);


                        long currentTime = System.currentTimeMillis();
                        String dateTime = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
                        long reverseTime = Long.MAX_VALUE - currentTime;

                        int currentMinute = ((new DateTime()).getMinuteOfHour()) % 2;
                        String state = (currentMinute == 0) ? "NJ" : "NY";

                        abstractReceiver.store(StructuredRecord.builder(OUTPUT_SCHEMA).
                                set("MESSAGE_NUM", Long.toString(currentTime)).
                                set("MESSAGE", getGoodMessage(
                                        Long.toString(currentTime), reverseTime, state, dateTime))
                                .build());

                    } catch (Exception e) {
                        LOG.error("Error getting content from {}.", e);
                    }
                }     // while (!abstractReceiver.isStopped())
            }

            @Override
            public void interrupt() {
                super.interrupt();
            }
        }.start();
    }

    String getGoodMessage(String currentTime, long reverseTime, String state, String time) {

        String message = "{\n" +
                "  \"cef\": {\n" +
                "    \"event\": {\n" +
                "      \"ts\": " + reverseTime + ",\n" +
                "      \"state\": \"" + state + "\",\n" +
                "      \"country\": \"USA\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        /*String message = "{\n" +
                "  \"cef\": {\n" +
                "    \"event\": {\n" +
                "      \"reversets\": \"" + reverseTime + "\",\n" +
                "      \"ts\": \"" + currentTime + "\",\n" +
                "      \"id\": \"" + currentTime + "\",\n" +
                "      \"time\": \"" + time + "\",\n" +
                "      \"city\": \"Princeton\",\n" +
                "      \"state\": \"" + state + "\",\n" +
                "      \"country\": \"USA\"\n" +
                "    }\n" +
                "  }\n" +
                "}";*/
        return message;
        //return "{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NJ\",\"country\":\"USA\"}}}";
    }

    String getBadMessage() {
        return "{\"cef\":{\"event\":{\"id\":\"id-1\",\"country\":\"USA\"}}}";
    }

    @Override
    public void stop() {

    }

    @Override
    public void init(AbstractReceiver abstractReceiver) {
        this.abstractReceiver = abstractReceiver;
    }

    //region comment
    /*
    @Override
    public void startOld() {

        new Thread() {
            @Override
            public void run() {
                AtomicInteger serialNumber = new AtomicInteger(200);
                boolean messagesEmitted = false;

                while (!abstractReceiver.isStopped()) {

                    if (!messagesEmitted) {
                        messagesEmitted = true;
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                            LOG.error("!!!!!!!!!!!!! Emitting message Version 4 !!!!!!!!!!!!!!");

                            List<String> messages = new LinkedList<>();

                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NJ\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NY\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NJ\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NY\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NJ\",\"country\":\"USA\"}}}");

                            //messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"country\":\"USA\"}}}");

                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NY\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NJ\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NY\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NJ\",\"country\":\"USA\"}}}");
                            messages.add("{\"cef\":{\"event\":{\"id\":\"id-1\",\"city\":\"Princeton\",\"state\":\"NY\",\"country\":\"USA\"}}}");

                            for (String msg : messages) {
                                TimeUnit.MILLISECONDS.sleep(100);
                                abstractReceiver.store(StructuredRecord.builder(OUTPUT_SCHEMA).
                                        set("MESSAGE_NUM", Long.toString(System.currentTimeMillis())).
                                        set("MESSAGE", msg)
                                        .build());
                            }

                        } catch (Exception e) {
                            LOG.error("Error getting content from {}.", e);
                        }
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } // while (!abstractReceiver.isStopped())
            }

            @Override
            public void interrupt() {
                super.interrupt();
            }
        }.start();
    }
    */

    //endregion
}

