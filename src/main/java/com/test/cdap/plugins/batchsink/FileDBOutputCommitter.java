package com.test.cdap.plugins.batchsink;

import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public class FileDBOutputCommitter extends OutputCommitter {
    private static final Logger LOG = LoggerFactory.getLogger(FileDBOutputCommitter.class);

    @Override
    public void setupJob(JobContext jobContext) throws IOException {
        LOG.error("FileDBOutputCommitter.setupJob");
    }

    @Override
    public void setupTask(TaskAttemptContext taskAttemptContext) throws IOException {
        LOG.error("FileDBOutputCommitter.setupTask");
    }

    @Override
    public boolean needsTaskCommit(TaskAttemptContext taskAttemptContext) throws IOException {
        LOG.error("FileDBOutputCommitter.needsTaskCommit");
        return false;
    }

    @Override
    public void commitTask(TaskAttemptContext taskAttemptContext) throws IOException {
        LOG.error("FileDBOutputCommitter.commitTask");
    }

    @Override
    public void abortTask(TaskAttemptContext taskAttemptContext) throws IOException {
        LOG.error("FileDBOutputCommitter.abortTask");
    }
}
