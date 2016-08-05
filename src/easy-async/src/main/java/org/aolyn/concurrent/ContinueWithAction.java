package org.aolyn.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public interface ContinueWithAction<I> {
    void apply(ListenableFuture<? extends I> task) throws Exception; //NOSONAR
}
