package org.aolyn.concurrent;

import org.aolyn.concurrent.context.CallContext;
import com.google.common.util.concurrent.*;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Chris Huang on 2017-05-31.
 */
public final class TaskUtils {
    private static Executor defaultExecutor = null;

    public static ListenableFuture CompletedTask = fromResult(0);
    // public static final TaskUtilOptions Options = new TaskUtilOptions();

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

    public static void setDefaultExecutor(Executor executor) {
        defaultExecutor = executor;
    }

    public static void execute(Runnable runable) {
        getExecutor().execute(new RunableHolder(runable));
    }

    public static ListenableFutureTask run(Runnable runable) {
        Runnable runnableWraper = new RunableHolder(runable);
        ListenableFutureTask<Integer> futureTask = ListenableFutureTask.create(() -> {
            runnableWraper.run();
            return 0;
        });
        getExecutor().execute(futureTask);
        return futureTask;
    }

    public static <V> ListenableFuture<V> run(Callable<V> callable) {
        Callable<V> callableWraper = new CallableHolder<>(callable);
        ListenableFutureTask<V> future = ListenableFutureTask.create(callableWraper);
        getExecutor().execute(future);
        return future;
    }

    public static <V> ListenableFuture<V> fromResult(V value) {
        ListenableFuture<V> future = Futures.immediateFuture(value);
        return future;
    }

    public static <V> ListenableFuture<V> fromException(Exception exception) {
        SettableFuture<V> task = SettableFuture.create();
        task.setException(exception);
        return task;
    }

    public static <I> ListenableFuture continueWith(
        ListenableFuture<I> input,
        ContinueWithAction<I> action) {
        SettableFuture<Object> tcs = SettableFuture.create();

        final ContinueWithAction<I> actionWraper = new ContinueWithActionHolder<>(action);
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
        final ContinueWithFunction<I, O> actionWraper = new ContinueWithFunctionHolder<>(function);
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

        final ContinueWithTaskFunction<I, O> functionWraper = new ContinueWithTaskFunctionHolder<>(getNextTaskFunc);
        CallContext.setData("TaskUtil.continueWith_logSpan", false);
        continueWith(task, tsk -> {
            try {
                ListenableFuture<O> task3 = functionWraper.apply(tsk);

                CallContext.setData("TaskUtil.continueWith_logSpan", false);
                continueWith(task3, tsk2 -> {
                    try {
                        tcs.set(task3.get());
                    } catch (Throwable ex) { //NOSONAR
                        tcs.setException(ex);
                    }
                });
                CallContext.setData("TaskUtil.continueWith_logSpan", null);

            } catch (Throwable ex) { //NOSONAR
                tcs.setException(ex);
            }
        });
        CallContext.setData("TaskUtil.continueWith_logSpan", null);

        return tcs;
    }

    public static <T> void waitAll(ListenableFuture<T>... tasks)
        throws ExecutionException, InterruptedException {
        ListenableFuture allTask = whenAll(tasks);
        allTask.get();
    }

    public static <T> void waitAll(List<ListenableFuture<T>> tasks)
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
    public static <T> ListenableFuture<List<T>> whenAll(List<ListenableFuture<T>> tasks) {
        ListenableFuture<List<T>> allTask = WhenAllTaskHelper.whenAll(tasks);
        return allTask;
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed,
     * if any futures fail the All-Futrue will not complete until all futures completed.
     *
     * @param tasks
     * @param <T>
     * @return
     */
    public static <T> ListenableFuture<List<T>> whenAll(ListenableFuture<T>... tasks) {
        ListenableFuture<List<T>> allTask = WhenAllTaskHelper.whenAll(tasks);
        return allTask;
    }


    /**
     * create a Future(call it All-Future) which will complete when all the futures completed,
     * if any futures fail the All-Futrue will not complete until all futures completed.
     *
     * @param tasks
     * @return
     */
    public static void whenAll2(ListenableFuture<Object>... tasks) {
        //ListenableFuture<List<T>> allTask = WhenAllTask.whenAll(tasks);
        //return null;
    }
}
