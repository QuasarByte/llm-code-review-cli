package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasarbyte.llm.codereview.cli.exception.LlmCodeReviewCliException;
import com.quasarbyte.llm.codereview.cli.model.CliReviewTarget;
import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.cli.service.CliRulesFileReader;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliFileGroupMapper;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliReviewTargetMapper;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliRuleMapper;
import com.quasarbyte.llm.codereview.sdk.model.parameter.FileGroup;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ReviewTarget;
import com.quasarbyte.llm.codereview.sdk.model.parameter.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CliReviewTargetMapperImpl implements CliReviewTargetMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliReviewTargetMapperImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final CliFileGroupMapper cliFileGroupMapper;
    private final CliRuleMapper cliRuleMapper;
    private final CliRulesFileReader rulesFileReader;

    public CliReviewTargetMapperImpl(CliFileGroupMapper cliFileGroupMapper, CliRuleMapper cliRuleMapper, CliRulesFileReader rulesFileReader) {
        this.cliFileGroupMapper = cliFileGroupMapper;
        this.cliRuleMapper = cliRuleMapper;
        this.rulesFileReader = rulesFileReader;
        logger.debug("CliReviewTargetMapperImpl initialized.");
    }

    @Override
    public ReviewTarget map(CliReviewTarget reviewTarget) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Mapping CliReviewTarget: {}", objectMapper.writeValueAsString(reviewTarget));
            } catch (Exception e) {
                logger.warn("Failed to serialize CliReviewTarget for debug logging: {}", e.getMessage());
            }
        }

        if (reviewTarget == null) {
            logger.warn("Provided CliReviewTarget is null, returning null.");
            return null;
        }

        ReviewTarget result = new ReviewTarget();
        result.setReviewTargetName(reviewTarget.getReviewTargetName());
        logger.debug("Set reviewTargetName: {}", reviewTarget.getReviewTargetName());

        result.setFileGroups(mapFileGroups(reviewTarget));
        logger.debug("Mapped file groups: {}", result.getFileGroups() != null ? result.getFileGroups().size() : 0);

        final List<CliRule> allCliRules = new ArrayList<>();

        if (reviewTarget.getRulesFilePaths() != null && !reviewTarget.getRulesFilePaths().isEmpty()) {
            logger.debug("Processing rulesFilePaths: {}", reviewTarget.getRulesFilePaths());
            reviewTarget.getRulesFilePaths().forEach(path -> {
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

        if (reviewTarget.getRules() != null) {
            logger.debug("Adding {} inline CliRules from reviewTarget.", reviewTarget.getRules().size());
            allCliRules.addAll(reviewTarget.getRules());
        }

        result.setRules(mapRules(allCliRules));
        logger.debug("Mapped total rules: {}", allCliRules.size());

        result.setReviewTargetPrompts(reviewTarget.getReviewTargetPrompts());
        logger.debug("Set reviewTargetPrompts: {}", reviewTarget.getReviewTargetPrompts());

        logger.info("Successfully mapped CliReviewTarget '{}' with {} fileGroups and {} rules.",
                result.getReviewTargetName(),
                result.getFileGroups() != null ? result.getFileGroups().size() : 0,
                allCliRules.size());

        return result;
    }

    private List<FileGroup> mapFileGroups(CliReviewTarget reviewTarget) {
        if (reviewTarget.getFileGroups() == null) {
            logger.debug("No file groups to map.");
            return Collections.emptyList();
        }
        logger.debug("Mapping {} file groups.", reviewTarget.getFileGroups().size());
        return reviewTarget.getFileGroups().stream()
                .map(cliFileGroupMapper::map)
                .collect(Collectors.toList());
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

    private static boolean nullOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private static boolean notNullOrBlank(String string) {
        return !nullOrBlank(string);
    }
}
