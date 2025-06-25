package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.quasarbyte.llm.codereview.cli.exception.LlmCodeReviewCliException;
import com.quasarbyte.llm.codereview.cli.model.CliFileGroup;
import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.cli.service.CliRulesFileReader;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliFileGroupMapper;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliRuleMapper;
import com.quasarbyte.llm.codereview.sdk.model.parameter.FileGroup;
import com.quasarbyte.llm.codereview.sdk.model.parameter.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CliFileGroupMapperImpl implements CliFileGroupMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliFileGroupMapperImpl.class);

    private final CliRuleMapper cliRuleMapper;
    private final CliRulesFileReader rulesFileReader;

    public CliFileGroupMapperImpl(CliRuleMapper cliRuleMapper, CliRulesFileReader rulesFileReader) {
        this.cliRuleMapper = cliRuleMapper;
        this.rulesFileReader = rulesFileReader;
        logger.debug("PFileGroupMapperImpl initialized with CliRuleMapper and CliRulesFileReader.");
    }

    @Override
    public FileGroup map(CliFileGroup fileGroup) {
        logger.info("Mapping CliFileGroup: {}", fileGroup != null ? fileGroup.getFileGroupName() : "null");
        if (fileGroup == null) {
            logger.warn("Provided CliFileGroup is null, returning null.");
            return null;
        }

        final List<CliRule> allCliRules = new ArrayList<>();

        if (fileGroup.getRulesFilePaths() != null && !fileGroup.getRulesFilePaths().isEmpty()) {
            logger.debug("Processing rulesFilePaths: {}", fileGroup.getRulesFilePaths());
            fileGroup.getRulesFilePaths().forEach(path -> {
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
        } else {
            logger.debug("No rulesFilePaths to process.");
        }

        if (fileGroup.getRules() != null) {
            logger.debug("Adding {} inline CliRules from fileGroup.", fileGroup.getRules().size());
            allCliRules.addAll(fileGroup.getRules());
        }

        FileGroup result = new FileGroup();
        result.setFileGroupName(fileGroup.getFileGroupName());
        result.setPaths(fileGroup.getPaths() != null ? fileGroup.getPaths() : Collections.emptyList());
        result.setExcludePaths(fileGroup.getExcludePaths() != null ? fileGroup.getExcludePaths() : Collections.emptyList());
        result.setFilesBatchSize(fileGroup.getFilesBatchSize());
        result.setRules(mapRules(allCliRules));
        result.setFileGroupPrompts(fileGroup.getFileGroupPrompts());
        result.setCodePage(fileGroup.getCodePage());

        logger.info("Mapped FileGroup '{}': {} total rules, {} paths.",
                result.getFileGroupName(),
                allCliRules.size(),
                result.getPaths() != null ? result.getPaths().size() : 0);

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

    private static boolean nullOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private static boolean notNullOrBlank(String string) {
        return !nullOrBlank(string);
    }
}
