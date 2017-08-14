package com.github.ompc.greys.core.server;

import com.github.ompc.greys.core.Configure;
import com.github.ompc.greys.core.util.LogUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class LocalServer extends AbstractServer {
    private final Logger logger = LogUtil.getLogger();

    private static final String RESET_COMMAND = "reset";

    public LocalServer(Instrumentation inst) {
        super(inst);
    }

    @Override
    public void bind(Configure configure) throws IOException {
        super.bind(configure);

        logger.info("local-server start. and will excute command {} and save result at {}", configure.getCommand(), configure.getOutPath());
        WritableByteChannel channel = openOutput(configure.getOutPath());
        final Session session = sessionManager.newSession(configure.getJavaPid(), channel, DEFAULT_CHARSET);
        try {
            // 命令执行
            commandHandler.executeCommand(configure.getCommand(), session);
        } catch (IOException e) {
            logger.info("can't open the output file, err is {}", e.getMessage());
        } finally {
            clean(session);
            destroy();
        }


    }

    private FileChannel openOutput(String outputPath) throws IOException {
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        return new FileOutputStream(file, true).getChannel();
    }

    private void clean(Session session) {
        try {
            commandHandler.executeCommand(RESET_COMMAND, session);
        } catch (IOException e) {
            logger.warn("restore the enhanced Classes failure, the err is {}", e.getMessage());
        }
    }

}
