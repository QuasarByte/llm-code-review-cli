package com.quasarbyte.llm.codereview.cli.service.climapper;

/**
 * Factory interface for creating CliPersistenceConfigurationMapper instances
 */
public interface CliPersistenceConfigurationMapperFactory {

    /**
     * Creates a new CliPersistenceConfigurationMapper instance
     *
     * @return configured CliPersistenceConfigurationMapper instance
     */
    CliPersistenceConfigurationMapper create();
}
