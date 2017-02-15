package com.test.cdap.plugins.compute;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 2/15/2017.
 */
public class DataWindowMapper implements Function<StructuredRecord, StructuredRecord> {

    private final String messageStatus;
    public Schema outputSchema;

    public DataWindowMapper(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public StructuredRecord call(StructuredRecord structuredRecord) throws Exception {
        outputSchema = Schema.recordOf(
                "outputSchema", Schema.Field.of(messageStatus, Schema.of(Schema.Type.STRING)));

        StructuredRecord.Builder builder = StructuredRecord.builder(outputSchema);
        for (Schema.Field field : outputSchema.getFields()) {
            builder.set(field.getName(), "STALE");
        }
        return builder.build();
    }
}
