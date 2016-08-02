package org.aolyn.concurrent;

import java.util.concurrent.Callable;

class CallableHolder<V> extends ContextRunableHolder implements Callable<V> {
    private Callable<V> runnable;

    public CallableHolder(Callable<V> callable) {
        this.runnable = callable;
    }

    public V call() throws Exception {
        beforeExecute();

        try {
            return runnable.call();
        } finally {
            afterExecute();
        }
    }
}
