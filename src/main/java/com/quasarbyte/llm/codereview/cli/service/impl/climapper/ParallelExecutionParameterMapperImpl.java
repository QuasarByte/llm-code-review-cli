package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliParallelExecutionParameter;
import com.quasarbyte.llm.codereview.cli.service.climapper.ParallelExecutionParameterMapper;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ParallelExecutionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelExecutionParameterMapperImpl implements ParallelExecutionParameterMapper {

    private static final Logger logger = LoggerFactory.getLogger(ParallelExecutionParameterMapperImpl.class);

    @Override
    public ParallelExecutionParameter map(CliParallelExecutionParameter parameter) {
        logger.info("Mapping CliParallelExecutionParameter: {}", format(parameter));

        Integer batchSize = (parameter.getBatchSize() != null && parameter.getBatchSize() > 0)
                ? parameter.getBatchSize()
                : 1;
        if (parameter.getBatchSize() == null || parameter.getBatchSize() <= 0) {
            logger.warn("Provided batchSize is null or non-positive ({}). Defaulting to 1.", parameter.getBatchSize());
        } else {
            logger.debug("Using provided batchSize: {}", batchSize);
        }

        int poolSize = (parameter.getPoolSize() != null && parameter.getPoolSize() > 0)
                ? parameter.getPoolSize()
                : 1;
        if (parameter.getPoolSize() == null || parameter.getPoolSize() <= 0) {
            logger.warn("Provided poolSize is null or non-positive ({}). Defaulting to 1.", parameter.getPoolSize());
        } else {
            logger.debug("Using provided poolSize: {}", poolSize);
        }

        logger.info("Creating ExecutorService with poolSize: {}", poolSize);
        ExecutorService executorService = Executors.newWorkStealingPool(poolSize);

        ParallelExecutionParameter pep = new ParallelExecutionParameter()
                .setBatchSize(batchSize)
                .setExecutorService(executorService);

        logger.info("Mapped ParallelExecutionParameter: batchSize={}, executorService={}", batchSize, executorService);
        return pep;
    }

    private static String format(CliParallelExecutionParameter parameter) {
        return "PParallelExecutionParameter{" +
                "batchSize=" + parameter.getBatchSize() +
                ", poolSize=" + parameter.getPoolSize() +
                '}';
    }
}
