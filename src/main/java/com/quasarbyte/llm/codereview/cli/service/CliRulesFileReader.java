package com.quasarbyte.llm.codereview.cli.service;

import com.quasarbyte.llm.codereview.cli.model.CliRule;

import java.util.List;

public interface CliRulesFileReader {
    List<CliRule> readPRules(String filePath) throws Exception;
}
