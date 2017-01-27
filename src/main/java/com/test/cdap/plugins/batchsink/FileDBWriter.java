package com.test.cdap.plugins.batchsink;

import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public class FileDBWriter<K, V> extends RecordWriter<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(FileDBWriter.class);
    private final File destinatioFile;

    public FileDBWriter() throws IOException {
        LOG.error("new instance of DB created");
        destinatioFile = new File("filedb.txt");
        LOG.error("File create at path ==> " + destinatioFile.getAbsolutePath());
    }

    @Override
    public void write(K k, V v) throws IOException, InterruptedException {
        LOG.error("FileDBWriter.write -> Ganesh -> " + v.toString());
        try (BufferedWriter writer = Files.newBufferedWriter(destinatioFile.toPath(),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(v.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        LOG.error("FileDBWriter.close");
    }
}
