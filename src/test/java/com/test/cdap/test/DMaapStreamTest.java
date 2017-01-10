package com.test.cdap.test;

import co.cask.cdap.api.artifact.ArtifactVersion;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.dataset.table.Table;
import co.cask.cdap.common.utils.Tasks;
import co.cask.cdap.datapipeline.DataPipelineApp;
import co.cask.cdap.datastreams.DataStreamsApp;
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
import co.cask.cdap.proto.id.ArtifactId;
import co.cask.cdap.proto.id.NamespaceId;
import co.cask.cdap.test.ApplicationManager;
import co.cask.cdap.test.DataSetManager;
import co.cask.cdap.test.TestConfiguration;
import com.test.cdap.DMaapStreamConfig;
import com.test.cdap.DMaapStreamSource;
import com.google.common.collect.ImmutableSet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by cdap on 10/18/16.
 */

/*public class DMaapStreamTest {

} */

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

        addPluginArtifact(NamespaceId.DEFAULT.artifact("cdap-plugin", "1.0"), parents, DMaapStreamSource.class,
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

        DataStreamsConfig pipelineConfig = DataStreamsConfig.builder()
                .addStage(new ETLStage("source", new ETLPlugin("DMaapStream", StreamingSource.PLUGIN_TYPE, properties, null)))
                .addStage(new ETLStage("sink", MockSink.getPlugin("httpOutput")))
                .addConnection("source", "sink")
                .setBatchInterval("1s")
                .build();

        AppRequest<DataStreamsConfig> appRequest = new AppRequest<>(DATASTREAMS_ARTIFACT, pipelineConfig);

        Id.Application appId = Id.Application.from(Id.Namespace.DEFAULT, "DMaapSourceApp");
        ApplicationManager appManager = deployApplication(appId, appRequest);

        final DataSetManager<Table> outputManager = getDataset("httpOutput");
        Tasks.waitFor(
                true,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        outputManager.flush();
                        Set<String> contents = new HashSet<>();
                        for (StructuredRecord record : MockSink.readOutput(outputManager)) {
                            contents.add((String) record.get("MESSAGE"));
                        }
                        return contents.size() == 1 && contents.contains(deserialiezJSON);
                    }
                },
                4,
                TimeUnit.MINUTES);
    }
}
