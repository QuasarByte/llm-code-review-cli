package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliReviewParameter;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ReviewParameter;

public interface CliReviewParameterMapper {
    ReviewParameter map(CliReviewParameter parameter);
}
