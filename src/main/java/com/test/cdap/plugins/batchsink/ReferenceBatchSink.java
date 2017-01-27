package com.test.cdap.plugins.batchsink;

import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.batch.BatchSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public abstract class ReferenceBatchSink<IN, KEY_OUT, VAL_OUT> extends BatchSink<IN, KEY_OUT, VAL_OUT> {
    private static final Logger LOG = LoggerFactory.getLogger(ReferenceBatchSink.class);
    private final ReferencePluginConfig config;

    public ReferenceBatchSink(ReferencePluginConfig config) {
        this.config = config;
    }

    @Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) {
        super.configurePipeline(pipelineConfigurer);
        // Verify that reference name meets dataset id constraints
        IdUtils.validateId(config.referenceName);
    }
}
