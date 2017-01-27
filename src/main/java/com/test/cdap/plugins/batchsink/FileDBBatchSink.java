package com.test.cdap.plugins.batchsink;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.batch.Output;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.api.dataset.lib.KeyValue;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.Emitter;
import co.cask.cdap.etl.api.batch.BatchSink;
import co.cask.cdap.etl.api.batch.BatchSinkContext;
import com.test.cdap.plugins.realtimesink.WordCountSink;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
@Plugin(type = "batchsink")
@Name("FileDB")
@Description("File Batch Sink converts a StructuredRecord to a String and writes it to File")
public class FileDBBatchSink extends ReferenceBatchSink<StructuredRecord, NullWritable, String> {

    private static final Logger LOG = LoggerFactory.getLogger(FileDBBatchSink.class);

    private final FileDBSinkConfig config;

    public FileDBBatchSink(FileDBSinkConfig config) {
        super(config);
        LOG.error("Creating Filebatch sink");
        this.config = config;
    }

    @Override
    public void prepareRun(BatchSinkContext batchSinkContext) throws Exception {
        LOG.error("Invoking Prepare RUN");
        if (config == null)
            LOG.error("!!! Config is null !!!");
        else
            LOG.error("**** Config is NOT null **** ");
        batchSinkContext.addOutput(Output.of(config.referenceName, new FileDBOutputFormatProvider(config)));
    }

    @Override
    public void transform(StructuredRecord input, Emitter<KeyValue<NullWritable, String>> emitter) throws Exception {
        LOG.error("Calling Transform");
        for (Schema.Field field : input.getSchema().getFields()) {
            LOG.error("Received field ==> " + field.getName().toLowerCase());
            if (field.getName().toLowerCase().equals("message")) {
                LOG.error("Emiting message ==> " + input.get(field.getName()).toString());
                emitter.emit(new KeyValue<>(NullWritable.get(), input.get(field.getName()).toString()));
            } else {
                LOG.error("Non message field received");
            }
        }
    }

    public static class FileDBSinkConfig extends ReferencePluginConfig {
        @Name(Properties.FILE_NAME)
        @Description("Filename to write")
        @Macro
        public String filename;

        public FileDBSinkConfig(String referenceName, String filename) {
            super(referenceName);
            this.filename = filename;
        }
    }

    public static class Properties {
        public static final String FILE_NAME = "filename";
    }
}
