package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.quasarbyte.llm.codereview.cli.service.climapper.CliDataSourceConfigurationMapper;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliDataSourceConfigurationMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of CliDataSourceConfigurationMapperFactory
 */
public class CliDataSourceConfigurationMapperFactoryImpl implements CliDataSourceConfigurationMapperFactory {

    private static final Logger logger = LoggerFactory.getLogger(CliDataSourceConfigurationMapperFactoryImpl.class);

    @Override
    public CliDataSourceConfigurationMapper create() {
        logger.debug("Instantiating CliDataSourceConfigurationMapper");
        return new CliDataSourceConfigurationMapperImpl();
    }
}
