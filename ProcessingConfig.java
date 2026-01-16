package com.aplusbinary.binarypixor.doc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.processing")
public class ProcessingConfig {

    private Temp temp = new Temp();
    private Cleanup cleanup = new Cleanup();

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public Cleanup getCleanup() {
        return cleanup;
    }

    public void setCleanup(Cleanup cleanup) {
        this.cleanup = cleanup;
    }

    // Convenience methods for backward compatibility
    public String getTempDirectory() {
        return temp.getDirectory();
    }

    public void setTempDirectory(String tempDirectory) {
        this.temp.setDirectory(tempDirectory);
    }

    public int getCleanupAfterHours() {
        return cleanup.getHours();
    }

    public void setCleanupAfterHours(int cleanupAfterHours) {
        this.cleanup.setHours(cleanupAfterHours);
    }

    public static class Temp {
        private String directory;

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }
    }

    public static class Cleanup {
        private int hours = 24;

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }
    }
}
