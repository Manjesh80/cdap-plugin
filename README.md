Build
-----
To build your plugins:

    mvn clean compile -DskipTests

    In IntelliJ debug DMaapStreamTest.testDMaapStreamingSource


Deployment
----------
You can deploy your plugins using the CDAP CLI:

    > load artifact <target/plugin.jar> config-file <target/plugin.json>

For example, if your artifact is named 'my-plugins-1.0.0':

    > load artifact target/my-plugins-1.0.0.jar config-file target/my-plugins-1.0.0.json
