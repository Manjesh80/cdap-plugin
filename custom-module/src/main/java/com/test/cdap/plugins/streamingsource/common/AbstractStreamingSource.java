package com.test.cdap.plugins.streamingsource.common;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.dataset.DatasetProperties;
import co.cask.cdap.api.dataset.lib.KeyValueTable;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.streaming.StreamingContext;
import co.cask.cdap.etl.api.streaming.StreamingSource;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.receiver.Receiver;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public abstract class AbstractStreamingSource extends StreamingSource<StructuredRecord> {

    /*@Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) throws IllegalArgumentException {
        super.configurePipeline(pipelineConfigurer);
        Map<String, String> props = new HashMap<>();
        props.put("domain", "measurementsForVfScaling");
        props.put("functionalRole", "vFirewall");

        pipelineConfigurer.createDataset("runtimeargs",
                KeyValueTable.class.getName(), DatasetProperties.builder().addAll(props).build());

    }*/

    @Override
    public JavaDStream<StructuredRecord> getStream(StreamingContext streamingContext) throws Exception {
        return streamingContext.getSparkStreamingContext().receiverStream(getReceiver());
    }

    public abstract Receiver<StructuredRecord> getReceiver();
}
