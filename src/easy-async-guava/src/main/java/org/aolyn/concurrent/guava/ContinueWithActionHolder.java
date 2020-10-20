package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.ContextRunnableHolder;
import org.aolyn.concurrent.RunnableFilter;

/**
 * Created by Chris Huang on 2016-07-22.
 */
class ContinueWithActionHolder<I> extends ContextRunnableHolder implements ContinueWithAction<I> {
    private final ContinueWithAction<I> action;

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
