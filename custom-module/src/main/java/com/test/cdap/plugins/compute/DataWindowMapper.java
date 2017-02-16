package com.test.cdap.plugins.compute;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import org.apache.spark.api.java.function.Function;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 2/15/2017.
 */
public class DataWindowMapper implements Function<StructuredRecord, StructuredRecord> {

    private final String messageStatus;
    private final DataWindowerConfig dataWindowerConfig;
    public Schema outputSchema;

    public DataWindowMapper(String messageStatus, DataWindowerConfig dataWindowerConfig) {
        this.messageStatus = messageStatus;
        this.dataWindowerConfig = dataWindowerConfig;
    }

    @Override
    public StructuredRecord call(StructuredRecord structuredRecord) throws Exception {

        List<Schema.Field> outputFields = new LinkedList<>();
        outputFields.addAll(structuredRecord.getSchema().getFields());
        outputFields.add(Schema.Field.of(messageStatus, Schema.of(Schema.Type.STRING)));

        outputSchema = Schema.recordOf("outputSchema", outputFields);

        StructuredRecord.Builder builder = StructuredRecord.builder(outputSchema);

        for (Schema.Field field : outputSchema.getFields()) {

            // If the field is input field then copy the data and continue
            if (structuredRecord.get(field.getName()) != null) {
                builder.set(field.getName(), structuredRecord.get(field.getName()));
                continue;
            }

            builder.set(field.getName(), "STALE");
        }
        return builder.build();
    }
}
