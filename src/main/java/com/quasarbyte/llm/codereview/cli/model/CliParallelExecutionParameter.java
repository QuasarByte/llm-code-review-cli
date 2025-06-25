package com.quasarbyte.llm.codereview.cli.model;

/**
 * Configuration parameters for parallel execution.
 */
public class CliParallelExecutionParameter {
    /**
     * The number of items to process in a single batch during parallel execution.
     * <p>
     * If {@code null} or less than or equal to zero, batching 1 will be used.
     * </p>
     */
    private Integer batchSize;

    /**
     * The size of the thread pool to use for parallel processing.
     * <p>
     * If {@code null} or less than or equal to zero, the 1 pool size will be used.
     * </p>
     */
    private Integer poolSize;

    public Integer getBatchSize() {
        return batchSize;
    }

    public CliParallelExecutionParameter setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public CliParallelExecutionParameter setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
        return this;
    }
}
