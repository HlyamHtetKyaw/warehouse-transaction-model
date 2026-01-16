package com.aplusbinary.binarypixor.doc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIConfig {

    private Api api = new Api();
    private Model model = new Model();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    // Convenience methods for backward compatibility
    public String getApiKey() {
        return api.getKey();
    }

    public void setApiKey(String apiKey) {
        this.api.setKey(apiKey);
    }

    public String getModelName() {
        return model.getName();
    }

    public void setModelName(String modelName) {
        this.model.setName(modelName);
    }

    public static class Api {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class Model {
        private String name = "gpt-3.5-turbo";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
