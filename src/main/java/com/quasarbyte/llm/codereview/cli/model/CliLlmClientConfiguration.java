package com.quasarbyte.llm.codereview.cli.model;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Configuration for the LLM client library.
 * <p>
 * This class contains settings required for connecting to and interacting with the
 * Large Language Model (LLM) service, such as API keys, endpoints, authentication parameters,
 * connection timeouts, and other client-specific options.
 * </p>
 */
public class CliLlmClientConfiguration {
    /**
     * Checks if the Jackson library version is compatible with the required features.
     * <p>
     * If set to {@code true}, the plugin will validate Jackson version compatibility.
     * </p>
     */
    private Boolean checkJacksonVersionCompatibility;

    /**
     * Enables or disables the validation of responses returned by the API.
     * <p>
     * If set to {@code true}, responses will be validated for correctness.
     * </p>
     */
    private Boolean responseValidation;

    /**
     * Timeout duration for API requests, specified as a {@link java.time.Duration}.
     * <p>
     * Example: <code>PT30S</code> for 30 seconds.
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-">Duration.parse documentation</a>
     * for details on the ISO-8601 format.
     * </p>
     */
    private Duration timeoutDuration;

    /**
     * The maximum number of retry attempts for failed requests.
     */
    private Integer maxRetries;

    /**
     * HTTP headers to be included with API requests.
     * <p>
     * Each header name maps to a list of header values.
     * </p>
     */
    private Map<String, List<String>> headersMap;

    /**
     * Query parameters to be included with API requests.
     * <p>
     * Each parameter name maps to a list of parameter values.
     * </p>
     */
    private Map<String, List<String>> queryParamsMap;

    /**
     * Proxy configuration for API requests.
     */
    private CliProxy proxy;

    /**
     * The API key used for authenticating API requests.
     */
    private String apiKey;

    /**
     * The version of the Azure service to be used for requests.
     */
    private String azureServiceVersion;

    /**
     * The base URL for the API.
     */
    private String baseUrl;

    /**
     * The organization name or identifier associated with the API requests.
     */
    private String organization;

    /**
     * The project name or identifier associated with the API requests.
     */
    private String project;

    public Boolean getCheckJacksonVersionCompatibility() {
        return checkJacksonVersionCompatibility;
    }

    public CliLlmClientConfiguration setCheckJacksonVersionCompatibility(Boolean checkJacksonVersionCompatibility) {
        this.checkJacksonVersionCompatibility = checkJacksonVersionCompatibility;
        return this;
    }

    public Boolean getResponseValidation() {
        return responseValidation;
    }

    public CliLlmClientConfiguration setResponseValidation(Boolean responseValidation) {
        this.responseValidation = responseValidation;
        return this;
    }

    public Duration getTimeoutDuration() {
        return timeoutDuration;
    }

    public CliLlmClientConfiguration setTimeoutDuration(Duration timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
        return this;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public CliLlmClientConfiguration setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public Map<String, List<String>> getHeadersMap() {
        return headersMap;
    }

    public CliLlmClientConfiguration setHeadersMap(Map<String, List<String>> headersMap) {
        this.headersMap = headersMap;
        return this;
    }

    public Map<String, List<String>> getQueryParamsMap() {
        return queryParamsMap;
    }

    public CliLlmClientConfiguration setQueryParamsMap(Map<String, List<String>> queryParamsMap) {
        this.queryParamsMap = queryParamsMap;
        return this;
    }

    public CliProxy getProxy() {
        return proxy;
    }

    public CliLlmClientConfiguration setProxy(CliProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    public CliLlmClientConfiguration setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getAzureServiceVersion() {
        return azureServiceVersion;
    }

    public CliLlmClientConfiguration setAzureServiceVersion(String azureServiceVersion) {
        this.azureServiceVersion = azureServiceVersion;
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public CliLlmClientConfiguration setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public String getOrganization() {
        return organization;
    }

    public CliLlmClientConfiguration setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public String getProject() {
        return project;
    }

    public CliLlmClientConfiguration setProject(String project) {
        this.project = project;
        return this;
    }
}
