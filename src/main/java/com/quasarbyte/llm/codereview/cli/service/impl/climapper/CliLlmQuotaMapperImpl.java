package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliLlmQuota;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliLlmQuotaMapper;
import com.quasarbyte.llm.codereview.sdk.model.parameter.LlmQuota;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliLlmQuotaMapperImpl implements CliLlmQuotaMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliLlmQuotaMapperImpl.class);

    @Override
    public LlmQuota map(CliLlmQuota quota) {
        logger.info("Mapping CliLlmQuota: {}", quota != null ? format(quota) : "null");
        if (quota == null) {
            logger.info("Provided CliLlmQuota is null, returning null.");
            return null;
        } else {
            logger.debug("Mapping requestQuota: {}", quota.getRequestQuota());
            LlmQuota result = new LlmQuota().setRequestQuota(quota.getRequestQuota());
            logger.info("Mapped CliLlmQuota to LlmQuota with requestQuota: {}", result.getRequestQuota());
            return result;
        }
    }

    private static String format(CliLlmQuota quota) {
        return "PLlmQuota{" +
                "requestQuota=" + quota.getRequestQuota() +
                '}';
    }
}
