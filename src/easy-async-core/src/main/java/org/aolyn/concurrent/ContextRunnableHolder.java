package org.aolyn.concurrent;

import org.aolyn.concurrent.context.CallContext;
import org.aolyn.concurrent.context.LogicalExecuteContext;

import java.util.concurrent.Callable;

/**
 * Created by Chris Huang on 2016-07-22.
 */
public class ContextRunnableHolder {

    private LogicalExecuteContext context;
    private LogicalExecuteContext contextBeforeRun;
    private RunnableFilter filter;

    protected ContextRunnableHolder(RunnableFilter filter) {
        this.context = LogicalExecuteContext.copy(CallContext.getContext());
        this.filter = filter;
    }

    protected <V> V process(Callable<V> callable) throws Exception {
        try {
            beforeExecute();
        } catch (Exception ex) {
            //ignore
        }

        try {
            return callable.call();
        } finally {
            try {
                afterExecute();
            } catch (Exception ex) {
                //ignore
            }
        }
    }

    private void beforeExecute() {
        contextBeforeRun = CallContext.getContext();
        //restore context which when task created to current thread
        if (contextBeforeRun != context) {
            CallContext.setContext(context);
        }
        if (filter != null) {
            filter.beforeExecute(this);
        }
    }

    private void afterExecute() {
        final Object currentContext = CallContext.getContext();
        //restore context before run to current thread
        if (currentContext != contextBeforeRun) {
            CallContext.setContext(contextBeforeRun);
        }
        if (filter != null) {
            filter.afterExecute(this);
        }
    }
}
