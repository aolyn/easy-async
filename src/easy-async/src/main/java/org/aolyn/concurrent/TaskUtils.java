package org.aolyn.concurrent;

import com.google.common.util.concurrent.*;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public final class TaskUtils {
    private static Executor defaultExecutor = null;
    private static RunnableFilter filter;

    // public static final ListenableFuture CompletedTask = fromResult(0); //NOSONAR

    static {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("TaskUtil-DefaultExecutor-%d")
                .build();

        Executor asyncExecutor = new ThreadPoolExecutor(32, 1024,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), threadFactory);
        defaultExecutor = asyncExecutor;
    }

    private TaskUtils() {
    }

    private static Executor getExecutor() {
        if (defaultExecutor != null) {
            return defaultExecutor;
        }

        return MoreExecutors.directExecutor();
    }

    /**
     * set default executor for TaskUtils, if is null MoreExecutors.directExecutor() will be used
     * @param executor default executor
     */
    public static void setDefaultExecutor(Executor executor) {
        defaultExecutor = executor;
    }

    /**
     * execute a runnable by default executor
     * @param runnable runnable to execute
     */
    public static void execute(Runnable runnable) {
        getExecutor().execute(new RunnableHolder(runnable, filter));
    }

    /**
     * execute a runnable by default executor
     * @param runnable runnable to execute
     * @return
     */
    public static ListenableFuture run(Runnable runnable) {
        Runnable runnableWrapper = new RunnableHolder(runnable, filter);
        ListenableFutureTask<Integer> futureTask = ListenableFutureTask.create(() -> {
            runnableWrapper.run();
            return 0;
        });
        getExecutor().execute(futureTask);
        return futureTask;
    }

    /**
     * execute a Callable with return value by default executor
     * @param callable callable to execute
     * @param <V> return class of callable
     * @return ListenableFuture<V> with Callable's result
     */
    public static <V> ListenableFuture<V> run(Callable<V> callable) {
        Callable<V> callableWraper = new CallableHolder<>(callable, filter);
        ListenableFutureTask<V> future = ListenableFutureTask.create(callableWraper);
        getExecutor().execute(future);
        return future;
    }

    /**
     * get a finished future with result of specific value
     * @param value future result value
     * @param <V> future result class
     * @return specific value in arguments
     */
    public static <V> ListenableFuture<V> fromResult(V value) {
        return Futures.immediateFuture(value);
    }

    /**
     * get a finished future with specific exception
     * @param exception future result exception
     * @param <V> exception class
     * @return specific exception in arguments
     */
    public static <V> ListenableFuture<V> fromException(Exception exception) {
        SettableFuture<V> task = SettableFuture.create();
        task.setException(exception);
        return task;
    }

    /**
     *
     * @param input
     * @param action
     * @param <I>
     * @return
     */
    public static <I> ListenableFuture continueWith(
            ListenableFuture<I> input,
            ContinueWithAction<I> action) {
        SettableFuture<Object> tcs = SettableFuture.create();

        final ContinueWithAction<I> actionWraper = new ContinueWithActionHolder<>(action, filter);
        input.addListener(() -> {
            boolean isSetted = false;

            try {
                actionWraper.apply(input);
                tcs.set(null);
                isSetted = true;
            } catch (Throwable ex) { //NOSONAR
                tcs.setException(ex);
                isSetted = true;
            } finally {
                if (!isSetted) {
                    tcs.setException(new Exception("unkown continueWith exception"));
                    //todo:
                }
            }
        }, getExecutor());

        return tcs;
    }

    public static <I, O> ListenableFuture<O> continueWith(
            ListenableFuture<I> input,
            ContinueWithFunction<I, O> function) {

        SettableFuture<O> tcs = SettableFuture.create();
        final ContinueWithFunction<I, O> actionWraper = new ContinueWithFunctionHolder<>(function, filter);
        input.addListener(() -> {
            boolean isSetted = false;

            try {
                O result = actionWraper.apply(input);
                tcs.set(result);
                isSetted = true;
            } catch (Throwable ex) { //NOSONAR
                tcs.setException(ex);
                isSetted = true;
            } finally {
                if (!isSetted) {
                    tcs.setException(new Exception("unkown continueWith exception"));
                    //todo:
                }
            }

        }, getExecutor());

        return tcs;
    }

    public static <I, O> ListenableFuture<O> continueWithTask(ListenableFuture<I> task,
            ContinueWithTaskFunction<I, O> getNextTaskFunc) {
        SettableFuture<O> tcs = SettableFuture.create();

        final ContinueWithTaskFunction<I, O> functionWraper =
                new ContinueWithTaskFunctionHolder<>(getNextTaskFunc, filter);
        continueWith(task, tsk -> {
            try {
                ListenableFuture<O> task3 = functionWraper.apply(tsk);

                continueWith(task3, tsk2 -> {
                    try {
                        tcs.set(task3.get());
                    } catch (Throwable ex) { //NOSONAR
                        tcs.setException(ex);
                    }
                });

            } catch (Throwable ex) { //NOSONAR
                tcs.setException(ex);
            }
        });

        return tcs;
    }

    public static <T> void waitAll(ListenableFuture<? extends T>... tasks)
            throws ExecutionException, InterruptedException {
        ListenableFuture allTask = whenAll(tasks);
        allTask.get();
    }

    public static <T> void waitAll(List<ListenableFuture<? extends T>> tasks)
            throws ExecutionException, InterruptedException {
        ListenableFuture allTask = whenAll(tasks);
        allTask.get();
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed,
     * if any futures fail the All-Futrue will not complete until all futures completed.
     *
     * @param tasks
     * @param <T>
     * @return
     */
    public static <T> ListenableFuture<List<T>> whenAll(List<? extends ListenableFuture<? extends T>> tasks) {
        return WhenAllTaskHelper.whenAll(tasks);
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed,
     * if any futures fail the All-Futrue will not complete until all futures completed.
     *
     * @param tasks
     * @param <T>
     * @return
     */
    public static <T> ListenableFuture<List<T>> whenAll(ListenableFuture<? extends T>... tasks) {
        return WhenAllTaskHelper.whenAll(tasks);
    }
}
