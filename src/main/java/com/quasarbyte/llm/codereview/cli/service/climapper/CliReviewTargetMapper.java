package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliReviewTarget;
import com.quasarbyte.llm.codereview.sdk.model.parameter.ReviewTarget;

public interface CliReviewTargetMapper {
    ReviewTarget map(CliReviewTarget reviewTarget);
}
