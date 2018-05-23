package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.ContextRunnableHolder;
import org.aolyn.concurrent.ContinueWithResult;
import org.aolyn.concurrent.RunnableFilter;

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
