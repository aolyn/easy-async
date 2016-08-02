package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

class ContinueWithFunctionHolder<I, O> extends ContextRunableHolder implements ContinueWithFunction<I, O> {
    private ContinueWithFunction<I, O> action;

    public ContinueWithFunctionHolder(ContinueWithFunction<I, O> callable) {
        this.action = callable;
    }

    public O apply(ListenableFuture<? extends I> task) throws Throwable {
        beforeExecute();

        try {
            return action.apply(task);
        } finally {
            afterExecute();
        }
    }
}
