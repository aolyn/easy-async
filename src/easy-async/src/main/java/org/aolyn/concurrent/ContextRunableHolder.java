package org.aolyn.concurrent;

import org.aolyn.concurrent.context.CallContext;
import org.aolyn.concurrent.context.LogicalExecuteContext;
// import com.dianping.cat.Cat;
// import com.dianping.cat.message.ForkedTransaction;

import java.util.concurrent.atomic.AtomicInteger;

class ContextRunableHolder {
    private LogicalExecuteContext context;
    private LogicalExecuteContext contextBeforeRun;
    // private ForkedTransaction transaction;
    private static AtomicInteger continueIndex = new AtomicInteger();
    private boolean logSpan = true;

    protected ContextRunableHolder() {
        this.context = CallContext.getContext();

        // if (TaskUtils.Options.isLogSpan()) {
        //     Object logSpanFlag = CallContext.getData("TaskUtil.continueWith_logSpan");
        //     if (logSpanFlag != null) {
        //         logSpan = (Boolean) logSpanFlag;
        //     }
        //
        //     if (logSpan) {
        //         String spanName;
        //         Object spanNameObj = CallContext.getData("continueWithSpanName");
        //         if (spanNameObj != null) {
        //             spanName = (String) spanNameObj;
        //         } else {
        //             Integer index = continueIndex.incrementAndGet();
        //             spanName = "ContinueWithSpan-" + index.toString();
        //         }
        //         // transaction = Cat.newForkedTransaction("TaskUtil", spanName);
        //     }
        // } else {
        //     logSpan = false;
        // }
    }

    protected void beforeExecute() {
        contextBeforeRun = CallContext.getContext();
        //restore context which when task created to current thread
        if (contextBeforeRun != context) {
            CallContext.setContext(context);
        }
        if (logSpan) {
            // transaction.fork();
            //transaction.resetStartTimestamp();
        }
    }

    protected void afterExecute() {
        final Object currentContext = CallContext.getContext();
        //restore context before run to current thread
        if (currentContext != contextBeforeRun) {
            CallContext.setContext(contextBeforeRun);
        }
        if (logSpan) {
            // transaction.complete();
        }
    }
}
