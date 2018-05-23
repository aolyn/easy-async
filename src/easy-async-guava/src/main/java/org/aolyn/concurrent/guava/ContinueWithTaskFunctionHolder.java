package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by Chris Huang on 2016-07-22.
 */
class ContinueWithTaskFunctionHolder<I, O> extends ContextRunnableHolder implements ContinueWithTaskFunction<I, O> {
    private ContinueWithTaskFunction<I, O> action;

    public ContinueWithTaskFunctionHolder(ContinueWithTaskFunction<I, O> callable, RunnableFilter filter) {
        super(filter);
        this.action = callable;
    }

    public ListenableFuture<O> apply(ListenableFuture<? extends I> task) throws Exception {
        return process(() -> action.apply(task));
    }
}
