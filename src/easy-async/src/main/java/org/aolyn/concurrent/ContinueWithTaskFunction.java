package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by Chris Huang on 2017-06-03.
 */
public interface ContinueWithTaskFunction<I, O> {
    ListenableFuture<O> apply(ListenableFuture<? extends I> task) throws Throwable; //NOSONAR
}
