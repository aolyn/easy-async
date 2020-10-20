package org.aolyn.concurrent.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Huang on 2016-07-22.
 */
public class LogicalExecuteContext {

    private String name;
    private final Map<String, Object> dataItems = new HashMap<>();
    private long threadId;

    LogicalExecuteContext() {
    }

    public Object getData(String key) {
        return dataItems.get(key);
    }

    public void setData(String key, Object value) {
        if (value == null) {
            dataItems.remove(key);
        } else {
            dataItems.put(key, value);
        }
    }

    public void reset() {
        dataItems.clear();
        setName(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LogicalExecuteContext copy(LogicalExecuteContext src) {
        if (src == null) {
            return null;
        }

        LogicalExecuteContext newContext = new LogicalExecuteContext();
        newContext.name = src.name;
        newContext.dataItems.putAll(src.dataItems);
        return newContext;
    }

    void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    long getThreadId() {
        return this.threadId;
    }
}
