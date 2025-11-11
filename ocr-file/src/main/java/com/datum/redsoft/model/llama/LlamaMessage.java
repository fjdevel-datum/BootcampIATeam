package com.datum.redsoft.model.llama;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LlamaMessage {
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("content")
    private String content;

    // Constructor vacío
    public LlamaMessage() {}

    // Constructor
    public LlamaMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Getters y Setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}