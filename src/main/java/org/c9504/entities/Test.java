package org.c9504.entities;

import java.time.Instant;
import java.util.UUID;

public class Test {

    private UUID id;
    private String state;
    private String message;
    private Long createdAt;

    public Test() {
    }

    public Test(UUID id, String state, String message, Long createdAt) {
        this.id = id;
        this.state = state;
        this.message = message;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

}
