package org.aolyn.concurrent.test;

import org.aolyn.concurrent.guava.TaskUtils;
import org.aolyn.concurrent.context.CallContext;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class TaskUtils_ContinueWithTask_Test {
    private static String testContextDataKey = "ListenableFutureTest_testContextDataKey";

    @Test
    public void continueWithTaskSuccessTest1() throws Exception {
        ListenableFuture<String> future = TaskUtils.fromResult("1");


        ListenableFuture<Object> nextTask = TaskUtils.continueWithTask(future, tsk -> {
            return TaskUtils.continueWith(tsk, tsk2 -> {
                return "hello";
            });
            
        });

        Assert.assertEquals("hello", nextTask.get());
    }

    @Test
    public void continueWithSettableNoResultExceptionTest() throws Exception {
        SettableFuture<String> future = SettableFuture.create();
        //future.setException(new Exception("test ex"));

        final String contextValue = "value231";
        CallContext.setData(testContextDataKey, contextValue);
        Assert.assertEquals(contextValue, CallContext.getData(testContextDataKey));

        future.set("hello");
        ListenableFuture<String> nextTask = TaskUtils.continueWithTask(future, tsk -> {
            try {
                Assert.assertEquals(contextValue, CallContext.getData(testContextDataKey));
                String result = future.get();
                ListenableFuture<String> invokeTask2 = TaskUtils.run(() -> {
                    Assert.assertEquals(contextValue, CallContext.getData(testContextDataKey));
                    return "hello";
                });

                return TaskUtils.continueWith(invokeTask2, tsk2 -> {
                    Assert.assertEquals(contextValue, CallContext.getData(testContextDataKey));
                    return "hello2";
                });
            } catch (Exception ex) {
                Assert.assertEquals(ExecutionException.class.getName(), ex.getClass().getName());
                Assert.assertEquals(NullPointerException.class.getName(), ex.getCause().getClass().getName());
                System.out.println(ex);
                Assert.assertEquals("java.lang.NullPointerException: test ex", ex.getMessage());
            }
            return TaskUtils.fromResult((String) null);
        });

        try {
            String result = nextTask.get();
            Assert.assertEquals("hello2", result);
        } catch (Exception ex) {
            //ex.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
