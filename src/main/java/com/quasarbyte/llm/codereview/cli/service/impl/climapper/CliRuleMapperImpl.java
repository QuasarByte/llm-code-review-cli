package com.quasarbyte.llm.codereview.cli.service.impl.climapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.cli.service.climapper.CliRuleMapper;
import com.quasarbyte.llm.codereview.sdk.model.parameter.Rule;
import com.quasarbyte.llm.codereview.sdk.model.parameter.RuleSeverityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliRuleMapperImpl implements CliRuleMapper {

    private static final Logger logger = LoggerFactory.getLogger(CliRuleMapperImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Rule map(CliRule rule) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Mapping CliRule: {}", objectMapper.writeValueAsString(rule));
            } catch (Exception e) {
                logger.warn("Failed to serialize CliRule for debug logging: {}", e.getMessage());
            }
        }

        if (rule == null) {
            logger.warn("Provided CliRule is null, returning null.");
            return null;
        }

        Rule result = new Rule();
        result.setCode(rule.getCode());
        result.setDescription(rule.getDescription());
        result.setSeverity(getSeverity(rule));

        logger.info("Mapped CliRule to Rule: code='{}', description='{}', severity={}",
                result.getCode(), result.getDescription(), result.getSeverity());

        return result;
    }

    private static RuleSeverityEnum getSeverity(CliRule rule) {
        return rule.getSeverity() != null ? RuleSeverityEnum.fromName(rule.getSeverity()) : RuleSeverityEnum.INFO;
    }
}
