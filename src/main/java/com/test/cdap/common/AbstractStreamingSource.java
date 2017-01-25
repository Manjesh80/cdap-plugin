package com.test.cdap.common;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.etl.api.streaming.StreamingContext;
import co.cask.cdap.etl.api.streaming.StreamingSource;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.receiver.Receiver;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public abstract class AbstractStreamingSource extends StreamingSource<StructuredRecord> {

    public abstract Receiver<StructuredRecord> getReceiver();

    @Override
    public JavaDStream<StructuredRecord> getStream(StreamingContext streamingContext) throws Exception {
        return streamingContext.getSparkStreamingContext().receiverStream( getReceiver());
    }
}
