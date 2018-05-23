package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

class ContinueWithResultFunctionHolder<I, O> extends ContextRunnableHolder implements ContinueWithResultFunction<I, O> {

    private ContinueWithResultFunction<I, O> action;

    public ContinueWithResultFunctionHolder(ContinueWithResultFunction<I, O> callable, RunnableFilter filter) {
        super(filter);
        this.action = callable;
    }

    public O apply(ListenableFuture<? extends I> task, ContinueWithResult result) throws Exception {
        return process(() -> action.apply(task, result));
    }
}
