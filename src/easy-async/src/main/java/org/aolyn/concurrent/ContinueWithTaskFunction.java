package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public interface ContinueWithTaskFunction<I, O> {
    ListenableFuture<O> apply(ListenableFuture<? extends I> task) throws Exception;
}
