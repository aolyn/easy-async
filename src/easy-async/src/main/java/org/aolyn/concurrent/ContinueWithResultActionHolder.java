package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

class ContinueWithResultActionHolder<I> extends ContextRunnableHolder implements ContinueWithResultAction<I> {

    private ContinueWithResultAction<I> action;

    public ContinueWithResultActionHolder(ContinueWithResultAction<I> callable, RunnableFilter filter) {
        super(filter);
        this.action = callable;
    }

    public void apply(ListenableFuture<? extends I> task, ContinueWithResult result) throws Exception {
        process(() -> {
            action.apply(task,result);
            return null;
        });
    }
}
