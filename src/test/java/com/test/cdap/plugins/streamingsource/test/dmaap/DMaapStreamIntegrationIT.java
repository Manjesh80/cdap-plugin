package com.test.cdap.plugins.streamingsource.test.dmaap;

import co.cask.cdap.api.artifact.ArtifactVersion;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.dataset.table.Table;
import co.cask.cdap.common.utils.Tasks;
import co.cask.cdap.datastreams.DataStreamsApp;
import co.cask.cdap.datastreams.DataStreamsSparkLauncher;
import co.cask.cdap.etl.api.streaming.StreamingSource;
import co.cask.cdap.etl.mock.batch.MockSink;
import co.cask.cdap.etl.mock.test.HydratorTestBase;
import co.cask.cdap.etl.proto.v2.DataStreamsConfig;
import co.cask.cdap.etl.proto.v2.ETLPlugin;
import co.cask.cdap.etl.proto.v2.ETLStage;
import co.cask.cdap.proto.artifact.AppRequest;
import co.cask.cdap.proto.artifact.ArtifactRange;
import co.cask.cdap.proto.artifact.ArtifactSummary;
import co.cask.cdap.proto.id.ApplicationId;
import co.cask.cdap.proto.id.ArtifactId;
import co.cask.cdap.proto.id.NamespaceId;
import co.cask.cdap.test.ApplicationManager;
import co.cask.cdap.test.DataSetManager;
import co.cask.cdap.test.SparkManager;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.test.cdap.plugins.streamingsource.dmaap.DMaapStreamSource;
import com.test.cdap.plugins.streamingsource.dmaap.config.DMaaPStreamingConfig;
import com.test.cdap.plugins.streamingsource.dmaap.receiver.FastHttpReceiver;
import com.test.cdap.plugins.streamingsource.dmaap.receiver.MockHttpReceiver;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by cdap on 10/18/16.
 */

public class DMaapStreamIntegrationIT extends HydratorTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(DMaapStreamIntegrationIT.class);
    protected static final ArtifactId DATASTREAMS_ARTIFACT_ID = NamespaceId.DEFAULT.artifact("data-streams", "3.2.0");
    protected static final ArtifactSummary DATASTREAMS_ARTIFACT = new ArtifactSummary("data-streams", "3.2.0");

    @ClassRule
    public static TemporaryFolder tmpFolder = new TemporaryFolder();

    @BeforeClass
    public static void setupTest() throws Exception {

        LOG.error("********* Test Message **********");
        setupStreamingArtifacts(DATASTREAMS_ARTIFACT_ID, DataStreamsApp.class);

        Set<ArtifactRange> parents = ImmutableSet.of(
                new ArtifactRange(NamespaceId.DEFAULT.toId(), DATASTREAMS_ARTIFACT_ID.getArtifact(),
                        new ArtifactVersion(DATASTREAMS_ARTIFACT_ID.getVersion()), true,
                        new ArtifactVersion(DATASTREAMS_ARTIFACT_ID.getVersion()), true)
        );

        //dumpClasspath(Class.forName("com.test.cdap.plugins.streamingsource.dmaap.DMaapStreamSource").getClassLoader());

        addPluginArtifact(NamespaceId.DEFAULT.artifact("spark-plugins", "1.0.0"), parents,
                DMaapStreamSource.class, DMaaPStreamingConfig.class,
                MockHttpReceiver.class, FastHttpReceiver.class);


    }

    @AfterClass
    public static void cleanup() {

    }

    public static void dumpClasspath(ClassLoader classLoader) {
        LOG.error(" !!!!!!!!!!!!! Dumping ClassPath for classloader: {} !!!!!!!!!!!!!", classLoader);
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader) classLoader;
            LOG.error(Joiner.on("\r\n").join(ucl.getURLs()));
            //LOG.error("\t ==========>>>" + Arrays.toString(ucl.getURLs()));
        } else {
            LOG.error("\t(cannot display components as not a URLClassLoader)");
        }
        if (classLoader.getParent() != null) {
            dumpClasspath(classLoader.getParent());
        }
    }

    @Test
    public void testDMaapStreamingSource() throws Exception {

        LOG.error("************************ START LOADING **********************");
        dumpClasspath(getClass().getClassLoader());
        LOG.error("************************ END LOADING **********************");

        Map<String, String> properties = new HashMap<>();
        properties.put("dmaapHostName", "dmaapHostName");
        properties.put("dmaapTopicName", "dmaapTopicName");
        properties.put("dmaapProtocol", "dmaapProtocol");
        properties.put("dmaapUserName", "dmaapUserName");
        properties.put("dmaapUserPassword", "dmaapUserPassword");
        properties.put("dmaapContentType", "dmaapContentType");
        properties.put("dmaapConsumerId", "dmaapConsumerId");
        properties.put("dmaapConsumerGroup", "dmaapConsumerGroup");
        properties.put("dmaapTimeoutMS", "100");
        properties.put("dmaapMessageLimit", "100");
        properties.put("interval", "1");
        properties.put("charset", "charset");
        properties.put("readTimeout", "1000");

        DataStreamsConfig dmaapPipeline = DataStreamsConfig.builder()
                .addStage(new ETLStage("source", new ETLPlugin(
                        "DMaapStream", StreamingSource.PLUGIN_TYPE, properties, null)))
                .addStage(new ETLStage("sink", MockSink.getPlugin("dmaapOutput")))
                .addConnection("source", "sink")
                .setBatchInterval("2s")
                .build();

        AppRequest<DataStreamsConfig> appRequest = new AppRequest<>(
                DATASTREAMS_ARTIFACT, dmaapPipeline);
        ApplicationId appId = NamespaceId.DEFAULT.app("DMaaPTestingApp");

        ApplicationManager appManager = null;
        try {
            appManager = deployApplication(appId.toId(), appRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        SparkManager sparkManager = appManager.getSparkManager(DataStreamsSparkLauncher.NAME);
        sparkManager.start();
        sparkManager.waitForStatus(true, 3, 5);

        final DataSetManager<Table> outputManager = getDataset("dmaapOutput");
        final Set<String> dmaapContents = new HashSet<>();

        Tasks.waitFor(true, new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        outputManager.flush();
                        for (StructuredRecord record : MockSink.readOutput(outputManager)) {
                            dmaapContents.add((String) record.get("MESSAGE"));
                        }
                        return dmaapContents.size() >= 2;
                    }
                },
                1, TimeUnit.MINUTES);

        sparkManager.stop();

        String allMessages = com.google.common.base.Joiner.on(",").join(dmaapContents);
        Assert.assertTrue(allMessages.contains("country"));
        Assert.assertTrue(allMessages.contains("country"));

    }
}
