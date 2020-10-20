package org.aolyn.concurrent.context;

/**
 * Created by Chris Huang on 2016-07-22.
 */
public final class CallContext {

    private final static ThreadLocal<LogicalExecuteContext> contextLocal = new ThreadLocal<>();

    private CallContext() {
    }

    private static LogicalExecuteContext getOrCreateContext() {
        LogicalExecuteContext context = contextLocal.get();
        if (context == null) {
            context = contextLocal.get();
            if (context == null) {
                context = new LogicalExecuteContext();
                setContext(context);
            }
        }
        return context;
    }

    public static LogicalExecuteContext getContext() {
        return contextLocal.get();
    }

    /**
     * set current thread context, after set context's threadId will set to current thread's Id
     *
     * @param context context to copy from
     */
    public static void setContext(LogicalExecuteContext context) {
        contextLocal.set(context);
        context.setThreadId(Thread.currentThread().getId());
    }

    public static Object getData(String key) {
        LogicalExecuteContext context = getContext();
        if (context == null) {
            return null;
        }
        // CallContext copied by InheritableThreadLocal
        if (Thread.currentThread().getId() != context.getThreadId()) {
            context = LogicalExecuteContext.copy(context);
            setContext(context);
        }

        return context.getData(key);
    }

    /**
     * set data item to Context, if context not exist will create new one
     *
     * @param key key
     * @param value value
     */
    public static void setData(String key, Object value) {
        LogicalExecuteContext context = getOrCreateContext();
        context.setData(key, value);
    }

    //public static void clear() {
    //    LogicalExecuteContext context = contextLocal.get();
    //    if (context != null) {
    //        context.reset();
    //    }
    //    contextLocal.set(null);
    //}
}
