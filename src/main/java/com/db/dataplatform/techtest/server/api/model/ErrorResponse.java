package com.db.dataplatform.techtest.server.api.model;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
    private final int status;
    private final String message;
    private Map<String, String> violations = new HashMap<>();

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void addFieldError(String path, String message) {
        violations.put(path, message);
    }

    public Map<String, String> getViolations() {
        return violations;
    }

}
