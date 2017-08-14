package com.github.ompc.greys.core.server;

import com.github.ompc.greys.core.Configure;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public interface Server {
    boolean isBind();

    void bind(Configure configure) throws IOException;

    void unbind();

    void destroy();
}
