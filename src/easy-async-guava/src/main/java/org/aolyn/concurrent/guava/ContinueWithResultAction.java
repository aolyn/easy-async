package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.ContinueWithResult;

/**
 * Created by Chris Huang on 2017-06-06.
 */
public interface ContinueWithResultAction<I> {

    void apply(ListenableFuture<? extends I> task, ContinueWithResult result) throws Exception;
}
