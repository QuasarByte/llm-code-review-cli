package com.quasarbyte.llm.codereview.cli.service.impl.parser;

import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.cli.model.CliRuleList;
import com.quasarbyte.llm.codereview.cli.service.parser.CliRulesXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

public class CliRulesXmlParserImpl implements CliRulesXmlParser {

    private static final Logger logger = LoggerFactory.getLogger(CliRulesXmlParserImpl.class);

    @Override
    public List<CliRule> parseRules(String xml) throws Exception {
        logger.info("Parsing rules from XML. XML length: {}", xml != null ? xml.length() : "null");

        if (xml == null) {
            return Collections.emptyList();
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CliRuleList.class);
            logger.debug("Created JAXBContext for CliRuleList.");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            logger.debug("Created Unmarshaller for CliRuleList.");
            CliRuleList ruleList = (CliRuleList) unmarshaller.unmarshal(new StringReader(xml));
            if (ruleList == null) {
                logger.warn("Parsed CliRuleList is null.");
                return null;
            }
            List<CliRule> rules = ruleList.getRules();
            logger.info("Successfully parsed {} rules from XML.", rules != null ? rules.size() : 0);
            return rules;
        } catch (Exception e) {
            logger.error("Failed to parse rules from XML: {}", e.getMessage(), e);
            throw e;
        }
    }
}
