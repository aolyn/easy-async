package org.aolyn.concurrent.test;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.SettableFuture;
import org.aolyn.concurrent.guava.TaskUtils;
import org.junit.Test;

public class TaskUtilsContinueWithResultTest {

    @Test
    public void continueWithResultActionTest() throws ExecutionException, InterruptedException {
        ListenableFuture<Object> task1 = TaskUtils.fromException(new Exception("test"));
        TaskUtils.continueWith(task1, (tsk, result) -> {
            if (!result.isSuccess() && result.getException() != null) {
                return TaskUtils.fromResult(1);
            } else {
                throw new Exception("result error 1");
            }
        }).get();
    }

    @Test
    public void continueWithResultActionTestOld() throws ExecutionException, InterruptedException {
        ListenableFuture<Object> task1 = TaskUtils.fromException(new Exception("test"));
        TaskUtils.continueWith(task1, (tsk) -> {
            try {
                Object object = tsk.get();
                return object;
            } catch (Exception ex) {
                //log
            }
            return null;
        }).get();
    }

    @Test
    public void continueWithResultActionTestNew() throws ExecutionException, InterruptedException {
        ListenableFuture<Object> task1 = TaskUtils.fromException(new Exception("test"));
        TaskUtils.continueWith(task1, (tsk, result) -> {
            if(result.isSuccess()){
                return tsk.get();
            }
            //log
            return null;
        }).get();
    }

    @Test
    public void continueWithResultActionTest2() throws ExecutionException, InterruptedException {
        ListenableFuture<Object> task1 = SettableFuture.create();
        task1.cancel(false);
        TaskUtils.continueWith(task1, (tsk, result) -> {
            if (!result.isSuccess() && result.getException() != null) {
                return TaskUtils.fromResult(1);
            } else {
                throw new Exception("result error 1");
            }
        }).get();
    }

    @Test
    public void continueWithResultActionTest3() throws ExecutionException, InterruptedException {
        ListenableFuture<Object> task1 = SettableFuture.create();
        task1.cancel(true);
        TaskUtils.continueWith(task1, (tsk, result) -> {
            if (!result.isSuccess() && result.getException() != null) {
                return TaskUtils.fromResult(1);
            } else {
                throw new Exception("result error 1");
            }
        }).get();
    }

    @Test
    public void continueWithResultFunctionTest() throws ExecutionException, InterruptedException {
        ListenableFuture<Object> task1 = TaskUtils.fromException(new Exception("test"));
        TaskUtils.continueWith(task1, (tsk, result) -> {
            if (!result.isSuccess() && result.getException() != null) {

                return TaskUtils.fromResult(1);
            } else {
                throw new Exception("result error 1");
            }
        }).get();
    }

    @Test
    public void continueWithResultTaskFunctionTest() throws ExecutionException, InterruptedException {
        ListenableFuture<Object> task1 = TaskUtils.fromException(new Exception("test"));
        TaskUtils.continueWithTask(task1, (tsk, result) -> {
            if (result.isSuccess()) {
                return TaskUtils.fromResult(1);
            } else {
                return TaskUtils.fromResult(2);
            }
        }).get();
    }

}
