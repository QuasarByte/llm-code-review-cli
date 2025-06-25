package com.quasarbyte.llm.codereview.cli.exception;

public class LlmCodeReviewCliException extends RuntimeException {
    public LlmCodeReviewCliException(String message) {
        super(message);
    }

    public LlmCodeReviewCliException(String message, Throwable cause) {
        super(message, cause);
    }
}
