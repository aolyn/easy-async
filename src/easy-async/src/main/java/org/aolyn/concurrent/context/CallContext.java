package org.aolyn.concurrent.context;

public final class CallContext {
    private static ThreadLocal<LogicalExecuteContext> contextLocal = new InheritableThreadLocal<>();
    private static Object contextInitLock = new Object();

    private CallContext() {
    }

    private static LogicalExecuteContext getOrCreateContext() {
        LogicalExecuteContext context = contextLocal.get();
        if (context == null) {
            synchronized (contextInitLock) { //TOTO: only lock current Thread
                context = contextLocal.get();
                if (context == null) {
                    context = new LogicalExecuteContext();
                    contextLocal.set(context);
                }
            }
        }
        return context;
    }

    public static LogicalExecuteContext getContext() {
        return contextLocal.get();
    }

    public static void setContext(LogicalExecuteContext context) {
        contextLocal.set(context);
    }

    public static Object getData(String key) {
        LogicalExecuteContext context = getContext();
        if (context == null) {
            return null;
        }
        return context.getData(key);
    }

    /**
     * set data item to Context, if context not exist will create new one
     * @param key
     * @param value
     */
    public static void setData(String key, Object value) {
        LogicalExecuteContext context = getOrCreateContext();
        context.setData(key, value);
    }

    public static void clear() {
        LogicalExecuteContext context = contextLocal.get();
        if (context != null) {
            context.reset();
        }
        contextLocal.set(null);
    }
}
