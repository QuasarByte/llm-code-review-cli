package com.quasarbyte.llm.codereview.cli.service.parser;

import com.quasarbyte.llm.codereview.cli.model.CliRule;

import java.util.List;

public interface CliRulesXmlParser {
    List<CliRule> parseRules(String xml) throws Exception;
}
