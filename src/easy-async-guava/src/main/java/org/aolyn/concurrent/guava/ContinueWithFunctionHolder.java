package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.ContextRunnableHolder;
import org.aolyn.concurrent.RunnableFilter;

/**
 * Created by Chris Huang on 2016-07-22.
 */
class ContinueWithFunctionHolder<I, O> extends ContextRunnableHolder implements ContinueWithFunction<I, O> {
    private final ContinueWithFunction<I, O> action;

    public ContinueWithFunctionHolder(ContinueWithFunction<I, O> callable, RunnableFilter filter) {
        super(filter);
        this.action = callable;
    }

    public O apply(ListenableFuture<? extends I> task) throws Exception {
        return process(()->action.apply(task));
    }
}
