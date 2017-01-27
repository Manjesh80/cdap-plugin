package com.test.cdap.plugins.batchsink;

import co.cask.cdap.api.data.batch.OutputFormatProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public class FileDBOutputFormatProvider implements OutputFormatProvider {
    private static final Logger LOG = LoggerFactory.getLogger(FileDBOutputFormatProvider.class);
    private final Map<String, String> conf;

    public FileDBOutputFormatProvider(FileDBBatchSink.FileDBSinkConfig config) {
        LOG.error("Creating FileDBOutputFormatProvider");
        this.conf = new HashMap<>();
        LOG.error(" FileDBOutputFormatProvider ==> Adding value");
        conf.put("filedb.filename", config.filename);
        LOG.error(" FileDBOutputFormatProvider ==> Adding value done");
    }

    @Override
    public String getOutputFormatClassName() {
        return FileDBOutputFormat.class.getName();
    }

    @Override
    public Map<String, String> getOutputFormatConfiguration() {
        return conf;
    }
}
