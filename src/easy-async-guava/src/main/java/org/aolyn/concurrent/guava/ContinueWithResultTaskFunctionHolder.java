package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.ContextRunnableHolder;
import org.aolyn.concurrent.ContinueWithResult;
import org.aolyn.concurrent.RunnableFilter;

class ContinueWithResultTaskFunctionHolder<I, O> extends ContextRunnableHolder implements
    ContinueWithResultTaskFunction<I, O> {

    private ContinueWithResultTaskFunction<I, O> action;

    public ContinueWithResultTaskFunctionHolder(ContinueWithResultTaskFunction<I, O> callable, RunnableFilter filter) {
        super(filter);
        this.action = callable;
    }

    public ListenableFuture<O> apply(ListenableFuture<? extends I> task, ContinueWithResult result) throws Exception {
        return process(() -> action.apply(task, result));
    }
}
