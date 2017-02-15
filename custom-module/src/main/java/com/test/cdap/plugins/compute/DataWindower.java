package com.test.cdap.plugins.compute;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.batch.SparkCompute;
import co.cask.cdap.etl.api.batch.SparkExecutionPluginContext;
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

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 2/15/2017.
 */

@Plugin(type = SparkCompute.PLUGIN_TYPE)
@Name(DataWindower.PLUGIN_NAME)
@Description("Pluging that applies windowing on a given message")
public class DataWindower extends SparkCompute<StructuredRecord, StructuredRecord> {

    public static final String PLUGIN_NAME = "DataWindower";
    private DataWindowerConfig dataWindowerConfig;

    public DataWindower(DataWindowerConfig dataWindowerConfig) {
        this.dataWindowerConfig = dataWindowerConfig;
    }

    @Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) throws IllegalArgumentException {
        super.configurePipeline(pipelineConfigurer);
    }

    @Override
    public JavaRDD<StructuredRecord> transform(SparkExecutionPluginContext context,
                                               JavaRDD<StructuredRecord> javaRDD) throws Exception {
        JavaSparkContext javaSparkContext = context.getSparkContext();
        SQLContext sqlContext = new SQLContext(javaSparkContext);
        StructType schema = new StructType(new StructField[]{
                new StructField(dataWindowerConfig.messageStatus, DataTypes.StringType, false, Metadata.empty())});
        JavaRDD<StructuredRecord> convertedRDD = javaRDD.map(new DataWindowMapper(dataWindowerConfig.messageStatus));
        return convertedRDD;
    }
}
















