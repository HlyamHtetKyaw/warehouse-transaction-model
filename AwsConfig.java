package com.aplusbinary.binarypixor.doc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsConfig {

    private S3 s3 = new S3();
    private Credentials credentials = new Credentials();

    public S3 getS3() {
        return s3;
    }

    public void setS3(S3 s3) {
        this.s3 = s3;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    // Convenience methods for backward compatibility
    public String getAccessKeyId() {
        return credentials.getAccessKey();
    }

    public void setAccessKeyId(String accessKeyId) {
        this.credentials.setAccessKey(accessKeyId);
    }

    public String getSecretAccessKey() {
        return credentials.getSecretKey();
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.credentials.setSecretKey(secretAccessKey);
    }

    public static class Credentials {
        private String accessKey;
        private String secretKey;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static class S3 {
        private String bucketName;
        private String region;

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}
