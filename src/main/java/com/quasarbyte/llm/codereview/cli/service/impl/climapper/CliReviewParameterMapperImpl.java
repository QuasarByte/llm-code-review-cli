package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasarbyte.llm.codereview.cli.exception.LlmCodeReviewCliException;
import com.quasarbyte.llm.codereview.cli.exception.ValidationException;
import com.quasarbyte.llm.codereview.cli.model.CliReviewParameter;
import com.quasarbyte.llm.codereview.cli.model.CliReviewTarget;
import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.cli.service.CliRulesFileReader;
import com.quasarbyte.llm.codereview.cli.service.climapper.*;
import com.quasarbyte.llm.codereview.sdk.model.configuration.LlmChatCompletionConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.configuration.LlmMessagesMapperConfigurationRhino;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ReviewParameter;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ReviewTarget;
import com.quasarbyte.llm.codereview.sdk.model.parameter.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CliReviewParameterMapperImpl implements CliReviewParameterMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliReviewParameterMapperImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final CliLlmQuotaMapper quotaMapper;
    private final CliReviewTargetMapper cliReviewTargetMapper;
    private final CliRhinoConfigurationMapper rhinoConfigurationMapper;
    private final CliRuleMapper cliRuleMapper;
    private final CliRulesFileReader rulesFileReader;

    public CliReviewParameterMapperImpl(CliLlmQuotaMapper quotaMapper,
                                        CliReviewTargetMapper cliReviewTargetMapper,
                                        CliRhinoConfigurationMapper rhinoConfigurationMapper,
                                        CliRuleMapper cliRuleMapper,
                                        CliRulesFileReader rulesFileReader) {
        this.quotaMapper = quotaMapper;
        this.cliReviewTargetMapper = cliReviewTargetMapper;
        this.rhinoConfigurationMapper = rhinoConfigurationMapper;
        this.cliRuleMapper = cliRuleMapper;
        this.rulesFileReader = rulesFileReader;
        logger.debug("CliReviewParameterMapperImpl initialized with dependencies.");
    }

    @Override
    public ReviewParameter map(CliReviewParameter parameter) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Mapping CliReviewParameter: {}", objectMapper.writeValueAsString(parameter));
            } catch (Exception e) {
                logger.warn("Failed to serialize CliReviewParameter for debug logging: {}", e.getMessage());
            }
        }

        if (parameter == null) {
            logger.warn("Provided CliReviewParameter is null, returning null.");
            return null;
        }
        ReviewParameter result = new ReviewParameter();
        result.setReviewName(parameter.getReviewName());

        final List<CliRule> allCliRules = new ArrayList<>();

        if (parameter.getRulesFilePaths() != null && !parameter.getRulesFilePaths().isEmpty()) {
            parameter.getRulesFilePaths().forEach(path -> {
                final List<CliRule> cliRules;
                if (notNullOrBlank(path)) {
                    try {
                        logger.info("Reading Rules from file: '{}'", path);
                        cliRules = rulesFileReader.readPRules(path);
                        logger.debug("Read {} CliRules from '{}'", cliRules != null ? cliRules.size() : 0, path);
                    } catch (Exception e) {
                        logger.error("Failed to read CliRules from '{}': {}", path, e.getMessage(), e);
                        throw new LlmCodeReviewCliException(String.format("Can not read file: '%s', error message: '%s'", path, e.getMessage()), e);
                    }
                } else {
                    logger.warn("Skipped blank or null rulesFilePath.");
                    cliRules = Collections.emptyList();
                }

                if (cliRules != null) {
                    allCliRules.addAll(cliRules);
                }

            });
        }

        if (parameter.getRules() != null) {
            logger.debug("Adding {} inline CliRules from parameter.", parameter.getRules().size());
            allCliRules.addAll(parameter.getRules());
        }

        result.setRules(mapRules(allCliRules));
        result.setTargets(mapTargets(parameter.getTargets()));
        result.setSystemPrompts(parameter.getSystemPrompts());
        result.setReviewPrompts(parameter.getReviewPrompts());

        LlmChatCompletionConfiguration llmChatCompletionConfiguration = parameter.getLlmChatCompletionConfiguration();

        if (llmChatCompletionConfiguration == null) {
            logger.error("reviewParameter.llmChatCompletionConfiguration is not set.");
            throw new ValidationException("reviewParameter.llmChatCompletionConfiguration is not set.");
        }

        if (nullOrBlank(llmChatCompletionConfiguration.getModel())) {
            logger.error("reviewParameter.llmChatCompletionConfiguration.model is not set.");
            throw new ValidationException("reviewParameter.llmChatCompletionConfiguration.model is not set.");
        }

        result.setLlmChatCompletionConfiguration(llmChatCompletionConfiguration);
        result.setLlmMessagesMapperConfiguration(getRhinoConfiguration(parameter));
        result.setRulesBatchSize(parameter.getRulesBatchSize());
        result.setTimeoutDuration(parseDuration(parameter.getTimeoutDuration()));
        result.setLlmQuota(quotaMapper.map(parameter.getLlmQuota()));
        result.setUseReasoning(parameter.getUseReasoning());

        logger.info("Successfully mapped CliReviewParameter to ReviewParameter: reviewName='{}', total rules={}, total targets={}",
                result.getReviewName(),
                allCliRules.size(),
                result.getTargets() != null ? result.getTargets().size() : 0);

        return result;
    }

    private List<Rule> mapRules(List<CliRule> rules) {
        if (rules == null) {
            logger.debug("No CliRules to map.");
            return Collections.emptyList();
        }
        logger.debug("Mapping {} CliRules to Rules.", rules.size());
        return rules.stream()
                .map(cliRuleMapper::map)
                .collect(Collectors.toList());
    }

    private List<ReviewTarget> mapTargets(List<CliReviewTarget> targets) {
        if (targets == null) {
            logger.debug("No targets to map.");
            return Collections.emptyList();
        }
        logger.debug("Mapping {} CliReviewTargets to ReviewTargets.", targets.size());
        return targets.stream()
                .map(cliReviewTargetMapper::map)
                .collect(Collectors.toList());
    }

    private LlmMessagesMapperConfigurationRhino getRhinoConfiguration(CliReviewParameter parameter) {
        return rhinoConfigurationMapper.map(parameter.getRhinoConfiguration());
    }

    private static Duration parseDuration(String duration) {
        if (duration == null) {
            return null;
        }
        try {
            return Duration.parse(duration);
        } catch (Exception e) {
            logger.error("Failed to parse duration '{}': {}", duration, e.getMessage());
            throw new LlmCodeReviewCliException("Can not parse duration, see 'https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-', error: " + e.getMessage(), e);
        }
    }

    private static boolean nullOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private static boolean notNullOrBlank(String string) {
        return !nullOrBlank(string);
    }
}
