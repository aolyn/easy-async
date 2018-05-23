package chris.context.test;

import com.google.common.util.concurrent.ListenableFuture;
import org.aolyn.concurrent.TaskUtils;
import org.aolyn.concurrent.context.CallContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ContextTest {

    private static String testContextDataKey = "ListenableFutureTest_testContextDataKey";

    @Test
    public void trackableExecutorTest() {
        Object contextValue = "context1-2-3";
        CallContext.setData(testContextDataKey, contextValue);

        Object contextValue1 = CallContext.getData(testContextDataKey);
        Assert.assertEquals(contextValue, contextValue1);

        ListenableFuture<String> task1 = TaskUtils.run(() -> {
            Object context = CallContext.getData(testContextDataKey);
            Assert.assertEquals(contextValue, context);

            return "hello ";
        });

        ListenableFuture task2 = TaskUtils.continueWith(task1, tsk -> {
            Object context = CallContext.getData(testContextDataKey);
            Assert.assertEquals(contextValue, context);
        });

        try {
            task2.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void defaultExecutorTest() throws ExecutionException, InterruptedException {
        Object contextValue = "context1-2-3";
        CallContext.setData(testContextDataKey, contextValue);

        ListenableFuture<String> task1 = TaskUtils.run(() -> {
            Object context = CallContext.getData(testContextDataKey);
            Assert.assertEquals(contextValue, context);

            return "hello";
        });

        ListenableFuture task2 = TaskUtils.continueWith(task1, tsk -> {
            Assert.assertEquals("hello", tsk.get());
            Object context = CallContext.getData(testContextDataKey);
            Assert.assertEquals(contextValue, context);
            return "hello";
        });

        Assert.assertEquals("hello", task2.get());
    }

    @Test
    public void defaultExecutorTest22() throws ExecutionException, InterruptedException {
        Object contextValue = "context1-2-3";
        CallContext.setData(testContextDataKey, contextValue);

        ListenableFuture<String> task1 = TaskUtils.run(() -> {
            Thread.sleep(50);
            Object context = CallContext.getData(testContextDataKey);
            Assert.assertEquals(contextValue, context);

            return "hello";
        });
        CallContext.clear();
        task1.get();
    }

    @Test
    public void fromResultTest() throws ExecutionException, InterruptedException {
        Object contextValue = "context1-2-3";
        CallContext.setData(testContextDataKey, contextValue);

        ListenableFuture<String> task1 = TaskUtils.fromResult("test11--213");

        ListenableFuture task2 = TaskUtils.continueWith(task1, tsk -> {
            Object context = CallContext.getData(testContextDataKey);
            Assert.assertEquals(contextValue, context);
        });

        task2.get();
    }

    @Test
    public void run2Test() throws ExecutionException, InterruptedException {
        TaskUtils.getProvider().setDefaultExecutor(Executors.newFixedThreadPool(1));

        for (int i = 0; i < 9; i++) {
            final int loopIndex = i;
            final Object contextValue = "context1-" + Integer.toString(i);
            CallContext.setData(testContextDataKey, contextValue);

            ListenableFuture<String> task1 = TaskUtils.run(() -> {
                System.out.println();
                Object context = CallContext.getData(testContextDataKey);
                Assert.assertEquals(contextValue, context);

                return "hello ";
            });

            task1.get();
        }
    }

    @Test
    public void runContextRemoveTest() throws ExecutionException, InterruptedException {
        TaskUtils.getProvider().setDefaultExecutor(Executors.newFixedThreadPool(2));
        CallContext.setContext(null);
        for (int i = 0; i < 9; i++) {
            final int loopIndex = i;
            TaskUtils.run(() -> {
                Object currentContext = CallContext.getData(testContextDataKey);
                Assert.assertEquals(null, currentContext);

                final Object contextValue = "context1-" + Integer.toString(loopIndex);
                CallContext.setData(testContextDataKey, contextValue);

                ListenableFuture<String> task1 = TaskUtils.run(() -> {
                    Object context = CallContext.getData(testContextDataKey);
                    if (!contextValue.equals(context)) {
                        System.out.println("context not equals");
                    }
                    Assert.assertEquals(contextValue, context);

                    return "hello ";
                });

                try {
                    task1.get();
                } catch (Exception ex) {

                }
            }).get();
        }
    }

    @Test
    public void listenableTest() {
        ListenableFuture<String> task1 = TaskUtils.fromResult("test11--213");
        Thread thread = Thread.currentThread();

        ListenableFuture task2 = TaskUtils.continueWith(task1, tsk -> {
            Object context = CallContext.getData(testContextDataKey);
            Assert.assertEquals(null, context);
        });

        try {
            task2.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

