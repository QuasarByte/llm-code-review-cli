package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliLlmQuota;
import com.quasarbyte.llm.codereview.sdk.model.parameter.LlmQuota;

public interface CliLlmQuotaMapper {
    LlmQuota map(CliLlmQuota quota);
}
