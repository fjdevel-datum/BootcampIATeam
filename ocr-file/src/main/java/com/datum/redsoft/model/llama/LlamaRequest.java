package com.datum.redsoft.model.llama;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class LlamaRequest {
    
    @JsonProperty("messages")
    private List<LlamaMessage> messages;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("stream")
    private boolean stream;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    @JsonProperty("temperature")
    private Double temperature;

    // Constructor vacío
    public LlamaRequest() {}

    // Constructor
    public LlamaRequest(List<LlamaMessage> messages, String model, boolean stream, Integer maxTokens, Double temperature) {
        this.messages = messages;
        this.model = model;
        this.stream = stream;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    // Getters y Setters
    public List<LlamaMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<LlamaMessage> messages) {
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}