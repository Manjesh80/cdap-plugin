package com.test.cdap.plugins.streamingsource.common;

import java.io.Serializable;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public interface CustomReceiver extends Serializable {

    public void start();

    public void stop();

    public void init(AbstractReceiver abstractReceiver);

}
