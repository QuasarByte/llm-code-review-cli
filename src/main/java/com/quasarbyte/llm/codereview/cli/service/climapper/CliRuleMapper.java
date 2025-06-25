package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.sdk.model.parameter.Rule;

public interface CliRuleMapper {
    Rule map(CliRule rule);
}
