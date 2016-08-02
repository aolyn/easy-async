package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by Chris Huang on 2017-06-06.
 */
public interface ContinueWithAction<I> {
    void apply(ListenableFuture<? extends I> task) throws Throwable; //NOSONAR
}
