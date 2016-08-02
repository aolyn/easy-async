package org.aolyn.concurrent;

class RunableHolder extends ContextRunableHolder implements Runnable {
    private Runnable runnable;

    public RunableHolder(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        beforeExecute();

        try {
            runnable.run();
        } finally {
            afterExecute();
        }
    }
}
