package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by Chris Huang on 2016-07-22.
 */
class ContinueWithActionHolder<I> extends ContextRunnableHolder implements ContinueWithAction<I> {
    private ContinueWithAction<I> action;

    public ContinueWithActionHolder(ContinueWithAction<I> callable, RunnableFilter filter) {
        super(filter);
        this.action = callable;
    }

    public void apply(ListenableFuture<? extends I> task) throws Exception {
        process(() -> {
            action.apply(task);
            return null;
        });
    }
}
