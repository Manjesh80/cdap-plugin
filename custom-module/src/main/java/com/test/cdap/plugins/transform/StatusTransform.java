package com.test.cdap.plugins.transform;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.api.dataset.DatasetProperties;
import co.cask.cdap.api.dataset.table.Table;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.Emitter;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.Transform;
import co.cask.cdap.etl.api.TransformContext;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 2/17/2017.
 */
@Plugin(type = "transform")
@Name("StatusTransform")
@Description("Executes user-provided JavaScript that transforms one record into zero or more records.")
public class StatusTransform extends Transform<StructuredRecord, StructuredRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(StatusTransform.class);

    private final StatusTransformConfig config;
    private Schema outSchema;
    private Table messageRecordTable;

    public StatusTransform(StatusTransformConfig config) {
        this.config = config;
    }

    @Override
    public void initialize(TransformContext context) throws Exception {
        super.initialize(context);
        try {
            outSchema = Schema.parseJson(config.schema);
        } catch (IOException e) {
            throw new IllegalArgumentException("Output Schema specified is not a valid JSON. Please check the Schema JSON.");
        }

        //TODO:  How to get handle to Data-set ?????
    }

    @Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) throws IllegalArgumentException {
        super.configurePipeline(pipelineConfigurer);

        Schema messageHistorySchema =
                Schema.recordOf("messageRecordSchema",
                        Schema.Field.of("message", Schema.of(Schema.Type.STRING)),
                        Schema.Field.of("lastNotified", Schema.of(Schema.Type.LONG)));

        pipelineConfigurer.createDataset("messageRecord", Table.class,
                DatasetProperties.builder()
                        .add(Table.PROPERTY_SCHEMA, messageHistorySchema.toString())
                        .add(Table.PROPERTY_SCHEMA_ROW_FIELD, "message")
                        .build());

        try {
            Schema outputSchema = Schema.parseJson(config.schema);
            pipelineConfigurer.getStageConfigurer().setOutputSchema(outputSchema);
        } catch (IOException e) {
            throw new IllegalArgumentException("Output Schema specified is not a valid JSON. Please check the Schema JSON.");
        }
    }

    @Override
    public void transform(StructuredRecord structuredRecord, Emitter<StructuredRecord> emitter) throws Exception {

        String key = structuredRecord.get(config.field);
        LOG.error(" !!!!!!!!!!! MESSAGE TO LOOK for in DB ==> " + key);

        StructuredRecord.Builder builder = StructuredRecord.builder(outSchema);
        for (Schema.Field field : outSchema.getFields()) {
            if (structuredRecord.get(field.getName()) != null) {
                builder.set(field.getName(), structuredRecord.get(field.getName()));
            }
        }
        builder.set("MESSAGE_STATUS", "STALE");
        emitter.emit(builder.build());
    }

    public static class StatusTransformConfig extends PluginConfig {
        @Name("field")
        @Description("Input field to be considered for Row Key checking")
        private String field;

        @Name("schema")
        @Description("Output schema")
        private String schema;

        public StatusTransformConfig(String field, @Nullable String mapping, String schema) {
            this.field = field;
            this.schema = schema;
        }
    }
}
