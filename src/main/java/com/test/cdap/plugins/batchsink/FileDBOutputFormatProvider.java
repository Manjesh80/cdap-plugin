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
        this.conf = new HashMap<>();
        conf.put("filedb.filename", config.filename);
    }

    @Override
    public String getOutputFormatClassName() {
        return null;
    }

    @Override
    public Map<String, String> getOutputFormatConfiguration() {
        return null;
    }
}
