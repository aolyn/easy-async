package org.aolyn.concurrent.guava;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.aolyn.concurrent.CallableHolder;
import org.aolyn.concurrent.ContinueWithResult;
import org.aolyn.concurrent.RunnableFilter;
import org.aolyn.concurrent.RunnableHolder;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public final class TaskUtilsProvider {

    private Executor defaultExecutor;
    private Executor continueWithExecutor;
    private RunnableFilter filter;

    public TaskUtilsProvider() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("TaskUtil-DefaultExecutor-%d")
            .build();

        defaultExecutor = new ThreadPoolExecutor(32, 1024,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
    }

    /**
     * get executor used for continueWith
     * @return Executor
     */
    private Executor getContinueWithExecutor() {
        return continueWithExecutor != null
            ? continueWithExecutor
            : getDefaultExecutor();
    }

    /**
     * set executor for continueWith, if it's null default executor will be used
     *
     * @param continueWithExecutor executor used for continueWith
     */
    public void setContinueWithExecutor(Executor continueWithExecutor) {
        this.continueWithExecutor = continueWithExecutor;
    }

    private Executor getDefaultExecutor() {
        if (defaultExecutor != null) {
            return defaultExecutor;
        }

        return MoreExecutors.directExecutor();
    }

    /**
     * set default executor for TaskUtils, if is null MoreExecutors.directExecutor() will be used,
     * if continueWithExecutor not set default executor will also be used to continueWith
     *
     * @param executor default executor
     */
    public void setDefaultExecutor(Executor executor) {
        defaultExecutor = executor;
    }

    /**
     * set filter
     * @param filter filter
     */
    public void setFilter(RunnableFilter filter) {
        this.filter = filter;
    }

    /**
     * execute a runnable by default executor
     *
     * @param runnable runnable to execute
     */
    public void execute(Runnable runnable) {
        getDefaultExecutor().execute(new RunnableHolder(runnable, filter));
    }

    /**
     * execute a runnable by default executor
     *
     * @param runnable runnable to execute
     * @return future
     */
    public ListenableFuture run(Runnable runnable) {
        Runnable runnableWrapper = new RunnableHolder(runnable, filter);
        ListenableFutureTask<Integer> futureTask = ListenableFutureTask.create(() -> {
            runnableWrapper.run();
            return 0;
        });
        getDefaultExecutor().execute(futureTask);
        return futureTask;
    }

    /**
     * execute a Callable with return value by default executor
     *
     * @param callable callable to execute
     * @param <V> return class of callable
     * @return ListenableFuture of V with Callable's result
     */
    public <V> ListenableFuture<V> run(Callable<V> callable) {
        Callable<V> callableWrapper = new CallableHolder<>(callable, filter);
        ListenableFutureTask<V> future = ListenableFutureTask.create(callableWrapper);
        getDefaultExecutor().execute(future);
        return future;
    }

    /**
     * get a finished future with result of specific value
     *
     * @param value future result value
     * @param <V> future result class
     * @return specific value in arguments
     */
    public <V> ListenableFuture<V> fromResult(V value) {
        return Futures.immediateFuture(value);
    }

    /**
     * get a finished future with specific exception
     *
     * @param exception future result exception
     * @param <V> exception class
     * @return specific exception in arguments
     */
    public <V> ListenableFuture<V> fromException(Exception exception) {
        SettableFuture<V> task = SettableFuture.create();
        task.setException(exception);
        return task;
    }

    /**
     * continueWith
     * @param input input future
     * @param action continuation action
     * @param <I> the return type of input future
     * @return future
     */
    public <I> ListenableFuture continueWith(
        ListenableFuture<I> input,
        ContinueWithAction<I> action) {
        SettableFuture<Object> tcs = SettableFuture.create();

        final ContinueWithAction<I> actionWraper = new ContinueWithActionHolder<>(action, filter);
        input.addListener(() -> {
            boolean isSet = false;

            try {
                actionWraper.apply(input);
                tcs.set(null);
                isSet = true;
            } catch (Throwable ex) {
                tcs.setException(ex);
                isSet = true;
            } finally {
                if (!isSet) {
                    tcs.setException(new Exception("unknown continueWith exception"));
                    //todo:
                }
            }
        }, getContinueWithExecutor());

        return tcs;
    }

    public <I, O> ListenableFuture<O> continueWith(
        ListenableFuture<I> input,
        ContinueWithFunction<I, O> function) {

        SettableFuture<O> tcs = SettableFuture.create();
        final ContinueWithFunction<I, O> actionWraper = new ContinueWithFunctionHolder<>(function, filter);
        input.addListener(() -> {
            boolean isSet = false;

            try {
                O result = actionWraper.apply(input);
                tcs.set(result);
                isSet = true;
            } catch (Throwable ex) {
                tcs.setException(ex);
                isSet = true;
            } finally {
                if (!isSet) {
                    tcs.setException(new Exception("unknown continueWith exception"));
                    //todo:
                }
            }

        }, getContinueWithExecutor());

        return tcs;
    }

    public <I, O> ListenableFuture<O> continueWithTask(ListenableFuture<I> task,
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
                    } catch (Throwable ex) {
                        tcs.setException(ex);
                    }
                });

            } catch (Throwable ex) {
                tcs.setException(ex);
            }
        });

        return tcs;
    }

    //continue with result functions
    public <I> ListenableFuture continueWith(
        ListenableFuture<I> input,
        ContinueWithResultAction<I> action) {
        SettableFuture<Object> tcs = SettableFuture.create();

        final ContinueWithResultActionHolder<I> actionWraper = new ContinueWithResultActionHolder<>(action, filter);
        input.addListener(() -> {
            boolean isSet = false;

            Exception exception = null;
            try {
                input.get();
            } catch (ExecutionException | InterruptedException | CancellationException ex) {
                exception = ex;
            }

            ContinueWithResult result = new ContinueWithResult(exception == null, exception);
            try {
                actionWraper.apply(input, result);
                tcs.set(null);
                isSet = true;
            } catch (Throwable ex) {
                tcs.setException(ex);
                isSet = true;
            } finally {
                if (!isSet) {
                    tcs.setException(new Exception("unknown continueWith exception"));
                    //todo:
                }
            }
        }, getContinueWithExecutor());

        return tcs;
    }

    public <I, O> ListenableFuture<O> continueWith(
        ListenableFuture<I> input,
        ContinueWithResultFunction<I, O> function) {

        SettableFuture<O> tcs = SettableFuture.create();
        final ContinueWithResultFunctionHolder<I, O> actionWraper = new ContinueWithResultFunctionHolder<>(function,
            filter);
        input.addListener(() -> {
            boolean isSet = false;

            Exception exception = null;
            try {
                input.get();
            } catch (ExecutionException | InterruptedException | CancellationException ex) {
                exception = ex;
            }

            ContinueWithResult tskresult = new ContinueWithResult(exception == null, exception);

            try {
                O result = actionWraper.apply(input, tskresult);
                tcs.set(result);
                isSet = true;
            } catch (Throwable ex) {
                tcs.setException(ex);
                isSet = true;
            } finally {
                if (!isSet) {
                    tcs.setException(new Exception("unknown continueWith exception"));
                    //todo:
                }
            }

        }, getContinueWithExecutor());

        return tcs;
    }

    public <I, O> ListenableFuture<O> continueWithTask(ListenableFuture<I> task,
        ContinueWithResultTaskFunction<I, O> getNextTaskFunc) {
        SettableFuture<O> tcs = SettableFuture.create();

        final ContinueWithResultTaskFunction<I, O> functionWraper = new ContinueWithResultTaskFunctionHolder<>(
            getNextTaskFunc, filter);
        //CallContext.setData(ContinueWithSpanSwitchSetter.LOG_SPAN_SWITCHER_NAME, false);
        continueWith(task, tsk -> {
            try {

                Exception exception = null;
                try {
                    task.get();
                } catch (ExecutionException | InterruptedException | CancellationException ex) {
                    exception = ex;
                }

                ContinueWithResult tskresult = new ContinueWithResult(exception == null, exception);

                ListenableFuture<O> task3 = functionWraper.apply(tsk, tskresult);

                //CallContext.setData(ContinueWithSpanSwitchSetter.LOG_SPAN_SWITCHER_NAME, false);
                continueWith(task3, tsk2 -> {
                    try {
                        tcs.set(task3.get());
                    } catch (Throwable ex) {
                        tcs.setException(ex);
                    }
                });
                //CallContext.setData(ContinueWithSpanSwitchSetter.LOG_SPAN_SWITCHER_NAME, null);

            } catch (Throwable ex) {
                tcs.setException(ex);
            }
        });
        //CallContext.setData(ContinueWithSpanSwitchSetter.LOG_SPAN_SWITCHER_NAME, null);

        return tcs;
    }

    public <T> void waitAll(ListenableFuture<? extends T>... tasks)
        throws ExecutionException, InterruptedException {
        ListenableFuture allTask = whenAll(tasks);
        allTask.get();
    }

    public <T> void waitAll(List<ListenableFuture<? extends T>> tasks)
        throws ExecutionException, InterruptedException {
        ListenableFuture allTask = whenAll(tasks);
        allTask.get();
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed, if any futures fail the
     * All-Futrue will not complete until all futures completed.
     *
     * @param tasks input tasks
     * @param <T> type to return
     * @return future
     */
    public <T> ListenableFuture<List<T>> whenAll(List<? extends ListenableFuture<? extends T>> tasks) {
        return WhenAllTaskHelper.whenAll(tasks);
    }

    /**
     * create a Future(call it All-Future) which will complete when all the futures completed, if any futures fail the
     * All-Futrue will not complete until all futures completed.
     *
     * @param tasks input tasks
     * @param <T> type to return
     * @return future
     */
    public <T> ListenableFuture<List<T>> whenAll(ListenableFuture<? extends T>... tasks) {
        return WhenAllTaskHelper.whenAll(tasks);
    }
}
