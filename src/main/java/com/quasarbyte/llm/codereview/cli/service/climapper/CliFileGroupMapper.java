package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliFileGroup;
import com.quasarbyte.llm.codereview.sdk.model.parameter.FileGroup;

public interface CliFileGroupMapper {
    FileGroup map(CliFileGroup fileGroup);
}
