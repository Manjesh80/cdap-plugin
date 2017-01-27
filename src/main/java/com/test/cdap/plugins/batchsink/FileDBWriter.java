package com.test.cdap.plugins.batchsink;

import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public class FileDBWriter<K, V> extends RecordWriter<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(FileDBWriter.class);
    @Override
    public void write(K k, V v) throws IOException, InterruptedException {

    }

    @Override
    public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {

    }
}
