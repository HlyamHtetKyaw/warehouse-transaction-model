package com.aplusbinary.binarypixor.doc.model;

/**
 * Enum to track the source of invoice ingestion for audit purposes.
 * This helps differentiate between single uploads and batch uploads.
 */
public enum IngestSource {
    SINGLE_UPLOAD("Single File Upload"),
    BATCH_UPLOAD("Batch File Upload"),
    API_INTEGRATION("API Integration"),
    MANUAL_ENTRY("Manual Entry"),
    OTHER("Other");

    private final String description;

    IngestSource(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
