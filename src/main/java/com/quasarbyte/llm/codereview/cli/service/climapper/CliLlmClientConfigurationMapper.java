package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliLlmClientConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.configuration.LlmClientConfiguration;

import java.util.List;

public interface CliLlmClientConfigurationMapper {
    LlmClientConfiguration map(CliLlmClientConfiguration configuration);
    List<LlmClientConfiguration> map(List<CliLlmClientConfiguration> configurations);
}
