package com.test.cdap.plugins.batchsink;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/27/2017.
 */
public final class Constants {

    /**
     * Common Reference Name property name and description
     */
    public static class Reference {
        public static final String REFERENCE_NAME = "referenceName";
        public static final String REFERENCE_NAME_DESCRIPTION = "This will be used to uniquely identify this source/sink " +
                "for lineage, annotating metadata, etc.";
    }

    public static final String EXTERNAL_DATASET_TYPE = "externalDataset";

    private Constants() {
    }
}