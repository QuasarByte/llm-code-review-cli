package com.quasarbyte.llm.codereview.cli.model;

/**
 * Configuration for executing custom scripts using Rhino.
 */
public class CliRhinoConfiguration {
    /**
     * The file path to the Rhino JavaScript script.
     * <p>
     * This script will be executed during the review process.
     * </p>
     */
    private String scriptFilePath;

    /**
     * The name of the function within the script to invoke.
     */
    private String functionName;

    public String getScriptFilePath() {
        return scriptFilePath;
    }

    public CliRhinoConfiguration setScriptFilePath(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath;
        return this;
    }

    public String getFunctionName() {
        return functionName;
    }

    public CliRhinoConfiguration setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }
}
