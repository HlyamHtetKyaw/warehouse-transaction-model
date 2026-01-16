package com.aplusbinary.binarypixor.doc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tesseract")
public class TesseractConfig {

    private Data data = new Data();
    private Language language = new Language();

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    // Convenience methods for backward compatibility
    public String getDataPath() {
        return data.getPath();
    }

    public void setDataPath(String dataPath) {
        this.data.setPath(dataPath);
    }

    public String getLanguageCode() {
        return language.getCode();
    }

    public void setLanguageCode(String languageCode) {
        this.language.setCode(languageCode);
    }

    public static class Data {
        private String path = "/usr/share/tesseract-ocr/4.00/tessdata";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Language {
        private String code = "eng";

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
