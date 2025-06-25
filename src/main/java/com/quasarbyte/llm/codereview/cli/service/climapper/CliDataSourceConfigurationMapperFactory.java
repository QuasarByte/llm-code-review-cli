package com.quasarbyte.llm.codereview.cli.service.climapper;

/**
 * Factory interface for creating CliDataSourceConfigurationMapper instances
 */
public interface CliDataSourceConfigurationMapperFactory {

    /**
     * Creates a new CliDataSourceConfigurationMapper instance
     *
     * @return configured CliDataSourceConfigurationMapper instance
     */
    CliDataSourceConfigurationMapper create();
}
