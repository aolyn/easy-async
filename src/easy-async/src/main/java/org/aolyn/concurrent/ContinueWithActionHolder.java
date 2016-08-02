package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

class ContinueWithActionHolder<I> extends ContextRunableHolder implements ContinueWithAction<I> {
    private ContinueWithAction<I> action;

    public ContinueWithActionHolder(ContinueWithAction<I> callable) {
        this.action = callable;
    }

    public void apply(ListenableFuture<? extends I> task) throws Throwable {
        beforeExecute();

        try {
            action.apply(task);
        } finally {
            afterExecute();
        }
    }
}
