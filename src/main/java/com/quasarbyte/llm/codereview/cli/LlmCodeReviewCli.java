package com.quasarbyte.llm.codereview.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quasarbyte.llm.codereview.cli.exception.LlmCodeReviewCliException;
import com.quasarbyte.llm.codereview.cli.exception.ValidationException;
import com.quasarbyte.llm.codereview.cli.model.*;
import com.quasarbyte.llm.codereview.cli.service.CliRulesFileReader;
import com.quasarbyte.llm.codereview.cli.service.FileService;
import com.quasarbyte.llm.codereview.cli.service.ResourceLoader;
import com.quasarbyte.llm.codereview.cli.service.climapper.*;
import com.quasarbyte.llm.codereview.cli.service.impl.CliRulesFileReaderImpl;
import com.quasarbyte.llm.codereview.cli.service.impl.CliRulesJsonParserImpl;
import com.quasarbyte.llm.codereview.cli.service.impl.FileServiceImpl;
import com.quasarbyte.llm.codereview.cli.service.impl.climapper.*;
import com.quasarbyte.llm.codereview.cli.service.impl.parser.CliRulesXmlParserImpl;
import com.quasarbyte.llm.codereview.cli.service.impl.parser.ResourceLoaderImpl;
import com.quasarbyte.llm.codereview.cli.service.parser.CliRulesJsonParser;
import com.quasarbyte.llm.codereview.cli.service.parser.CliRulesXmlParser;
import com.quasarbyte.llm.codereview.sdk.model.configuration.LlmClientConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.parameter.LlmClient;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ParallelExecutionParameter;
import com.quasarbyte.llm.codereview.sdk.model.parameter.PersistenceConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ReviewParameter;
import com.quasarbyte.llm.codereview.sdk.model.review.ReviewResult;
import com.quasarbyte.llm.codereview.sdk.model.run.RunFailureConfiguration;
import com.quasarbyte.llm.codereview.sdk.model.statistics.SeverityStatistics;
import com.quasarbyte.llm.codereview.sdk.service.*;
import com.quasarbyte.llm.codereview.sdk.service.impl.*;
import com.quasarbyte.llm.codereview.sdk.service.report.csv.CodeReviewReportCsvService;
import com.quasarbyte.llm.codereview.sdk.service.report.csv.impl.CodeReviewReportCsvServiceFactoryImpl;
import com.quasarbyte.llm.codereview.sdk.service.report.html.CodeReviewReportHtmlService;
import com.quasarbyte.llm.codereview.sdk.service.report.html.impl.CodeReviewReportHtmlServiceFactoryImpl;
import com.quasarbyte.llm.codereview.sdk.service.report.markdown.CodeReviewReportMarkdownService;
import com.quasarbyte.llm.codereview.sdk.service.report.markdown.impl.CodeReviewReportMarkdownServiceFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(
        name = "llm-code-review",
        mixinStandardHelpOptions = true,
        version = "0.1.0",
        description = "Performs automated code review using Large Language Models (LLMs)"
)
public class LlmCodeReviewCli implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(LlmCodeReviewCli.class);

    @Option(names = "--review-params-file", required = true, description = "JSON file with review parameters.")
    private File reviewParamsFile;

    @Option(names = "--llm-client-config-file", required = true, description = "JSON file with client configuration.")
    private File llmClientConfigFile;

    @Option(names = {"--base-url-override"}, description = "Override base URL for the LLM service.")
    private String baseUrlOverride;

    @Option(names = "--api-key-override", description = "Override api-key for LLM client.")
    private String apiKeyOverride;

    @Option(names = "--llm-clients-config-file", description = "JSON file with clients configuration.")
    private File llmClientsConfigFile;

    @Option(names = {"--base-urls-override"}, description = "Override base URLs for the LLM service.", arity = "0..*")
    private List<String> baseUrlsOverride;

    @Option(names = "--api-keys-override", description = "Override api-keys for LLM client.", arity = "0..*")
    private List<String> apiKeysOverride;

    @Option(names = "--persistence-configuration-file", description = "JSON file with persistence configuration.")
    private File persistenceConfigurationFile;

    @Option(names = "--parallel-execution-parameter-file", description = "JSON file with parallel execution parameter.")
    private File parallelExecutionParameterFile;

    @Option(names = "--run-failure-configuration-file", description = "JSON file with run failure configuration.")
    private File runFailureConfigurationFile;

    @Option(names = "--json-report-file-path", description = "JSON report file path (STDOUT, STDERR, report.json).")
    private String jsonReportFilePath;

    @Option(names = "--markdown-report-file-path", description = "Markdown report file path (STDOUT, STDERR, report.md).")
    private String markdownReportFilePath;

    @Option(names = "--html-report-file-path", description = "HTML report file path (STDOUT, STDERR, report.html).")
    private String htmlReportFilePath;

    @Option(names = "--csv-report-file-path", description = "CSV report file path (STDOUT, STDERR, report.csv).")
    private String csvReportFilePath;

    private ObjectMapper objectMapper;

    private CliFileGroupMapper fileGroupMapper;
    private CliLlmClientConfigurationMapper clientConfigurationMapper;
    private CliLlmQuotaMapper quotaMapper;
    private CliPersistenceConfigurationMapper persistenceConfigurationMapper;
    private CliProxyMapper proxyMapper;
    private CliReviewParameterMapper reviewParameterMapper;
    private CliReviewTargetMapper reviewTargetMapper;
    private CliRhinoConfigurationMapper rhinoConfigurationMapper;
    private CliRuleMapper ruleMapper;
    private CliRulesFileReader rulesFileReader;
    private CliRulesJsonParser rulesJsonParser;
    private CliRulesXmlParser rulesXmlParser;
    private CodeReviewReportCsvService codeReviewReportCsvService;
    private CodeReviewReportHtmlService codeReviewReportHtmlService;
    private CodeReviewReportMarkdownService codeReviewReportMarkdownService;
    private FileService fileService;
    private LlmMessMapperRhinoConfigRepository llmMessMapperRhinoConfigRepository;
    private ParallelExecutionParameterMapper parallelExecutionParameterMapper;
    private ResourceLoader resourceLoader;
    private ReviewParallelExecutionService reviewParallelExecutionService;
    private ReviewService reviewService;
    private RunFailureChecker runFailureChecker;
    private SeverityStatisticsCalculator severityStatisticsCalculator;

    public static void main(String[] args) {
        configureLogging();

        int exitCode = new CommandLine(new LlmCodeReviewCli()).execute(args);
        System.exit(exitCode);
    }

    public LlmCodeReviewCli() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        proxyMapper = new CliProxyMapperImpl();
        clientConfigurationMapper = new CliLlmClientConfigurationMapperImpl(proxyMapper);
        CliDataSourceConfigurationMapperFactory dataSourceConfigurationMapperFactory = new CliDataSourceConfigurationMapperFactoryImpl();
        persistenceConfigurationMapper = new CliPersistenceConfigurationMapperFactoryImpl(dataSourceConfigurationMapperFactory).create();
        ruleMapper = new CliRuleMapperImpl();
        fileService = new FileServiceImpl();
        rulesJsonParser = new CliRulesJsonParserImpl();
        rulesXmlParser = new CliRulesXmlParserImpl();
        resourceLoader = new ResourceLoaderImpl();
        rulesFileReader = new CliRulesFileReaderImpl(fileService, rulesJsonParser, rulesXmlParser, resourceLoader);
        fileGroupMapper = new CliFileGroupMapperImpl(ruleMapper, rulesFileReader);
        llmMessMapperRhinoConfigRepository = new LlmMessMapperRhinoConfigRepositoryFactoryImpl().create();
        parallelExecutionParameterMapper = new ParallelExecutionParameterMapperImpl();
        quotaMapper = new CliLlmQuotaMapperImpl();
        reviewParallelExecutionService = new ReviewParallelExecutionServiceFactoryImpl().create();
        reviewTargetMapper = new CliReviewTargetMapperImpl(fileGroupMapper, ruleMapper, rulesFileReader);
        rhinoConfigurationMapper = new CliRhinoConfigurationMapperImpl(llmMessMapperRhinoConfigRepository, resourceLoader);
        reviewParameterMapper = new CliReviewParameterMapperImpl(quotaMapper, reviewTargetMapper, rhinoConfigurationMapper, ruleMapper, rulesFileReader);
        reviewService = new ReviewServiceFactoryImpl().create();
        runFailureChecker = new RunFailureCheckerFactoryImpl().create();
        severityStatisticsCalculator = new SeverityStatisticsCalculatorImpl();
        codeReviewReportMarkdownService = new CodeReviewReportMarkdownServiceFactoryImpl().create();
        codeReviewReportHtmlService = new CodeReviewReportHtmlServiceFactoryImpl().create();
        codeReviewReportCsvService = new CodeReviewReportCsvServiceFactoryImpl().create();
    }

    @Override
    public Integer call() throws Exception {
        logger.info("Starting LLM Code Review CLI execution...");

        // Create configuration objects from command line parameters
        CliReviewParameter reviewParameter = createReviewParameter();

        Optional<CliLlmClientConfiguration> llmClientConfigurationOptional = createLlmClientConfiguration();
        List<CliLlmClientConfiguration> llmClientsConfiguration = createLlmClientsConfiguration();

        if (llmClientConfigurationOptional.isPresent() && !llmClientsConfiguration.isEmpty()) {
            String message = "Provide either a single LLM Client configuration or a list, not both.";
            logger.error(message);
            throw new ValidationException(message);
        } else if (!llmClientConfigurationOptional.isPresent() && llmClientsConfiguration.isEmpty()) {
            String message = "LLM Client configuration is not provided.";
            logger.error(message);
            throw new ValidationException(message);
        }

        CliParallelExecutionParameter parallelExecutionParameter = createParallelExecutionParameter()
                .orElseGet(() -> new CliParallelExecutionParameter().setBatchSize(null).setPoolSize(null));

        CliRunFailureConfiguration runFailureConfiguration = createRunFailureConfiguration()
                .orElseGet(() -> new CliRunFailureConfiguration().setCriticalThreshold(1).setWarningThreshold(100));

        final String reviewParameterAsJson;
        final String llmClientConfigurationAsJson;
        final String llmClientsConfigurationAsJson;
        final String parallelExecutionParameterAsJson;
        final String buildFailureConfigurationAsJson;

        try {
            reviewParameterAsJson = objectMapper.writeValueAsString(reviewParameter);

            if (llmClientConfigurationOptional.isPresent()) {
                llmClientConfigurationAsJson = objectMapper.writeValueAsString(maskedLlmClientConfigurationCopy(llmClientConfigurationOptional.get()));
            } else {
                llmClientConfigurationAsJson = null;
            }

            llmClientsConfigurationAsJson = objectMapper.writeValueAsString(maskedLlmClientConfigurationCopy(llmClientsConfiguration));
            parallelExecutionParameterAsJson = objectMapper.writeValueAsString(parallelExecutionParameter);
            buildFailureConfigurationAsJson = objectMapper.writeValueAsString(runFailureConfiguration);
        } catch (JsonProcessingException e) {
            logger.error("Cannot serialize configuration to JSON: " + e.getMessage(), e);
            return 1;
        }

        PersistenceConfiguration persistenceConfiguration = createPersistenceConfiguration()
                .orElse(null);

        logger.debug("reviewParameter body:");
        logger.debug(reviewParameterAsJson);

        logger.debug("llmClientConfiguration body:");
        logger.debug(llmClientConfigurationAsJson);

        logger.debug("llmClientsConfiguration body:");
        logger.debug(llmClientsConfigurationAsJson);

        logger.debug("parallelExecutionParameter body:");
        logger.debug(parallelExecutionParameterAsJson);

        logger.debug("buildFailureConfiguration body:");
        logger.debug(buildFailureConfigurationAsJson);

        final ReviewParameter mappedRP;
        try {
            mappedRP = this.reviewParameterMapper.map(reviewParameter);
            logger.info("Mapped reviewParameter successfully.");
        } catch (Exception e) {
            logger.error("Failed to map reviewParameter: " + e.getMessage(), e);
            return 1;
        }

        final Optional<LlmClient> llmClient;
        final List<LlmClient> llmClients;

        if (llmClientConfigurationOptional.isPresent()) {
            LlmClientFactory llmClientFactory = new LlmClientFactoryImpl();
            LlmClientConfiguration clientConfiguration;
            try {
                clientConfiguration = clientConfigurationMapper.map(llmClientConfigurationOptional.get());
                logger.info("Mapped llmClientConfiguration successfully.");
            } catch (Exception e) {
                logger.error("Failed to map llmClientConfiguration: " + e.getMessage(), e);
                return 1;
            }

            llmClient = Optional.of(llmClientFactory.create(clientConfiguration));
            llmClients = Collections.emptyList();

        } else {

            LlmClientFactory llmClientFactory = new LlmClientFactoryImpl();
            List<LlmClientConfiguration> clientConfigurations;
            try {
                clientConfigurations = clientConfigurationMapper.map(llmClientsConfiguration);
                logger.info("Mapped llmClientConfiguration successfully.");
            } catch (Exception e) {
                logger.error("Failed to map llmClientConfiguration: " + e.getMessage(), e);
                return 1;
            }

            llmClient = Optional.empty();
            llmClients = llmClientFactory.create(clientConfigurations);
        }

        final ReviewResult result;

        try {
            if (parallelExecutionParameter == null || (parallelExecutionParameter.getBatchSize() == null && parallelExecutionParameter.getPoolSize() == null)) {
                logger.info("Executing review in single-threaded mode.");

                if (llmClient.isPresent()) {
                    result = reviewService.review(mappedRP, llmClient.get(), persistenceConfiguration);
                } else {

                    if (llmClients.size() == 1) {
                        result = reviewService.review(mappedRP, llmClients.get(0), persistenceConfiguration);
                    } else {
                        throw new ValidationException("More than one or zero LlmClients is present.");
                    }

                }

            } else {
                if (parallelExecutionParameter.getBatchSize() == null) {
                    logger.error("parallel execution parameter batch size is null");
                    throw new ValidationException("parallel execution parameter batch size is null");
                }
                if (parallelExecutionParameter.getPoolSize() == null) {
                    logger.error("parallel execution parameter pool size is null");
                    throw new ValidationException("parallel execution parameter pool size is null");
                }
                ParallelExecutionParameter executionParameter = parallelExecutionParameterMapper.map(parallelExecutionParameter);
                logger.info(String.format("Executing review in parallel mode. Batch size: %d, Pool size: %d", executionParameter.getBatchSize(), parallelExecutionParameter.getPoolSize()));

                if (llmClient.isPresent()) {
                    result = reviewParallelExecutionService.review(mappedRP, llmClient.get(), persistenceConfiguration, executionParameter);
                } else {
                    result = reviewParallelExecutionService.review(mappedRP, llmClients, persistenceConfiguration, executionParameter);
                }
            }
        } catch (Exception e) {
            logger.error("Failed during review execution: " + e.getMessage(), e);
            return 1;
        }

        String resultAsJson;
        try {
            resultAsJson = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            logger.error("Cannot serialize review result to JSON: " + e.getMessage(), e);
            return 1;
        }

        logger.info("Review result items size: " + result.getItems().size());
        logger.debug("Review result body:");
        logger.debug(resultAsJson);

        SeverityStatistics severityStatistics;
        try {
            severityStatistics = severityStatisticsCalculator.calculate(result);
            logger.info(String.format("Calculated severity statistics: InfoCount=%d, WarningCount=%d, CriticalCount=%d",
                    severityStatistics.getInfoCount(),
                    severityStatistics.getWarningCount(),
                    severityStatistics.getCriticalCount()));
        } catch (Exception e) {
            logger.error("Failed to calculate severity statistics: {}", e.getMessage(), e);
            return 1;
        }

        logger.info("Review result body:");
        logger.info(resultAsJson);

        createReports(result, resultAsJson);

        final boolean failRun;
        try {
            failRun = runFailureChecker.check(new RunFailureConfiguration()
                            .setWarningThreshold(runFailureConfiguration.getWarningThreshold())
                            .setCriticalThreshold(runFailureConfiguration.getCriticalThreshold()),
                    severityStatistics);
            if (failRun) {
                logger.warn("Run failure criteria met. Failing run.");
            } else {
                logger.info("Run passed failure check.");
            }
        } catch (Exception e) {
            logger.error("Failed during run failure check: " + e.getMessage(), e);
            return 1;
        }

        if (failRun) {
            logger.error(String.format("Run failed. Failure check did not pass. InfoCount: %d, WarningCount: %d, CriticalCount: %d",
                    severityStatistics.getInfoCount(),
                    severityStatistics.getWarningCount(),
                    severityStatistics.getCriticalCount()));
            return 1;
        }

        logger.info("LLM Code Review CLI execution finished successfully.");
        return 0;
    }

    private CliReviewParameter createReviewParameter() {
        CliReviewParameter cliReviewParameter;
        try {
            // Load review parameters from a file
            cliReviewParameter = objectMapper.readValue(readFile(reviewParamsFile), CliReviewParameter.class);
            logger.debug("Loaded review parameters from file: {}", reviewParamsFile.getAbsolutePath());
        } catch (JsonProcessingException e) {
            logger.error("Cannot deserialize review parameters: " + e.getMessage(), e);
            throw new LlmCodeReviewCliException("Cannot deserialize review parameters: " + e.getMessage(), e);
        }
        return cliReviewParameter;
    }

    private Optional<CliLlmClientConfiguration> createLlmClientConfiguration() {
        final Optional<CliLlmClientConfiguration> pLlmClientConfigurationOptional;
        final CliLlmClientConfiguration pLlmClientConfig;

        if (llmClientConfigFile != null) {

            try {
                // Load LLM client configuration from file
                pLlmClientConfig = objectMapper.readValue(readFile(llmClientConfigFile), CliLlmClientConfiguration.class);
                logger.debug("Loaded LLM client config from file: {}", llmClientConfigFile.getAbsolutePath());
            } catch (JsonProcessingException e) {
                logger.error("Cannot deserialize LLM client configuration: " + e.getMessage(), e);
                throw new LlmCodeReviewCliException("Cannot deserialize LLM client configuration: " + e.getMessage(), e);
            }

            // Override API key if provided
            if (apiKeyOverride != null && !apiKeyOverride.trim().isEmpty()) {
                pLlmClientConfig.setApiKey(apiKeyOverride);
                logger.info("API key overridden via command line.");
            }

            // Override base url if provided
            if (baseUrlOverride != null && !baseUrlOverride.trim().isEmpty()) {
                pLlmClientConfig.setBaseUrl(baseUrlOverride);
                logger.info("Base url overridden via command line.");
            }

            pLlmClientConfigurationOptional = Optional.of(pLlmClientConfig);

        } else {
            pLlmClientConfigurationOptional = Optional.empty();
        }


        return pLlmClientConfigurationOptional;
    }

    private List<CliLlmClientConfiguration> createLlmClientsConfiguration() {
        final List<CliLlmClientConfiguration> pLlmClientConfig;

        if (llmClientsConfigFile != null) {

            try {
                // Load LLM client configuration from file
                pLlmClientConfig = objectMapper.readValue(readFile(llmClientsConfigFile), new TypeReference<List<CliLlmClientConfiguration>>() {
                });

                logger.debug("Loaded LLM clients config from file: {}", llmClientConfigFile.getAbsolutePath());
            } catch (JsonProcessingException e) {
                logger.error("Cannot deserialize LLM clients configuration: " + e.getMessage(), e);
                throw new LlmCodeReviewCliException("Cannot deserialize LLM clients configuration: " + e.getMessage(), e);
            }

            // Override base urls if provided
            if (baseUrlsOverride != null && !baseUrlsOverride.isEmpty()) {
                if (baseUrlsOverride.size() != pLlmClientConfig.size()) {
                    throw new ValidationException("Invalid number of base urls provided, not equals to llm clients in llm-clients-config-file.");
                }
                for (int i = 0; i < pLlmClientConfig.size(); i++) {
                    pLlmClientConfig.get(i).setBaseUrl(baseUrlsOverride.get(i));
                }
                logger.info("Base urls overridden via command line.");
            }

            // Override api keys if provided
            if (apiKeysOverride != null && !apiKeysOverride.isEmpty()) {
                if (apiKeysOverride.size() != pLlmClientConfig.size()) {
                    throw new ValidationException("Invalid number of apiKeys provided, not equals to llm clients in llm-clients-config-file.");
                }
                for (int i = 0; i < pLlmClientConfig.size(); i++) {
                    pLlmClientConfig.get(i).setApiKey(apiKeysOverride.get(i));
                }
                logger.info("API keys overridden via command line.");
            }

        } else {
            pLlmClientConfig = Collections.emptyList();
        }

        return pLlmClientConfig;
    }

    private Optional<PersistenceConfiguration> createPersistenceConfiguration() {
        final CliPersistenceConfiguration cliPersistenceConfiguration;

        if (persistenceConfigurationFile != null) {

            try {
                cliPersistenceConfiguration = objectMapper.readValue(readFile(persistenceConfigurationFile), CliPersistenceConfiguration.class);
                logger.debug("Loaded CLI persistence configuration from file: {}", persistenceConfigurationFile.getAbsolutePath());
            } catch (JsonProcessingException e) {
                logger.error("Cannot deserialize CLI persistence configuration: " + e.getMessage(), e);
                throw new LlmCodeReviewCliException("Cannot deserialize CLI persistence configuration: " + e.getMessage(), e);
            }

            PersistenceConfiguration persistenceConfiguration;
            try {
                persistenceConfiguration = persistenceConfigurationMapper.map(cliPersistenceConfiguration);
                logger.info("Mapped CLI persistence configuration successfully.");

                return Optional.of(persistenceConfiguration);

            } catch (Exception e) {
                logger.error("Failed to map CLI persistence configuration: " + e.getMessage(), e);
                throw new LlmCodeReviewCliException("Failed to map CLI persistence configuration: " + e.getMessage(), e);
            }

        } else {
            return Optional.empty();
        }
    }

    private Optional<CliParallelExecutionParameter> createParallelExecutionParameter() {
        if (parallelExecutionParameterFile != null) {
            CliParallelExecutionParameter parallelExecutionParameter;
            try {
                parallelExecutionParameter = objectMapper.readValue(readFile(parallelExecutionParameterFile), CliParallelExecutionParameter.class);
                logger.debug("Loaded parallel execution parameter from file: {}", parallelExecutionParameterFile.getAbsolutePath());
            } catch (JsonProcessingException e) {
                logger.error("Cannot deserialize parallel execution parameter: " + e.getMessage(), e);
                throw new LlmCodeReviewCliException("Cannot deserialize parallel execution parameter: " + e.getMessage(), e);
            }
            return Optional.of(parallelExecutionParameter);

        } else {
            return Optional.empty();
        }
    }

    private Optional<CliRunFailureConfiguration> createRunFailureConfiguration() {
        if (runFailureConfigurationFile != null) {
            CliRunFailureConfiguration config;
            try {
                config = objectMapper.readValue(readFile(runFailureConfigurationFile), CliRunFailureConfiguration.class);
                logger.debug("Loaded build failure configuration from file: {}", parallelExecutionParameterFile.getAbsolutePath());
            } catch (JsonProcessingException e) {
                logger.error("Cannot deserialize run failure configuration: " + e.getMessage(), e);
                throw new LlmCodeReviewCliException("Cannot deserialize run failure configuration: " + e.getMessage(), e);
            }
            return Optional.of(config);
        } else {
            return Optional.empty();
        }
    }

    private void createReports(ReviewResult result, String resultAsJson) {
        if (jsonReportFilePath != null && !jsonReportFilePath.trim().isEmpty()) {
            createFile(resultAsJson.getBytes(StandardCharsets.UTF_8), jsonReportFilePath);
        }

        if (markdownReportFilePath != null && !markdownReportFilePath.trim().isEmpty()) {
            String reportBody = codeReviewReportMarkdownService.generateMarkdownReport(result);
            createFile(reportBody.getBytes(StandardCharsets.UTF_8), markdownReportFilePath);
        }

        if (htmlReportFilePath != null && !htmlReportFilePath.trim().isEmpty()) {
            String reportBody = codeReviewReportHtmlService.generateHtmlReport(result);
            createFile(reportBody.getBytes(StandardCharsets.UTF_8), htmlReportFilePath);
        }

        if (csvReportFilePath != null && !csvReportFilePath.trim().isEmpty()) {
            String reportBody = codeReviewReportCsvService.generateCsvReport(result);
            createFile(reportBody.getBytes(StandardCharsets.UTF_8), csvReportFilePath);
        }
    }

    private static void createFile(byte[] bytes, String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new ValidationException("FilePath cannot be null or empty.");
        }
        if (isStdOut(filePath)) {
            System.out.println(new String(bytes));
        } else if (isStdErr(filePath)) {
            System.err.println(new String(bytes));
        } else {
            try {
                Files.createDirectories(Paths.get(filePath).getParent());
                Files.write(Paths.get(filePath), bytes, StandardOpenOption.CREATE);
            } catch (IOException e) {
                logger.error("Failed to write result file: " + e.getMessage(), e);
                throw new LlmCodeReviewCliException("Failed to write result file: " + e.getMessage(), e);
            }
        }
    }

    private static boolean isStdOut(String filePath) {
        return "stdout".equalsIgnoreCase(filePath);
    }

    private static boolean isStdErr(String filePath) {
        return "stderr".equalsIgnoreCase(filePath);
    }

    // Utility method to read file content
    private static String readFile(File file) {
        try {
            return new String(java.nio.file.Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            logger.error("Error reading file: " + file.getAbsolutePath(), e);
            throw new LlmCodeReviewCliException("Failed to read file: " + file.getAbsolutePath(), e);
        }
    }

    private static List<CliLlmClientConfiguration> maskedLlmClientConfigurationCopy(List<CliLlmClientConfiguration> configurations) {
        return configurations.stream().map(LlmCodeReviewCli::maskedLlmClientConfigurationCopy).collect(Collectors.toList());
    }

    private static CliLlmClientConfiguration maskedLlmClientConfigurationCopy(CliLlmClientConfiguration configuration) {
        CliLlmClientConfiguration masked = new CliLlmClientConfiguration();
        masked.setCheckJacksonVersionCompatibility(configuration.getCheckJacksonVersionCompatibility())
                .setResponseValidation(configuration.getResponseValidation())
                .setTimeoutDuration(configuration.getTimeoutDuration())
                .setMaxRetries(configuration.getMaxRetries())
                .setHeadersMap(configuration.getHeadersMap())
                .setQueryParamsMap(configuration.getQueryParamsMap())
                .setProxy(configuration.getProxy())
                .setApiKey(configuration.getApiKey() == null ? null : "******")
                .setAzureServiceVersion(configuration.getAzureServiceVersion())
                .setBaseUrl(configuration.getBaseUrl())
                .setOrganization(configuration.getOrganization())
                .setProject(configuration.getProject());
        return masked;
    }

    private static void configureLogging() {
        // Check if the bridge is already installed
        if (!SLF4JBridgeHandler.isInstalled()) {
            // Remove existing handlers from the root logger
            java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
            java.util.logging.Handler[] handlers = rootLogger.getHandlers();
            for (java.util.logging.Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // Install the SLF4J bridge
            SLF4JBridgeHandler.install();

            // Set the level for the root logger
            rootLogger.setLevel(java.util.logging.Level.ALL);
        }
    }
}
