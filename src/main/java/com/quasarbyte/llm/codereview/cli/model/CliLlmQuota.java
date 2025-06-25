package com.quasarbyte.llm.codereview.cli.model;

/**
 * Model representing the request quota for the LLM plugin.
 * <p>
 * This class is used to store and manage the request limit (quota)
 * applied when working with an LLM (Large Language Model) in the
 * CodeReview Maven plugin.
 * </p>
 */
public class CliLlmQuota {

    /**
     * The maximum number of allowed requests (quota).
     */
    private Long requestQuota;

    /**
     * Gets the configured request quota.
     *
     * @return the maximum number of allowed requests (quota)
     */
    public Long getRequestQuota() {
        return requestQuota;
    }

    /**
     * Sets the request quota.
     *
     * @param requestQuota the maximum number of allowed requests (quota)
     * @return the current {@link CliLlmQuota} instance for method chaining
     */
    public CliLlmQuota setRequestQuota(Long requestQuota) {
        this.requestQuota = requestQuota;
        return this;
    }
}
