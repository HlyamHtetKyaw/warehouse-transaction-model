package com.aplusbinary.binarypixor.doc.service.impl;

import com.aplusbinary.binarypixor.doc.config.AwsConfig;
import com.aplusbinary.binarypixor.doc.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class S3StorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(AwsConfig awsConfig) {
        this.bucketName = awsConfig.getS3().getBucketName();

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                awsConfig.getAccessKeyId(),
                awsConfig.getSecretAccessKey()
        );

        this.s3Client = S3Client.builder()
                .region(Region.of(awsConfig.getS3().getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        logger.info("S3 Storage initialized with bucket: {}", bucketName);
    }

    @Override
    public String storeFile(MultipartFile file, String filename) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        String key = "invoices/" + UUID.randomUUID() + "_" + filename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            logger.info("File uploaded to S3: {}/{}", bucketName, key);
            return key;
        } catch (S3Exception e) {
            logger.error("Failed to upload file to S3", e);
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public String storeFile(File file, String filename) throws IOException {
        if (!file.exists() || file.length() == 0) {
            throw new IOException("Failed to store empty or non-existent file");
        }

        String key = "invoices/" + UUID.randomUUID() + "_" + filename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(file.length())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

            logger.info("File uploaded to S3: {}/{}", bucketName, key);
            return key;
        } catch (S3Exception e) {
            logger.error("Failed to upload file to S3", e);
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public File retrieveFile(String filePath) throws IOException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            // Create temp file
            File tempFile = File.createTempFile("s3-download-", ".tmp");
            tempFile.deleteOnExit();

            s3Client.getObject(getObjectRequest, tempFile.toPath());

            logger.info("File downloaded from S3: {}/{}", bucketName, filePath);
            return tempFile;
        } catch (S3Exception e) {
            logger.error("Failed to download file from S3", e);
            throw new IOException("Failed to download file from S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("File deleted from S3: {}/{}", bucketName, filePath);
        } catch (S3Exception e) {
            logger.error("Failed to delete file from S3", e);
            throw new IOException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            logger.error("Error checking file existence in S3", e);
            return false;
        }
    }
}
