package org.aolyn.concurrent;

/**
 * Created by Chris Huang on 2016-07-22.
 */
public interface RunnableFilter {
    /**
     * will be called before future body running
     * @param wrapper executor wrapper, used to indicate each future
     */
    void beforeExecute(Object wrapper);

    /**
     * will be called after future body running
     * @param wrapper executor wrapper, used to indicate each future
     */
    void afterExecute(Object wrapper);
}
