package com.quasarbyte.llm.codereview.cli.model;

import java.util.List;

/**
 * Represents a single review target for code review.
 */
public class CliReviewTarget {
    /**
     * The name of the review target.
     */
    private String reviewTargetName;

    /**
     * The list of file groups associated with this review target.
     * <p>
     * Each file group defines a set of files to be reviewed.
     * </p>
     */
    private List<CliFileGroup> fileGroups;

    /**
     * The list of rules to be applied specifically to this review target.
     */
    private List<CliRule> rules;

    /**
     * The paths to an external files containing rules for this review target.
     */
    private List<String> rulesFilePaths;

    /**
     * List of prompt messages used during the review of this target.
     */
    private List<String> reviewTargetPrompts;

    public String getReviewTargetName() {
        return reviewTargetName;
    }

    public CliReviewTarget setReviewTargetName(String reviewTargetName) {
        this.reviewTargetName = reviewTargetName;
        return this;
    }

    public List<CliFileGroup> getFileGroups() {
        return fileGroups;
    }

    public CliReviewTarget setFileGroups(List<CliFileGroup> fileGroups) {
        this.fileGroups = fileGroups;
        return this;
    }

    public List<CliRule> getRules() {
        return rules;
    }

    public CliReviewTarget setRules(List<CliRule> rules) {
        this.rules = rules;
        return this;
    }

    public List<String> getRulesFilePaths() {
        return rulesFilePaths;
    }

    public CliReviewTarget setRulesFilePaths(List<String> rulesFilePaths) {
        this.rulesFilePaths = rulesFilePaths;
        return this;
    }

    public List<String> getReviewTargetPrompts() {
        return reviewTargetPrompts;
    }

    public CliReviewTarget setReviewTargetPrompts(List<String> reviewTargetPrompts) {
        this.reviewTargetPrompts = reviewTargetPrompts;
        return this;
    }
}
