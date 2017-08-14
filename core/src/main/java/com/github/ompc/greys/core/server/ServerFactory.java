package com.github.ompc.greys.core.server;

import com.github.ompc.greys.core.Configure;

import java.lang.instrument.Instrumentation;

public class ServerFactory {
    public Server getServer(Configure configure, final Instrumentation instrumentation) {
        if (configure.isRemote()) {
            return GaServer.getInstance(configure.getJavaPid(), instrumentation);
        } else {
            return new LocalServer(instrumentation);
        }
    }
}
