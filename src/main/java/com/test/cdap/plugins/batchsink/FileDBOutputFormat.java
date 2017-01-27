package com.test.cdap.plugins.batchsink;

import co.cask.cdap.api.data.batch.OutputFormatProvider;
import com.test.cdap.plugins.realtimesink.WordCountSink;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public class FileDBOutputFormat<K, V> extends OutputFormat<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(FileDBOutputFormat.class);

    public FileDBOutputFormat() {
    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {
        LOG.error("**** FileDBOutputFormat.checkOutputSpecs ****");
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        LOG.error("**** FileDBOutputFormat.getOutputCommitter ****");
        return new FileDBOutputCommitter();
    }

    @Override
    public RecordWriter<K, V> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        LOG.error("**** FileDBOutputFormat.getRecordWriter ****");
        return new FileDBWriter<K, V>();
    }
}