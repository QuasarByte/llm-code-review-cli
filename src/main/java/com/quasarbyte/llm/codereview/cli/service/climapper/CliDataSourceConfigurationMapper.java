package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliDataSourceConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.datasource.DataSourceConfiguration;

public interface CliDataSourceConfigurationMapper {
    DataSourceConfiguration map(CliDataSourceConfiguration cliDataSourceConfiguration);
}
