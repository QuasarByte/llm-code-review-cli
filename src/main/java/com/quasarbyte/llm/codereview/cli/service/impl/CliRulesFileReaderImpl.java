package com.quasarbyte.llm.codereview.cli.service.impl;

import com.quasarbyte.llm.codereview.cli.exception.LlmCodeReviewCliException;
import com.quasarbyte.llm.codereview.cli.exception.ValidationException;
import com.quasarbyte.llm.codereview.cli.model.FileTypeEnum;
import com.quasarbyte.llm.codereview.cli.model.CliRule;
import com.quasarbyte.llm.codereview.cli.service.FileService;
import com.quasarbyte.llm.codereview.cli.service.CliRulesFileReader;
import com.quasarbyte.llm.codereview.cli.service.ResourceLoader;
import com.quasarbyte.llm.codereview.cli.service.parser.CliRulesJsonParser;
import com.quasarbyte.llm.codereview.cli.service.parser.CliRulesXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CliRulesFileReaderImpl implements CliRulesFileReader {

    private static final Logger logger = LoggerFactory.getLogger(CliRulesFileReaderImpl.class);

    private final FileService fileService;
    private final CliRulesJsonParser rulesJsonParser;
    private final CliRulesXmlParser rulesXmlParser;
    private final ResourceLoader resourceLoader;

    public CliRulesFileReaderImpl(FileService fileService,
                                  CliRulesJsonParser rulesJsonParser,
                                  CliRulesXmlParser rulesXmlParser,
                                  ResourceLoader resourceLoader) {
        this.fileService = fileService;
        this.rulesJsonParser = rulesJsonParser;
        this.rulesXmlParser = rulesXmlParser;
        this.resourceLoader = resourceLoader;
        logger.debug("PRulesFileReaderImpl initialized.");
    }

    @Override
    public List<CliRule> readPRules(String filePath) throws Exception {
        logger.info("Reading Rules from file: '{}'", filePath);

        if (notNullOrBlank(filePath)) {

            String fileExtension = fileService.getFileExtension(filePath)
                    .orElseThrow(() -> {
                        logger.error("Cannot determine file extension for '{}'", filePath);
                        return new LlmCodeReviewCliException(String.format("Can not determine file extension of file: '%s'", filePath));
                    });

            logger.debug("Detected file extension '{}' for '{}'", fileExtension, filePath);

            Optional<FileTypeEnum> fileTypeEnumOptional = FileTypeEnum.findByExtension(fileExtension);

            if (fileTypeEnumOptional.isPresent()) {
                logger.debug("File type '{}' identified for extension '{}'", fileTypeEnumOptional.get(), fileExtension);

                if (FileTypeEnum.JSON.equals(fileTypeEnumOptional.get())) {
                    logger.info("Parsing JSON rules from '{}'", filePath);
                    String body = resourceLoader.load(filePath);
                    List<CliRule> rules = rulesJsonParser.parseRules(body);
                    logger.info("Parsed {} rules from JSON file '{}'", rules.size(), filePath);
                    return rules;

                } else if (FileTypeEnum.XML.equals(fileTypeEnumOptional.get())) {
                    logger.info("Parsing XML rules from '{}'", filePath);
                    String body = resourceLoader.load(filePath);
                    List<CliRule> rules = rulesXmlParser.parseRules(body);
                    logger.info("Parsed {} rules from XML file '{}'", rules.size(), filePath);
                    return rules;

                } else {
                    logger.error("Unsupported file type: '{}'", fileExtension);
                    throw new ValidationException(String.format("Unsupported file type: '%s'. Supported files types only JSON and XML.", fileExtension));
                }

            } else {
                logger.error("Unsupported file type: '{}'", fileExtension);
                throw new ValidationException(String.format("Unsupported file type: '%s'. Supported files types only JSON and XML.", fileExtension));
            }
        } else {
            logger.error("The file path is empty");
            throw new ValidationException("The file path is empty");
        }
    }

    private static boolean nullOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private static boolean notNullOrBlank(String string) {
        return !nullOrBlank(string);
    }
}
