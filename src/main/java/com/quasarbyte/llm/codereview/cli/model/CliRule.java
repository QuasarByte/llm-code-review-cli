package com.quasarbyte.llm.codereview.cli.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a single rule to be applied during code review.
 */
@XmlRootElement(name = "rule")
@XmlAccessorType(XmlAccessType.FIELD)
public class CliRule {

    /**
     * The unique code or identifier of the rule.
     * <p>
     * This code can be used to reference the rule in reports or configurations.
     * </p>
     */
    private String code;

    /**
     * A detailed description of the rule and its purpose.
     */
    private String description;

    /**
     * The severity level of the rule (e.g., "info", "warning", "critical").
     * <p>
     * Determines the impact of violating this rule.
     * </p>
     */
    private String severity;

    public String getCode() {
        return code;
    }

    public CliRule setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CliRule setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSeverity() {
        return severity;
    }

    public CliRule setSeverity(String severity) {
        this.severity = severity;
        return this;
    }
}
