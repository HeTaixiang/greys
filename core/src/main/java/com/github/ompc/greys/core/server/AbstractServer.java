package com.github.ompc.greys.core.server;

import com.github.ompc.greys.core.ClassDataSource;
import com.github.ompc.greys.core.Configure;
import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.manager.TimeFragmentManager;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServer implements Server{

    protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    protected final AtomicBoolean isBindRef = new AtomicBoolean(false);
    protected final SessionManager sessionManager;
    protected final CommandHandler commandHandler;

    public AbstractServer(Instrumentation inst) {
        this.sessionManager = new DefaultSessionManager();
        this.commandHandler = new DefaultCommandHandler(this, inst);

        initForManager(inst);
    }


    @Override
    public boolean isBind() {
        return isBindRef.get();
    }

    @Override
    public void bind(Configure configure) throws IOException {
        if (!isBindRef.compareAndSet(false, true)) {
            throw new IllegalStateException("already bind");
        }
    }

    @Override
    public void unbind() {
        if (!isBindRef.compareAndSet(true, false)) {
            throw new IllegalStateException("already unbind");
        }
    }

    @Override
    public void destroy() {
        if (!sessionManager.isDestroy()) {
            sessionManager.destroy();
        }
    }

    /*
     * 初始化各种manager
     */
    private void initForManager(final Instrumentation inst) {
        TimeFragmentManager.Factory.getInstance();
        ReflectManager.Factory.initInstance(new ClassDataSource() {
            @Override
            public Collection<Class<?>> allLoadedClasses() {
                final Class<?>[] classArray = inst.getAllLoadedClasses();
                return null == classArray
                        ? new ArrayList<Class<?>>()
                        : Arrays.asList(classArray);
            }
        });
    }
}
