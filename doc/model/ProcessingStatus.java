package com.aplusbinary.binarypixor.doc.model;

public enum ProcessingStatus {
    PENDING,
    UPLOADING,
    OCR_PROCESSING,
    NLP_PARSING,
    VALIDATING,
    COMPLETED,
    COMPLETED_WITH_WARNINGS, // New: Processing succeeded but S3/storage failed
    FAILED
}

