package org.aolyn.concurrent;

/**
 * Created by Chris Huang on 2016-07-22.
 */
class RunnableHolder extends ContextRunnableHolder implements Runnable {
    private Runnable runnable;

    public RunnableHolder(Runnable runnable, RunnableFilter filter) {
        super(filter);
        this.runnable = runnable;
    }

    public void run() {
        try {
            process(() -> {
                runnable.run();
                return null;
            });
        } catch (Exception e) {
            //ignore
        }
    }
}
