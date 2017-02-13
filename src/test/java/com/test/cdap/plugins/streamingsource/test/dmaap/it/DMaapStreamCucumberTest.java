package com.test.cdap.plugins.streamingsource.test.dmaap.it;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 2/1/2017.
 */

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/java/com/test/cdap/plugins/streamingsource/test/dmaap/it"},
        glue = "com.test.cdap.plugins.streamingsource.test.dmaap.it.steps"
)
public class DMaapStreamCucumberTest {

}
