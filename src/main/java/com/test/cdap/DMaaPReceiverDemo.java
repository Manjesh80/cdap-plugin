package com.test.cdap;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;
import org.apache.commons.io.FileUtils;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/24/2017.
 */
public class DMaaPReceiverDemo extends Receiver<StructuredRecord> {

    private static final long serialVersionUID = -7114956462480794482L;
    private static final Logger LOG = LoggerFactory.getLogger(DMaaPReceiverDemo.class);
    private final DMaaPStreamingConfig conf;

    private static final Schema OUTPUT_SCHEMA =
            Schema.recordOf("outputSchema",
                    Schema.Field.of("MESSAGE_NUM", Schema.of(Schema.Type.STRING)),
                    Schema.Field.of("MESSAGE", Schema.of(Schema.Type.STRING)));

    public DMaaPReceiverDemo(StorageLevel storageLevel, DMaaPStreamingConfig conf) {
        super(storageLevel);
        try {
            File classpathDetails = new File("classpathDetails.txt");
            classpathDetails.delete();
            classpathDetails.createNewFile();
            System.out.println("Class Details in ==> " + classpathDetails.getAbsolutePath());
            printArtifactsOfClassLoaderRecusively(this.getClass().getClassLoader(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.conf = conf;
    }

    public DMaaPStreamingConfig getConf() {
        return conf;
    }

    //region Standard methods override from CDAP Receiver Class
    @Override
    public StorageLevel storageLevel() {
        return StorageLevel.MEMORY_ONLY();
    }

    @Override
    public void onStart() {
        new Thread() {
            @Override
            public void run() {
                AtomicInteger serialNumber = new AtomicInteger();

                while (!isStopped()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);

                        StructuredRecord recordForNow = StructuredRecord.builder(OUTPUT_SCHEMA).
                                set("MESSAGE_NUM", Long.toString(System.currentTimeMillis())).
                                set("MESSAGE", "Message " + Integer.toString(serialNumber.incrementAndGet()))
                                .build();

                        store(recordForNow);

                    } catch (Exception e) {
                        LOG.error("Error getting content from {}.", e);
                    }
                }
            }

            @Override
            public void interrupt() {
                super.interrupt();
            }
        }.start();
    }

    @Override
    public void onStop() {

    }

    //endregion

    private void printArtifactsOfClassLoaderRecusively(ClassLoader contextClassLoader, boolean printClasses) throws Exception {


        if (contextClassLoader instanceof URLClassLoader) {

            String classLoaderName = contextClassLoader.toString();
            //writeLog(contextClassLoader.toString() + " ** IS OF TYPE URLClassLoader **");
            URLClassLoader ucl = (URLClassLoader) contextClassLoader;
            URL[] jars = ucl.getURLs();

            for (URL jar : jars) {
                String jarPath = URLDecoder.decode(jar.toString(), "UTF-8");
                String jarName = jarPath.substring(jarPath.lastIndexOf("/"));
                StringBuilder classNames = new StringBuilder();

                //classNames.append(" ++ Classes in JAR file ==> " + jar.toString()).append("\r\n");

                if (printClasses) {
                    jarPath = jarPath.contains("file:/") ? jarPath.replace("file:/", "") : jarPath;
                    try (ZipInputStream jarStream = new ZipInputStream(new FileInputStream(jarPath))) {
                        for (ZipEntry entry = jarStream.getNextEntry();
                             entry != null; entry = jarStream.getNextEntry()) {
                            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                                // This ZipEntry represents a class. Now, what class does it represent?
                                String className = entry.getName().replace('/', '.'); // including ".class"
                                className = classLoaderName + " ==> " + jarPath + " ==> " +
                                        className.substring(0, className.length() - ".class".length());
                                classNames.append(className).append("\r\n");
                            }
                        }
                    } catch (RuntimeException runtimeException) {
                        classNames.append("\r\n    ++++").append("!!!! ERROR " + runtimeException.getMessage());
                    } catch (Exception exception) {
                        classNames.append("\r\n    ++++").append("!!!! ERROR " + exception.getMessage());
                    }
                }
                writeLog(classNames.toString());
                //writeLog(classNames.toString());
            }
            //writeLog(Joiner.on("\r\n").join(ucl.getURLs()));
        } else {
            //writeLog(contextClassLoader.toString() + " !!!  IS NOT OF TYPE URLClassLoader !!!");
        }

        if (contextClassLoader.getParent() != null) {
            //writeLog(contextClassLoader.toString() + " ** PARENT IS NOT NULL ** " + contextClassLoader.getParent().toString());
            printArtifactsOfClassLoaderRecusively(contextClassLoader.getParent(), printClasses);
        } else {
            //writeLog(contextClassLoader.toString() + " !!! PARENT IS  NULL !!! ");
        }
    }

    private void writeLog(String message) {
        File classpathDetails = new File("classpathDetails.txt");
        try {
            FileUtils.writeStringToFile(classpathDetails, message + "\r\n", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
