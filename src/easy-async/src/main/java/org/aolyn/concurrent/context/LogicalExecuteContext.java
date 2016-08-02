package org.aolyn.concurrent.context;

import java.util.HashMap;
import java.util.Map;

public class LogicalExecuteContext {
    private String name;
    private Map<String, Object> dataItems = new HashMap<>();

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
}
