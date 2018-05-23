package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.ContinueWithResult;

/**
 * Created by Chris Huang on 2017-06-03.
 */
public interface ContinueWithResultFunction<I, O> {

    O apply(ListenableFuture<? extends I> task, ContinueWithResult result) throws Exception;
}
