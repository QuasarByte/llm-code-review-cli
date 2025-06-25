package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliRhinoConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.configuration.LlmMessagesMapperConfigurationRhino;

public interface CliRhinoConfigurationMapper {
    LlmMessagesMapperConfigurationRhino map(CliRhinoConfiguration configuration);
}
