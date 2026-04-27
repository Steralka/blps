package ru.blps.googleplay.exception;

import java.time.OffsetDateTime;

public class ApiError {

    private final String message;
    private final OffsetDateTime timestamp;

    public ApiError(String message) {
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
