package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.quasarbyte.llm.codereview.cli.service.climapper.CliDataSourceConfigurationMapperFactory;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliPersistenceConfigurationMapper;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliPersistenceConfigurationMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of CliPersistenceConfigurationMapperFactory
 */
public class CliPersistenceConfigurationMapperFactoryImpl implements CliPersistenceConfigurationMapperFactory {

    private static final Logger logger = LoggerFactory.getLogger(CliPersistenceConfigurationMapperFactoryImpl.class);

    private final CliDataSourceConfigurationMapperFactory dataSourceConfigurationMapperFactory;

    public CliPersistenceConfigurationMapperFactoryImpl(CliDataSourceConfigurationMapperFactory dataSourceConfigurationMapperFactory) {
        this.dataSourceConfigurationMapperFactory = dataSourceConfigurationMapperFactory;
        logger.debug("CliPersistenceConfigurationMapperFactoryImpl initialized with dependencies.");
    }

    @Override
    public CliPersistenceConfigurationMapper create() {
        logger.debug("Instantiating CliPersistenceConfigurationMapper");
        return new CliPersistenceConfigurationMapperImpl(dataSourceConfigurationMapperFactory.create());
    }
}
