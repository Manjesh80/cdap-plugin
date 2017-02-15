package com.test.cdap.plugins.compute;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.plugin.PluginConfig;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 2/15/2017.
 */
public class DataWindowerConfig extends PluginConfig {
    @Description("Field to be used to apply window operation.")
    public final String messageField;

    @Description("Window duration to qualify a message as stale or new")
    @Macro
    public final Integer windowDuration;

    @Description("Field to indicate the status of the message")
    public final String messageStatus;

    public DataWindowerConfig(String messageField, Integer windowDuration, String messageStatus) {
        this.messageField = messageField;
        this.windowDuration = windowDuration;
        this.messageStatus = messageStatus;
    }
}
