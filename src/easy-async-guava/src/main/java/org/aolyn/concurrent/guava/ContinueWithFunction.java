package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public interface ContinueWithFunction<I, O> {
    O apply(ListenableFuture<? extends I> task) throws Exception;
}
