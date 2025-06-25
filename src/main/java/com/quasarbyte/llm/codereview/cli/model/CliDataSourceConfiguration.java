package com.quasarbyte.llm.codereview.cli.model;

import java.util.Map;

public class CliDataSourceConfiguration {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private Map<String, String> properties;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public CliDataSourceConfiguration setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public CliDataSourceConfiguration setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CliDataSourceConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public CliDataSourceConfiguration setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public CliDataSourceConfiguration setProperties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }
}
