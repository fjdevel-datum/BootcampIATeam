package com.datum.redsoft.model.llama;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LlamaChoice {
    @JsonProperty("message")
    private LlamaMessage message;
    
    @JsonProperty("finish_reason")
    private String finishReason;
    
    @JsonProperty("index")
    private Integer index;

    // Constructor vacío
    public LlamaChoice() {}

    // Getters y Setters
    public LlamaMessage getMessage() {
        return message;
    }

    public void setMessage(LlamaMessage message) {
        this.message = message;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}