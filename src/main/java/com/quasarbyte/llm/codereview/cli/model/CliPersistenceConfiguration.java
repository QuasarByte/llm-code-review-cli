package com.quasarbyte.llm.codereview.cli.model;

public class CliPersistenceConfiguration {
    private CliDataSourceConfiguration dataSourceConfiguration;
    private Boolean persistFileContent;

    public CliDataSourceConfiguration getDataSourceConfiguration() {
        return dataSourceConfiguration;
    }

    public CliPersistenceConfiguration setDataSourceConfiguration(CliDataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        return this;
    }

    public Boolean getPersistFileContent() {
        return persistFileContent;
    }

    public CliPersistenceConfiguration setPersistFileContent(Boolean persistFileContent) {
        this.persistFileContent = persistFileContent;
        return this;
    }
}
