package com.test.cdap.plugins.realtimesink;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.api.dataset.DatasetProperties;
import co.cask.cdap.api.dataset.lib.KeyValueTable;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.batch.SparkExecutionPluginContext;
import co.cask.cdap.etl.api.batch.SparkPluginContext;
import co.cask.cdap.etl.api.batch.SparkSink;
import org.apache.avro.reflect.Nullable;
import org.apache.spark.api.java.JavaRDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/26/2017.
 */
@Plugin(type = SparkSink.PLUGIN_TYPE)
@Name(WordCountSink.PLUGIN_NAME)
@Description("Counts how many times each word appears in all records input to the aggregator.")
public class WordCountSink extends SparkSink<StructuredRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(WordCountSink.class);

    public static final String PLUGIN_NAME = "WordCount";
    private final Config config;

    /**
     * Config properties for the plugin.
     */
    public static class Config extends PluginConfig {

        @Description("The name of the FileSet to save the model to.")
        @Macro
        private final String fileSetName;

        @Description("Path of the FileSet to save the model to.")
        @Macro
        private final String path;

        @Description("A space-separated sequence of words to use for training.")
        private final String fieldToClassify;

        @Description("The field from which to get the prediction. It must be of type double.")
        private final String predictionField;

        @Nullable
        @Description("The number of features to use in training the model. It must be of type integer and equal to the" +
                " number of features used in NaiveBayesClassifier. The default value if none is provided will be" +
                " 100.")
        @Macro
        private final Integer numFeatures;

        public Config(String fileSetName, String path, String fieldToClassify, String predictionField,
                      Integer numFeatures) {
            this.fileSetName = fileSetName;
            this.path = path;
            this.fieldToClassify = fieldToClassify;
            this.predictionField = predictionField;
            this.numFeatures = numFeatures;
        }
    }

    public WordCountSink(Config config) {
        this.config = config;
    }

    @Override
    public void run(SparkExecutionPluginContext sparkExecutionPluginContext, JavaRDD<StructuredRecord> javaRDD) throws Exception {
        LOG.error("Calling word count RUN");
    }

    @Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) {
        LOG.error("Calling word count configurePipeline");
        Schema inputSchema = pipelineConfigurer.getStageConfigurer().getInputSchema();
        pipelineConfigurer.createDataset(config.fileSetName, KeyValueTable.class, DatasetProperties.EMPTY);
    }

    @Override
    public void prepareRun(SparkPluginContext sparkPluginContext) throws Exception {
        LOG.error("Calling word count Prepare RUN");
    }
}

