package com.quasarbyte.llm.codereview.cli.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Represents a list of rules used for code review.
 * <p>
 * This class is typically used for XML/JSON serialization/deserialization
 * of multiple {@link CliRule} elements.
 * </p>
 */
@XmlRootElement(name = "rules")
@XmlAccessorType(XmlAccessType.FIELD)
public class CliRuleList {
    /**
     * The list of individual rules to be applied during code review.
     */
    @XmlElement(name = "rule")
    private List<CliRule> rules;

    public List<CliRule> getRules() {
        return rules;
    }

    public void setRules(List<CliRule> rules) {
        this.rules = rules;
    }
}
