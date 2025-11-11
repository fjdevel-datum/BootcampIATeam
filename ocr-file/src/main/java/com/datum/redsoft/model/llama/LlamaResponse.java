package com.datum.redsoft.model.llama;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class LlamaResponse {
    
    @JsonProperty("choices")
    private List<LlamaChoice> choices;
    
    @JsonProperty("usage")
    private LlamaUsage usage;
    
    @JsonProperty("model")
    private String model;

    // Constructor vacío
    public LlamaResponse() {}

    // Getters y Setters
    public List<LlamaChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<LlamaChoice> choices) {
        this.choices = choices;
    }

    public LlamaUsage getUsage() {
        return usage;
    }

    public void setUsage(LlamaUsage usage) {
        this.usage = usage;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}