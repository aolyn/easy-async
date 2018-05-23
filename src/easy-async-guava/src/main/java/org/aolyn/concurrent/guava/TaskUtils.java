package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.*;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public final class TaskUtils {

    private static TaskUtilsProvider provider = new TaskUtilsProvider();

    public static TaskUtilsProvider getProvider() {
        return provider;
    }

    public static void setProvider(TaskUtilsProvider provider) {
        TaskUtils.provider = provider;
    }

    private TaskUtils() {
    }

    /**
     * execute a runnable by default executor
     *
     * @param runnable runnable to execute
     */
    public static void execute(Runnable runnable) {
        provider.execute(runnable);
    }

    /**
     * execute a runnable by default executor
     *
     * @param runnable runnable to execute
     */
    public static ListenableFuture run(Runnable runnable) {
        return provider.run(runnable);
    }

    /**
     * execute a Callable with return value by default executor
     *
     * @param callable callable to execute
     * @param <V> return class of callable
     * @return ListenableFuture<V> with Callable's result
     */
    public static <V> ListenableFuture<V> run(Callable<V> callable) {
        return provider.run(callable);
    }

    /**
     * get a finished future with result of specific value
     *
     * @param value future result value
     * @param <V> future result class
     * @return specific value in arguments
     */
    public static <V> ListenableFuture<V> fromResult(V value) {
        return provider.fromResult(value);
    }

    /**
     * get a finished future with specific exception
     *
     * @param exception future result exception
     * @param <V> exception class
     * @return specific exception in arguments
     */
    public static <V> ListenableFuture<V> fromException(Exception exception) {
        return provider.fromException(exception);
    }

    /**
     *
     * @param input
     * @param action
     * @param <I>
     * @return
     */
    public static <I> ListenableFuture continueWith(ListenableFuture<I> input, ContinueWithAction<I> action) {
        return provider.continueWith(input, action);
    }

    public static <I, O> ListenableFuture<O> continueWith(ListenableFuture<I> input,
        ContinueWithFunction<I, O> function) {
        return provider.continueWith(input, function);
    }

    public static <I, O> ListenableFuture<O> continueWithTask(ListenableFuture<I> task,
        ContinueWithTaskFunction<I, O> getNextTaskFunc) {
        return provider.continueWithTask(task, getNextTaskFunc);
    }

    //continue with result functions
    public static <I> ListenableFuture continueWith(
        ListenableFuture<I> input,
        ContinueWithResultAction<I> action) {
        return continueWith(input, action);
    }

    public static <I, O> ListenableFuture<O> continueWith(
        ListenableFuture<I> input,
        ContinueWithResultFunction<I, O> function) {
        return provider.continueWith(input, function);
    }

    public static <I, O> ListenableFuture<O> continueWithTask(ListenableFuture<I> task,
        ContinueWithResultTaskFunction<I, O> getNextTaskFunc) {
        return provider.continueWithTask(task, getNextTaskFunc);
    }

    public static <T> void waitAll(ListenableFuture<? extends T>... tasks)
        throws ExecutionException, InterruptedException {
        ListenableFuture allTask = whenAll(tasks);
        allTask.get();
    }

    public static <T> void waitAll(List<ListenableFuture<? extends T>> tasks)
        throws ExecutionException, InterruptedException {
        provider.waitAll(tasks);
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed, if any futures fail the
     * All-Futrue will not complete until all futures completed.
     */
    public static <T> ListenableFuture<List<T>> whenAll(List<? extends ListenableFuture<? extends T>> tasks) {
        return provider.whenAll(tasks);
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed, if any futures fail the
     * All-Futrue will not complete until all futures completed.
     */
    public static <T> ListenableFuture<List<T>> whenAll(ListenableFuture<? extends T>... tasks) {
        return provider.whenAll(tasks);
    }
}
