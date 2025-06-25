package com.quasarbyte.llm.codereview.cli.service.parser;

import com.quasarbyte.llm.codereview.cli.model.CliRule;

import java.util.List;

public interface CliRulesJsonParser {
    List<CliRule> parseRules(String json) throws Exception;
}
