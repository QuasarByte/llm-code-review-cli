package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliParallelExecutionParameter;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ParallelExecutionParameter;

public interface ParallelExecutionParameterMapper {
    ParallelExecutionParameter map(CliParallelExecutionParameter parameter);
}
