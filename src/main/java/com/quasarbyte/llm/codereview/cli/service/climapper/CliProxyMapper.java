package com.quasarbyte.llm.codereview.cli.service.climapper;

import com.quasarbyte.llm.codereview.cli.model.CliProxy;

import java.net.Proxy;

public interface CliProxyMapper {
    Proxy map(CliProxy proxy);
}
