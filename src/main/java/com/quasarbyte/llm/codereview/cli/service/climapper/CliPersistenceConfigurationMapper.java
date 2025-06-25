package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliPersistenceConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.parameter.PersistenceConfiguration;

public interface CliPersistenceConfigurationMapper {
    PersistenceConfiguration map(CliPersistenceConfiguration cliPersistenceConfiguration);
}
