package com.test.cdap.plugins.compute;

/**
 * Created by cdap on 2/16/17.
 */

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.api.dataset.DatasetProperties;
import co.cask.cdap.api.dataset.table.Get;
import co.cask.cdap.api.dataset.table.Put;
import co.cask.cdap.api.dataset.table.Row;
import co.cask.cdap.api.dataset.table.Table;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.batch.SparkCompute;
import co.cask.cdap.etl.api.batch.SparkExecutionPluginContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * DataWindowerExt - SparkCompute to transform input features into n-grams.
 */
@Plugin(type = SparkCompute.PLUGIN_TYPE)
@Name(DataWindowerExt.PLUGIN_NAME)
@Description("Used to transform input features into n-grams.")
public class DataWindowerExt extends SparkCompute<StructuredRecord, StructuredRecord> {
    private static final Logger LOG = LoggerFactory.getLogger(DataWindowerExt.class);
    public static final String PLUGIN_NAME = "DataWindowerExt";
    private DataWindowerExtConfig dataWindowerConfig;
    private Table messageHistoryTable;

    public DataWindowerExt(DataWindowerExtConfig dataWindowerConfig) {
        this.dataWindowerConfig = dataWindowerConfig;
    }

    public static class DataWindowerExtConfig extends PluginConfig {

        @Description("Field to be used to apply window operation.")
        public final String messageField;

        @Description("Window duration to qualify a message as stale or new")
        @Macro
        public final Integer windowDuration;

        @Description("Field to indicate the status of the message")
        public final String messageStatus;

        @Name("schema")
        @Description("Output schema")
        public final String schema;

        public DataWindowerExtConfig(String messageField, Integer windowDuration, String messageStatus, String schema) {
            this.messageField = messageField;
            this.windowDuration = windowDuration;
            this.messageStatus = messageStatus;
            this.schema = schema;
        }
    }

    @Override
    public void initialize(SparkExecutionPluginContext context) throws Exception {
        super.initialize(context);
        LOG.error("************* initialize *** " + context.getClass().toString() + "************* ");
        messageHistoryTable = context.getDataset("messageHistory");
    }

    @Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) throws IllegalArgumentException {
        super.configurePipeline(pipelineConfigurer);
        Schema messageHistorySchema =
                Schema.recordOf("messageHistorySchema",
                        Schema.Field.of("message", Schema.of(Schema.Type.STRING)),
                        Schema.Field.of("lastNotified", Schema.of(Schema.Type.LONG)));

        pipelineConfigurer.createDataset("messageHistory", Table.class,
                DatasetProperties.builder()
                        .add(Table.PROPERTY_SCHEMA, messageHistorySchema.toString())
                        .add(Table.PROPERTY_SCHEMA_ROW_FIELD, "message")
                        .build());

        try {
            Schema outputSchema = Schema.parseJson(dataWindowerConfig.schema);
            pipelineConfigurer.getStageConfigurer().setOutputSchema(outputSchema);
        } catch (IOException e) {
            throw new IllegalArgumentException("Output Schema specified is not a valid JSON. Please check the Schema JSON.");
        }
    }

    @Override
    public JavaRDD<StructuredRecord> transform(SparkExecutionPluginContext context,
                                               JavaRDD<StructuredRecord> javaRDD) throws Exception {

        LOG.error("************* " + context.getClass().toString() + "************* ");

        /*LOG.error("*************  Test Table access outside RDD - Start ************* ");
        Row testRow = messageHistoryTable.get(new Get("CDAP"));
        if (!testRow.isEmpty())
            LOG.error(" ----------------- TEST ROW NOT EMPTY -------------------");
        else
            LOG.error(" $$$$$$$$$$$$$$$$$$$$$$$$$ TEST ROW  EMPTY $$$$$$$$$$$$$$$$$$$$$$$$$");

        LOG.error("*************  Test Table access outside RDD - end ************* "); */


        final Schema outputSchema = Schema.parseJson(dataWindowerConfig.schema);
        JavaRDD<StructuredRecord> convertedRDD = javaRDD.map(new Function<StructuredRecord, StructuredRecord>() {
            @Override
            public StructuredRecord call(StructuredRecord structuredRecord) throws Exception {
                String message = structuredRecord.get(dataWindowerConfig.messageField);
                LOG.error("Message received ==> " + message);

                String messageStatus = "Unknown";
                boolean upsertMessage = true;
                long currentTime = System.currentTimeMillis();

                Row messageRow = messageHistoryTable.get(new Get(message));
                if (!messageRow.isEmpty()) {

                    long lastNotified = messageRow.getLong("lastNotified", -1);

                    if (lastNotified != -1 && lastNotified >= currentTime) {
                        long notificationGap = TimeUnit.MILLISECONDS.toSeconds(lastNotified - currentTime);
                        if (notificationGap > dataWindowerConfig.windowDuration) {
                            messageStatus = "NEW";
                        } else
                            messageStatus = "STALE";
                        upsertMessage = false;
                    }
                } else {
                    messageStatus = "NEW";
                }

                if (upsertMessage) {
                    messageHistoryTable.put(new Put(message).add("lastNotified", currentTime));
                }


                StructuredRecord.Builder builder = StructuredRecord.builder(outputSchema);
                for (Schema.Field field : outputSchema.getFields()) {
                    if (structuredRecord.get(field.getName()) != null) {
                        builder.set(field.getName(), structuredRecord.get(field.getName()));
                    }
                }
                builder.set(dataWindowerConfig.messageStatus, "STALE");
                return builder.build();
            }
        });
        return convertedRDD;
    }
}

