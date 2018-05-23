package org.aolyn.concurrent.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Chris Huang on 2016-07-22.
 */
final class WhenAllTaskHelper {

    private WhenAllTaskHelper() {
    }

    public static <T> ListenableFuture<List<T>> whenAll(ListenableFuture<? extends T>... tasks) {
        List<ListenableFuture<? extends T>> immutableList = ImmutableList.copyOf(tasks);
        return whenAll(immutableList);
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed (even if any of the
     * futures fails), if any future fail the All-Futrue will not complete until all futures completed.
     *
     * @param tasks
     * @param <T>
     * @return
     */
    public static <T> ListenableFuture<List<T>> whenAll(List<? extends ListenableFuture<? extends T>> tasks) {
        if (tasks.isEmpty()) {
            return TaskUtils.fromResult(new ArrayList<>());
        }

        final List<ListenableFuture<? extends T>> immutableList = ImmutableList.copyOf(tasks);
        final AtomicInteger counter = new AtomicInteger(immutableList.size());
        final SettableFuture<List<T>> resultTask = SettableFuture.create();

        for (ListenableFuture<? extends T> task : immutableList) {
            task.addListener(() -> {
                int unfinishedCount = counter.decrementAndGet();
                if (unfinishedCount == 0) {
                    List<T> results = new ArrayList<>();
                    for (ListenableFuture<? extends T> finishTask : immutableList) {
                        try {
                            T result = finishTask.get();
                            results.add(result);
                        } catch (Throwable ex) {
                            resultTask.setException(ex.getCause());
                            return;
                        }
                    }
                    resultTask.set(results);
                }
            }, MoreExecutors.directExecutor());
        }

        return resultTask;
    }
}
