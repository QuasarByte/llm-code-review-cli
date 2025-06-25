package com.quasarbyte.llm.codereview.cli.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.cli.service.parser.CliRulesJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class CliRulesJsonParserImpl implements CliRulesJsonParser {

    private static final Logger logger = LoggerFactory.getLogger(CliRulesJsonParserImpl.class);

    @Override
    public List<CliRule> parseRules(String json) throws Exception {
        if (json == null || json.trim().isEmpty()) {
            logger.warn("Input JSON for CliRules parsing is null or empty.");
            return Collections.emptyList();
        }

        logger.debug("Parsing CliRules from JSON string of length {}.", json.length());
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<CliRule> rules = mapper.readValue(json, new TypeReference<List<CliRule>>() {
            });
            logger.info("Parsed {} CliRules from JSON input.", rules.size());
            return rules;
        } catch (Exception e) {
            logger.error("Failed to parse CliRules from JSON: {}", e.getMessage(), e);
            throw e;
        }
    }
}
