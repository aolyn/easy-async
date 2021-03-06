package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.ContextRunnableHolder;
import org.aolyn.concurrent.ContinueWithResult;
import org.aolyn.concurrent.RunnableFilter;

class ContinueWithResultFunctionHolder<I, O> extends ContextRunnableHolder implements ContinueWithResultFunction<I, O> {

    private final ContinueWithResultFunction<I, O> action;

    public ContinueWithResultFunctionHolder(ContinueWithResultFunction<I, O> callable, RunnableFilter filter) {
        super(filter);
        this.action = callable;
    }

    public O apply(ListenableFuture<? extends I> task, ContinueWithResult result) throws Exception {
        return process(() -> action.apply(task, result));
    }
}
