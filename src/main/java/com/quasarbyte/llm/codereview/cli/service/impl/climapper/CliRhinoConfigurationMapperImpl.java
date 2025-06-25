package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasarbyte.llm.codereview.cli.exception.LlmCodeReviewCliException;
import com.quasarbyte.llm.codereview.cli.exception.ValidationException;
import com.quasarbyte.llm.codereview.cli.model.CliRhinoConfiguration;
import com.quasarbyte.llm.codereview.cli.service.ResourceLoader;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliRhinoConfigurationMapper;
import com.quasarbyte.llm.codereview.sdk.model.configuration.LlmMessagesMapperConfigurationRhino;
import com.quasarbyte.llm.codereview.sdk.service.LlmMessMapperRhinoConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CliRhinoConfigurationMapperImpl implements CliRhinoConfigurationMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliRhinoConfigurationMapperImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final LlmMessMapperRhinoConfigRepository llmMessMapperRhinoConfigRepository;
    private final ResourceLoader resourceLoader;

    public CliRhinoConfigurationMapperImpl(LlmMessMapperRhinoConfigRepository llmMessMapperRhinoConfigRepository,
                                           ResourceLoader resourceLoader) {
        this.llmMessMapperRhinoConfigRepository = llmMessMapperRhinoConfigRepository;
        this.resourceLoader = resourceLoader;
        logger.debug("PRhinoConfigurationMapperImpl initialized with LlmMessMapperRhinoConfigRepository and ResourceLoader.");
    }

    @Override
    public LlmMessagesMapperConfigurationRhino map(CliRhinoConfiguration configuration) {

        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Mapping CliRhinoConfiguration: {}", objectMapper.writeValueAsString(configuration));
            } catch (Exception e) {
                logger.warn("Failed to serialize CliRhinoConfiguration for debug logging: {}", e.getMessage());
            }
        }

        if (configuration != null) {

            if (notNullOrBlank(configuration.getScriptFilePath()) && notNullOrBlank(configuration.getFunctionName())) {
                logger.info("Mapping CliRhinoConfiguration with scriptFilePath='{}' and functionName='{}'.",
                        configuration.getScriptFilePath(), configuration.getFunctionName());

                LlmMessagesMapperConfigurationRhino result = new LlmMessagesMapperConfigurationRhino();

                String scriptFilePath = configuration.getScriptFilePath();
                String scriptBody;

                try {
                    scriptBody = resourceLoader.load(scriptFilePath);
                    logger.debug("Loaded script body from '{}', length={}", scriptFilePath, scriptBody != null ? scriptBody.length() : 0);
                } catch (IOException e) {
                    logger.error("Failed to load script from '{}': {}", scriptFilePath, e.getMessage(), e);
                    throw new LlmCodeReviewCliException(String.format("Can not read script body by file path '%s', error: '%s'", scriptFilePath, e.getMessage()), e);
                }

                result.setScriptBody(scriptBody);
                result.setFunctionName(configuration.getFunctionName());

                logger.info("Successfully mapped CliRhinoConfiguration with functionName='{}'.", configuration.getFunctionName());
                return result;

            } else {
                logger.error("Validation failed: Script file path and function name can not be blank.");
                throw new ValidationException("Script file path and function name can not be blank.");
            }

        } else {
            logger.info("PRhinoConfiguration is null, loading default configuration from repository.");
            return llmMessMapperRhinoConfigRepository.findDefaultConfiguration();
        }
    }

    private static boolean nullOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private static boolean notNullOrBlank(String string) {
        return !nullOrBlank(string);
    }
}
