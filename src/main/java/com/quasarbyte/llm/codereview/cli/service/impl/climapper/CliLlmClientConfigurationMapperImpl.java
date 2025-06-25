package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.quasarbyte.llm.codereview.cli.exception.ValidationException;
import com.quasarbyte.llm.codereview.cli.model.CliLlmClientConfiguration;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliLlmClientConfigurationMapper;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliProxyMapper;
import com.quasarbyte.llm.codereview.sdk.model.configuration.LlmClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CliLlmClientConfigurationMapperImpl implements CliLlmClientConfigurationMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliLlmClientConfigurationMapperImpl.class);

    private final CliProxyMapper proxyMapper;

    public CliLlmClientConfigurationMapperImpl(CliProxyMapper proxyMapper) {
        this.proxyMapper = proxyMapper;
        logger.debug("CliLlmClientConfigurationMapperImpl initialized with CliProxyMapper.");
    }

    @Override
    public LlmClientConfiguration map(CliLlmClientConfiguration configuration) {
        logger.info("Mapping CliLlmClientConfiguration: {}", configuration);
        if (configuration == null) {
            logger.warn("Provided CliLlmClientConfiguration is null, returning null.");
            return null;
        }

        String baseUrl = configuration.getBaseUrl();

        if (nullOrBlank(baseUrl)) {
            logger.error("Validation failed: llmClientConfiguration.baseUrl is not set.");
            throw new ValidationException("llmClientConfiguration.baseUrl is not set.");
        }

        LlmClientConfiguration result = new LlmClientConfiguration();

        result.setCheckJacksonVersionCompatibility(configuration.getCheckJacksonVersionCompatibility());
        logger.debug("Set checkJacksonVersionCompatibility: {}", configuration.getCheckJacksonVersionCompatibility());

        result.setResponseValidation(configuration.getResponseValidation());
        logger.debug("Set responseValidation: {}", configuration.getResponseValidation());

        result.setTimeoutDuration(configuration.getTimeoutDuration());
        logger.debug("Set timeoutDuration: {}", configuration.getTimeoutDuration());

        result.setMaxRetries(configuration.getMaxRetries());
        logger.debug("Set maxRetries: {}", configuration.getMaxRetries());

        result.setHeadersMap(convertMap(configuration.getHeadersMap()));
        logger.debug("Set headersMap: {}", configuration.getHeadersMap());

        result.setQueryParamsMap(convertMap(configuration.getQueryParamsMap()));
        logger.debug("Set queryParamsMap: {}", configuration.getQueryParamsMap());

        result.setProxy(proxyMapper.map(configuration.getProxy()));
        logger.debug("Set proxy: {}", configuration.getProxy());

        result.setApiKey(configuration.getApiKey());
        logger.debug("Set apiKey: {}", configuration.getApiKey() != null ? "[PROVIDED]" : "[NOT PROVIDED]");

        result.setAzureServiceVersion(configuration.getAzureServiceVersion());
        logger.debug("Set azureServiceVersion: {}", configuration.getAzureServiceVersion());

        result.setBaseUrl(configuration.getBaseUrl());
        logger.debug("Set baseUrl: {}", configuration.getBaseUrl());

        result.setOrganization(configuration.getOrganization());
        logger.debug("Set organization: {}", configuration.getOrganization());

        result.setProject(configuration.getProject());
        logger.debug("Set project: {}", configuration.getProject());

        logger.info("Successfully mapped CliLlmClientConfiguration to LlmClientConfiguration with baseUrl: '{}'", result.getBaseUrl());
        return result;
    }

    @Override
    public List<LlmClientConfiguration> map(List<CliLlmClientConfiguration> configurations) {
        Objects.requireNonNull(configurations, "configurations is null.");
        return configurations.stream().map(this::map).collect(Collectors.toList());
    }

    private static Map<String, Iterable<String>> convertMap(Map<String, List<String>> map) {
        if (map == null) {
            logger.debug("convertMap: input map is null, returning null.");
            return null;
        }
        logger.debug("convertMap: converting map with {} entries.", map.size());
        return new HashMap<>(map);
    }

    private static boolean nullOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private static boolean notNullOrBlank(String string) {
        return !nullOrBlank(string);
    }
}
