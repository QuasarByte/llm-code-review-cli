package com.quasarbyte.llm.codereview.cli;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LlmCodeReviewCliTest {

    private static final Logger logger = LoggerFactory.getLogger(LlmCodeReviewCliTest.class);

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        // Set up MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start(); // uses a random available port
    }

    @AfterEach
    void tearDown() throws Exception {
        // Shutdown MockWebServer
        mockWebServer.shutdown();
    }

    @Test
    void testExecuteWithMockLlmService() throws Exception {
        // --- MOCK: POST /v1/chat/completions ---
        String chatCompletionResponse = loadTestResource("src/test/resources/com/quasarbyte/llm/codereview/cli/configuration/test/one/chatCompletionResponse.json");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(chatCompletionResponse)
        );

        // Get the port that the mock server is running on
        int port = mockWebServer.getPort();
        String baseUrl = String.format("http://localhost:%d/v1/", port);

        // Create CLI arguments
        String[] args = {
                "--review-params-file", "src/test/resources/com/quasarbyte/llm/codereview/cli/configuration/test/one/reviewParameter.json",
                "--llm-client-config-file", "src/test/resources/com/quasarbyte/llm/codereview/cli/configuration/test/one/llmClientConfiguration.json",
                "--persistence-configuration-file", "src/test/resources/com/quasarbyte/llm/codereview/cli/configuration/test/one/persistenceConfiguration.json",
                "--parallel-execution-parameter-file", "src/test/resources/com/quasarbyte/llm/codereview/cli/configuration/test/one/parallelExecutionParameter.json",
                "--run-failure-configuration-file", "src/test/resources/com/quasarbyte/llm/codereview/cli/configuration/test/one/runFailureConfiguration.json",
                "--api-key-override", "demo",
                "--base-url-override", baseUrl,
                "--json-report-file-path", "target/reports/llm-code-review-report.json",
                "--markdown-report-file-path", "target/reports/llm-code-review-report.md",
                "--csv-report-file-path", "target/reports/llm-code-review-report.csv",
        };

        // Execute the CLI
        LlmCodeReviewCli cli = new LlmCodeReviewCli();
        int exitCode = new CommandLine(cli).execute(args);

        // Verify exit code
        assertEquals(0, exitCode, "CLI should exit with code 0 (success)");

        // Verify HTTP requests
        RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertNotNull(request, "No HTTP request to mock server (POST /v1/chat/completions)");
        assertEquals("/v1/chat/completions", request.getPath(), "Request path should be /v1/chat/completions");
        assertEquals("POST", request.getMethod(), "Request should be POST");

        // Check the body of the POST request
        String body = request.getBody().readUtf8();
        logger.info("Request body: {}", body);
        assertTrue(body.contains("ExampleOne.java"), "Request body should contain the file name");
        assertTrue(body.contains("qwen3-8b"), "Request body should contain the model name");
    }

    /**
     * Helper method to load test resource files as strings (Java 8 compatible)
     */
    private String loadTestResource(String resourcePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resourcePath)), StandardCharsets.UTF_8);
    }
}