package org.aolyn.concurrent;

import java.util.concurrent.Callable;

/**
 * Created by Chris Huang on 2016-07-22.
 */
public class CallableHolder<V> extends ContextRunnableHolder implements Callable<V> {
    private final Callable<V> runnable;

    public CallableHolder(Callable<V> callable, RunnableFilter filter) {
        super(filter);
        this.runnable = callable;
    }

    public V call() throws Exception {
        return process(runnable);
    }
}
