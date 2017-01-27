package com.test.cdap.plugins.batchsink;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.plugin.PluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public class ReferencePluginConfig extends PluginConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ReferencePluginConfig.class);
    @Name(Constants.Reference.REFERENCE_NAME)
    @Description(Constants.Reference.REFERENCE_NAME_DESCRIPTION)
    public String referenceName;

    public ReferencePluginConfig(String referenceName) {
        this.referenceName = referenceName;
    }
}