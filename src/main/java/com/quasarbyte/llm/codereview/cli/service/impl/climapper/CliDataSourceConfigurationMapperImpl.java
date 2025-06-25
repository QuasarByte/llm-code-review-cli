package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasarbyte.llm.codereview.cli.model.CliDataSourceConfiguration;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliDataSourceConfigurationMapper;
import com.quasarbyte.llm.codereview.sdk.model.datasource.DataSourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliDataSourceConfigurationMapperImpl implements CliDataSourceConfigurationMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliDataSourceConfigurationMapperImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DataSourceConfiguration map(CliDataSourceConfiguration cliDataSourceConfiguration) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Mapping CliDataSourceConfiguration: {}", objectMapper.writeValueAsString(cliDataSourceConfiguration));
            } catch (Exception e) {
                logger.warn("Failed to serialize CliDataSourceConfiguration for debug logging: {}", e.getMessage());
            }
        }

        if (cliDataSourceConfiguration == null) {
            logger.warn("Provided CliDataSourceConfiguration is null, returning null.");
            return null;
        }

        DataSourceConfiguration result = new DataSourceConfiguration();
        result.setJdbcUrl(cliDataSourceConfiguration.getJdbcUrl());
        result.setUsername(cliDataSourceConfiguration.getUsername());
        result.setPassword(cliDataSourceConfiguration.getPassword());
        result.setDriverClassName(cliDataSourceConfiguration.getDriverClassName());
        result.setProperties(cliDataSourceConfiguration.getProperties());

        logger.info("Mapped CliDataSourceConfiguration to DataSourceConfiguration: jdbcUrl='{}', username='{}', driverClassName='{}'",
                result.getJdbcUrl(), result.getUsername(), result.getDriverClassName());

        return result;
    }
}
