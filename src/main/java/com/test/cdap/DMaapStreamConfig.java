package com.test.cdap;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.datastreams.DataStreamsApp;

import javax.annotation.Nullable;


/**
 * Created by cdap on 10/14/16.
 */


public class DMaapStreamConfig extends PluginConfig {


    @Description("The amount of time to wait between each poll in seconds.")
    private long interval;

    @Description("The charset used to decode the response. Defaults to UTF-8.")
    @Nullable
    private String charset;

    @Description("Sets the read timeout in milliseconds. Set to 0 for infinite. Default is 60000 (1 minute).")
    @Nullable
    private Integer readTimeout;

    public long getInterval() {
        return interval;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public static final String DMAAP_HOSTNAME = "dmaapHostName";
    public static final String DMAAP_TOPICNAME = "dmaapTopicName";
    public static final String DMAAP_PROTOCOL = "dmaapProtocol";
    public static final String DMAAP_USERNAME = "dmaapUserName";
    public static final String DMAAP_USERPASSWORD = "dmaapUserPassword";
    public static final String DMAAP_CONTENTTYPE = "dmaapContentType";
    public static final String DMAAP_CONSUMERID = "dmaapConsumerId";
    public static final String DMAAP_CONSUMERGROUP = "dmaapConsumerGroup";
    public static final String DMAAP_TIMEOUTMS = "dmaapTimeoutMS";
    public static final String DMAAP_MESSAGELIMIT = "dmaapMessageLimit";

    @Nullable
    @Name(DMAAP_HOSTNAME)
    @Description("DMaap Hostname")
    private String dmaapHostName;
    public String getDMaapHostName() {
        return dmaapHostName;
    }

    @Nullable
    @Name(DMAAP_TOPICNAME)
    @Description("DMaap Topic Name")
    private String dmaapTopicName;
    public String getDmaapTopicName() {
        return dmaapTopicName;
    }

    @Nullable
    @Name(DMAAP_PROTOCOL)
    @Description("DMaap Protocaol")
    private String dmaapProtocol;
    public String getDmaapProtocol() {
        return dmaapProtocol;
    }

    @Nullable
    @Name(DMAAP_USERNAME)
    @Description("DMaap Username")
    private String dmaapUserName;
    public String getDmaapUsername() {
        return dmaapUserName;
    }

    @Nullable
    @Name(DMAAP_USERPASSWORD)
    @Description("DMaap Password")
    private String dmaapUserPassword;
    public String getDmaapPassword() {
        return dmaapUserPassword;
    }

    @Nullable
    @Name(DMAAP_CONTENTTYPE)
    @Description("DMaap ContentType")
    private String dmaapContentType;
    public String getDmaapContentType() {
        return dmaapContentType;
    }

    @Nullable
    @Name(DMAAP_CONSUMERID)
    @Description("DMaap ConsumerId")
    private String dmaapConsumerId;
    public String getDmaapConsumerId() {
        return dmaapConsumerId;
    }

    @Nullable
    @Name(DMAAP_CONSUMERGROUP)
    @Description("DMaap ConsumerGroup")
    private String dmaapConsumerGroup;
    public String getDmaapConsumerGroup() {
        return dmaapConsumerGroup;
    }

    @Nullable
    @Name(DMAAP_TIMEOUTMS)
    @Description("DMaap TimeoutMS")
    private Integer dmaapTimeoutMS;
    public Integer getDmaapTimeoutMS() {
        return dmaapTimeoutMS;
    }

    @Nullable
    @Name(DMAAP_MESSAGELIMIT)
    @Description("DMaap MessageLimit")
    private Integer dmaapMessageLimit;
    public Integer getDmaapMessageLimit() {
        return dmaapMessageLimit;
    }

    public DMaapStreamConfig() {
        this("", null, 60);
    }

    public DMaapStreamConfig(String referenceName, String url, long interval) {
        this(referenceName, url, interval, null);
    }

    public DMaapStreamConfig(String referenceName, String url, long interval, String requestHeaders) {
        this.interval = interval;
        this.readTimeout = 60 * 1000;
    }


    @SuppressWarnings("ConstantConditions")
    public void validate() {
        /* if (interval <= 0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid interval %d. Interval must be greater than 0.", interval));
        }
        if (readTimeout < 0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid readTimeout %d. Timeout must be 0 or a positive number.", readTimeout));
        }
        try {
            Charset.forName(charset);
        } catch (UnsupportedCharsetException e) {
            throw new IllegalArgumentException(String.format("Invalid charset %s.", charset));
        } */
    }
}