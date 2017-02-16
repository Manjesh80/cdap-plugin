package com.test.cdap.plugins.compute;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.api.dataset.Dataset;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.batch.SparkCompute;
import co.cask.cdap.etl.api.batch.SparkExecutionPluginContext;
import com.test.cdap.plugins.streamingsource.dmaap.receiver.FastHttpReceiver;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.feature.NGram;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 2/15/2017.
 */

@Plugin(type = SparkCompute.PLUGIN_TYPE)
@Name(DataWindower.PLUGIN_NAME)
@Description("Pluging that applies windowing on a given message")
public class DataWindower extends SparkCompute<StructuredRecord, StructuredRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(DataWindower.class);

    public static final String PLUGIN_NAME = "DataWindower";
    private DataWindowerConfig dataWindowerConfig;
    private List<Schema.Field> fields;
    // Output Schema that specifies the fields of JSON object
    private Schema outSchema;

    public DataWindower(DataWindowerConfig dataWindowerConfig) {
        this.dataWindowerConfig = dataWindowerConfig;
    }

    @Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) throws IllegalArgumentException {

        super.configurePipeline(pipelineConfigurer);

        try {
            Schema outputSchema = Schema.parseJson(dataWindowerConfig.schema);
            pipelineConfigurer.getStageConfigurer().setOutputSchema(outputSchema);
            fields = outputSchema.getFields();

            if (outputSchema.getField(dataWindowerConfig.messageStatus) == null) {
                throw new IllegalArgumentException(String.format("Field %s is not present in output schema", dataWindowerConfig.messageStatus));
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Output Schema specified is not a valid JSON. Please check the Schema JSON.");
        }

        Schema inputSchema = pipelineConfigurer.getStageConfigurer().getInputSchema();
        if (inputSchema != null && inputSchema.getField(dataWindowerConfig.messageField) == null) {
            throw new IllegalArgumentException(String.format("Field %s is not present in input schema", dataWindowerConfig.messageField));
        }
        if (inputSchema != null && inputSchema.getField(dataWindowerConfig.messageStatus) == null) {
            throw new IllegalArgumentException(String.format("Field %s is not present in input schema", dataWindowerConfig.messageStatus));
        }
    }

    @Override
    public void initialize(SparkExecutionPluginContext context) throws Exception {
        super.initialize(context);
        try {
            outSchema = Schema.parseJson(dataWindowerConfig.schema);
            fields = outSchema.getFields();
        } catch (IOException e) {
            throw new IllegalArgumentException("Output Schema specified is not a valid JSON. Please check the Schema JSON.");
        }
    }

    @Override
    public JavaRDD<StructuredRecord> transform(SparkExecutionPluginContext context,
                                               JavaRDD<StructuredRecord> javaRDD) throws Exception {
        JavaRDD<StructuredRecord> convertedRDD = javaRDD.map(new DataWindowMapper(dataWindowerConfig, outSchema));
        return convertedRDD;
    }
}

class DataWindowerConfig extends PluginConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DataWindowerConfig.class);

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

    public DataWindowerConfig(String messageField, Integer windowDuration, String messageStatus, String schema) {
        this.messageField = messageField;
        this.windowDuration = windowDuration;
        this.messageStatus = messageStatus;
        this.schema = schema;
    }
}


class DataWindowMapper implements Function<StructuredRecord, StructuredRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(DataWindowMapper.class);

    private final DataWindowerConfig dataWindowerConfig;
    public Schema outputSchema;

    public DataWindowMapper(DataWindowerConfig dataWindowerConfig, Schema outputSchema) {
        this.outputSchema = outputSchema;
        this.dataWindowerConfig = dataWindowerConfig;
    }

    @Override
    public StructuredRecord call(StructuredRecord structuredRecord) throws Exception {

        String message = structuredRecord.get(dataWindowerConfig.messageField);
        LOG.error("Message receiverd ==> " + message);

        //Table structure --> MESSAGE, MESSAGE-LAST-NOTIFIED, LIST<MESSAGE-ARRIVAL-TIME>

        // Step 1 ==> See if the message present in the table

        // GET IT , AND SEE WHEN IT WAS LAST NOTIFIED , IF NOTIFIED > THRESHOLD TIME, THEN NOTIFY

        // UPDATE IRRESEPECTIVE - UPSERT

        StructuredRecord.Builder builder = StructuredRecord.builder(outputSchema);
        for (Schema.Field field : outputSchema.getFields()) {
            if (structuredRecord.get(field.getName()) != null) {
                builder.set(field.getName(), structuredRecord.get(field.getName()));
            }
        }
        builder.set(dataWindowerConfig.messageStatus, "STALE");
        return builder.build();
    }
}















