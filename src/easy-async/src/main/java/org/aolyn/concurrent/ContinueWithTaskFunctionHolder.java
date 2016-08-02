package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

class ContinueWithTaskFunctionHolder<I, O> extends ContextRunableHolder implements ContinueWithTaskFunction<I, O> {
    private ContinueWithTaskFunction<I, O> action;

    public ContinueWithTaskFunctionHolder(ContinueWithTaskFunction<I, O> callable) {
        this.action = callable;
    }

    public ListenableFuture<O> apply(ListenableFuture<? extends I> task) throws Throwable {
        beforeExecute();

        try {
            return action.apply(task);
        } finally {
            afterExecute();
        }
    }
}
