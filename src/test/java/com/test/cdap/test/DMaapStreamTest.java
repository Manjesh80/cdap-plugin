package com.test.cdap.test;

import co.cask.cdap.api.artifact.ArtifactVersion;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.dataset.table.Table;
import co.cask.cdap.common.utils.Tasks;
import co.cask.cdap.datapipeline.DataPipelineApp;
import co.cask.cdap.datastreams.DataStreamsApp;
import co.cask.cdap.datastreams.DataStreamsSparkLauncher;
import co.cask.cdap.etl.api.streaming.StreamingSource;
import co.cask.cdap.etl.mock.batch.MockSink;
import co.cask.cdap.etl.mock.test.HydratorTestBase;
import co.cask.cdap.etl.proto.v2.DataStreamsConfig;
import co.cask.cdap.etl.proto.v2.ETLPlugin;
import co.cask.cdap.etl.proto.v2.ETLStage;
import co.cask.cdap.proto.Id;
import co.cask.cdap.proto.artifact.AppRequest;
import co.cask.cdap.proto.artifact.ArtifactRange;
import co.cask.cdap.proto.artifact.ArtifactSummary;
import co.cask.cdap.proto.id.ApplicationId;
import co.cask.cdap.proto.id.ArtifactId;
import co.cask.cdap.proto.id.NamespaceId;
import co.cask.cdap.test.ApplicationManager;
import co.cask.cdap.test.DataSetManager;
import co.cask.cdap.test.SparkManager;
import co.cask.cdap.test.TestConfiguration;
import com.test.cdap.DMaapStreamConfig;
import com.test.cdap.DMaapStreamSource;
import com.google.common.collect.ImmutableSet;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * Created by cdap on 10/18/16.
 */

public class DMaapStreamTest extends HydratorTestBase {

    @ClassRule
    public static final TestConfiguration CONFIG = new TestConfiguration("explore.enabled", false);

    protected static final ArtifactId DATAPIPELINE_ARTIFACT_ID = NamespaceId.DEFAULT.artifact("data-pipeline", "3.2.0");
    protected static final ArtifactSummary DATAPIPELINE_ARTIFACT = new ArtifactSummary("data-pipeline", "3.2.0");

    protected static final ArtifactId DATASTREAMS_ARTIFACT_ID = NamespaceId.DEFAULT.artifact("data-streams", "3.2.0");
    protected static final ArtifactSummary DATASTREAMS_ARTIFACT = new ArtifactSummary("data-streams", "3.2.0");

    @ClassRule
    public static TemporaryFolder tmpFolder = new TemporaryFolder();

    @BeforeClass
    public static void setupTest() throws Exception {
        // add the artifact for data pipeline app
        setupBatchArtifacts(DATAPIPELINE_ARTIFACT_ID, DataPipelineApp.class);

        setupStreamingArtifacts(DATASTREAMS_ARTIFACT_ID, DataStreamsApp.class);

        // add artifact for spark plugins
        Set<ArtifactRange> parents = ImmutableSet.of(
                new ArtifactRange(NamespaceId.DEFAULT.toId(), DATAPIPELINE_ARTIFACT_ID.getArtifact(),
                        new ArtifactVersion(DATAPIPELINE_ARTIFACT_ID.getVersion()), true,
                        new ArtifactVersion(DATAPIPELINE_ARTIFACT_ID.getVersion()), true),
                new ArtifactRange(NamespaceId.DEFAULT.toId(), DATASTREAMS_ARTIFACT_ID.getArtifact(),
                        new ArtifactVersion(DATASTREAMS_ARTIFACT_ID.getVersion()), true,
                        new ArtifactVersion(DATASTREAMS_ARTIFACT_ID.getVersion()), true)
        );

        addPluginArtifact(NamespaceId.DEFAULT.artifact("spark-plugins", "1.0.0"), parents, DMaapStreamSource.class,
                DMaapStreamConfig.class);
    }

    @AfterClass
    public static void cleanup() {

    }

    @Test
    public void testDMaapStreamingSource() throws Exception {

        final String deserialiezJSON = "Deserialized JSON";

        Map<String, String> properties = new HashMap<String,String>();
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
                        return dmaapContents.size() == 4;
                    }
                },
                1, TimeUnit.MINUTES);

        Assert.assertThat(dmaapContents, contains("Message 2"));
        Assert.assertThat(dmaapContents, contains("Message 3"));
        sparkManager.stop();


    }
}
