package com.test.cdap.plugins.batchsink;

import java.util.regex.Pattern;

/**
 * Utility class for Id related operations.
 */
public final class IdUtils {

    private IdUtils() {
    }

    private static final Pattern datasetIdPattern = Pattern.compile("[$\\.a-zA-Z0-9_-]+");

    public static void validateId(String id) throws IllegalArgumentException {
        if (!datasetIdPattern.matcher(id).matches()) {
            throw new IllegalArgumentException(String.format("%s is not a valid id. Allowed characters are letters, " +
                    "numbers, and _, -, ., or $.", id));
        }
    }
}
