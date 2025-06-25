package com.quasarbyte.llm.codereview.cli.service;

import java.util.Optional;

public interface FileService {
    Optional<String> getFileExtension(String fileName);
}
