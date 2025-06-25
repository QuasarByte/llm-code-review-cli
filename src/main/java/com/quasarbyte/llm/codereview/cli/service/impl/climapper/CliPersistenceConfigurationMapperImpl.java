package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasarbyte.llm.codereview.cli.model.CliPersistenceConfiguration;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliDataSourceConfigurationMapper;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliPersistenceConfigurationMapper;
import com.quasarbyte.llm.codereview.sdk.model.parameter.PersistenceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliPersistenceConfigurationMapperImpl implements CliPersistenceConfigurationMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliPersistenceConfigurationMapperImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final CliDataSourceConfigurationMapper dataSourceConfigurationMapper;

    public CliPersistenceConfigurationMapperImpl(CliDataSourceConfigurationMapper dataSourceConfigurationMapper) {
        this.dataSourceConfigurationMapper = dataSourceConfigurationMapper;
        logger.debug("CliPersistenceConfigurationMapperImpl initialized with dependencies.");
    }

    @Override
    public PersistenceConfiguration map(CliPersistenceConfiguration cliPersistenceConfiguration) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Mapping CliPersistenceConfiguration: {}", objectMapper.writeValueAsString(cliPersistenceConfiguration));
            } catch (Exception e) {
                logger.warn("Failed to serialize CliPersistenceConfiguration for debug logging: {}", e.getMessage());
            }
        }

        if (cliPersistenceConfiguration == null) {
            logger.warn("Provided CliPersistenceConfiguration is null, returning null.");
            return null;
        }

        PersistenceConfiguration result = new PersistenceConfiguration();
        result.setDataSourceConfiguration(dataSourceConfigurationMapper.map(cliPersistenceConfiguration.getDataSourceConfiguration()));
        result.setPersistFileContent(cliPersistenceConfiguration.getPersistFileContent());

        logger.info("Mapped CliPersistenceConfiguration to PersistenceConfiguration: persistFileContent='{}', dataSourceConfiguration='{}'",
                result.getPersistFileContent(),
                result.getDataSourceConfiguration() != null ? "configured" : "null");

        return result;
    }
}
