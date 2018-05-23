package chris;

import org.aolyn.concurrent.guava.TaskUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public class TaskUtilsTest {

    @Test
    public void runTimeTest() throws Exception {
        TaskUtils.run(() -> {

        }).get(); //init

        Date point1 = new Date();

        int sleepms = 50;
        ListenableFuture task1 = TaskUtils.run(() -> {
//            try {
//                Thread.sleep(sleepms);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        });

        ListenableFuture task2 = TaskUtils.run(() -> {
//            try {
//                Thread.sleep(sleepms);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        });

        ListenableFuture allTask = TaskUtils.whenAll(task1, task2);

        ListenableFuture<Integer> waitTask = TaskUtils.continueWith(allTask, tsk -> {
            Date pointInWait = new Date();
            long durationWait = pointInWait.getTime() - point1.getTime();
            System.out.println(durationWait);
            Assert.assertTrue(durationWait < sleepms * 2);
            return 1;
        });

        waitTask.get();
    }

    @Test
    public void continueWithSucessTest1() throws Exception {
        ListenableFuture<String> future = TaskUtils.fromResult("1");
        ListenableFuture<Object> nextTask = TaskUtils.continueWith(future, tsk -> {
            try {
                String result = tsk.get();
                return "prev success";
            } catch (Exception ex) {

            }
            return null;
        });

        Assert.assertEquals("prev success", nextTask.get());
    }

    @Test
    public void continueWithNoResultSucessTest() throws Exception {
        ListenableFuture<String> future = TaskUtils.fromResult("1");

        ListenableFuture nextTask = TaskUtils.continueWith(future, tsk -> {
            String result = tsk.get();
            System.out.println("sucess");
            Assert.assertEquals("1", result);
        });

        nextTask.get();
    }

    @Test
    public void continueWithNoResultExceptionTest() throws Exception {
        ListenableFuture<String> future = TaskUtils.run(() -> {
            if (Calendar.getInstance().getTime().getTime() > 0) {
                throw new NullPointerException("test ex");
            }
            return "0";
        });

        ListenableFuture nextTask = TaskUtils.continueWith(future, tsk -> {
            try {
                String result = future.get();
                Assert.assertTrue(false);
            } catch (Exception ex) {
                Assert.assertEquals(ExecutionException.class.getName(), ex.getClass().getName());
                Assert.assertEquals(NullPointerException.class.getName(), ex.getCause().getClass().getName());
                System.out.println(ex);
                Assert.assertEquals("java.lang.NullPointerException: test ex", ex.getMessage());
            }
        });

        nextTask.get();
    }


    @Test
    public void continueWithSettableNoResultExceptionTest() throws Exception {
        SettableFuture<String> future = SettableFuture.create();
        future.setException(new Exception("test ex"));
        //future.set("hello");
        ListenableFuture nextTask = TaskUtils.continueWith(future, tsk -> {
            try {
                String result = future.get();
                Assert.assertTrue(false);
            } catch (Exception ex) {
                Assert.assertEquals(ExecutionException.class.getName(), ex.getClass().getName());
                Assert.assertEquals(NullPointerException.class.getName(), ex.getCause().getClass().getName());
                System.out.println(ex);
                Assert.assertEquals("java.lang.NullPointerException: test ex", ex.getMessage());
            }
        });

        try {
            nextTask.get();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @Test
    public void continueWithSucessStringTest11() throws Exception {
        ListenableFuture future = TaskUtils.run(() -> {
        });

        ListenableFuture<String> nextTask = TaskUtils.continueWith(future, tsk -> {
            future.get();
            return "prev success";
        });

        Assert.assertEquals("prev success", nextTask.get());
    }

    @Test
    public void continueWithSucessStringTest1() throws Exception {
        ListenableFuture<String> future = TaskUtils.fromResult("1");

        ListenableFuture<String> nextTask = TaskUtils.continueWith(future, tsk -> {
            String result = future.get();
            return "prev success";
        });

        Assert.assertEquals("prev success", nextTask.get());
    }

    @Test
    public void continueWithSucessListStringTest1() throws Exception {
        List<String> value = new ArrayList<>();
        value.add("aaaaaa");

        ListenableFuture<List<String>> future = TaskUtils.fromResult(value);

        ListenableFuture<String> nextTask = TaskUtils.continueWith(future, tsk -> {
            List<String> result = future.get();
            return "prev success";
        });

        Assert.assertEquals("prev success", nextTask.get());
    }

    @Test
    public void continueWithSucessListIntegerTest1() throws Exception {
        List<Integer> value = new ArrayList<>();
        value.add(1);

        ListenableFuture<List<Integer>> future = TaskUtils.fromResult(value);

        ListenableFuture<String> nextTask = TaskUtils.continueWith(future, tsk -> {
            List<Integer> result = future.get();
            return "prev success";
        });

        Assert.assertEquals("prev success", nextTask.get());
    }


    @Test
    public void runTest1() throws ExecutionException, InterruptedException {
        ListenableFuture future = TaskUtils.run(() -> {
            System.out.println("run");
        });
        future.get();
    }

    @Test
    public void runWithResultTest3() throws ExecutionException, InterruptedException {
        ListenableFuture<String> future = TaskUtils.run(() -> {
            System.out.println("run");
            return "hello";
        });
        String str1 = future.get();
        Assert.assertEquals("hello", str1);
    }

    @Test
    public void runExceptionTest1() throws ExecutionException, InterruptedException {
        ListenableFuture future = TaskUtils.run(() -> {
            throw new IllegalAccessException("tex2");
        });

        try {
            future.get();
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertEquals("java.lang.IllegalAccessException: tex2", ex.getMessage());
        }
    }

    @Test
    public void continueWithFailedTest1() throws Exception {
        SettableFuture<String> future = SettableFuture.create();
        future.setException(new Exception("tex"));

        ListenableFuture<? extends Object> nextTask = TaskUtils.continueWith(future, tsk -> {
            Boolean isCancelled = tsk.isCancelled();
            try {
                String result = future.get();
                return "prev success";
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
            return "prev failed";
        });

        Assert.assertEquals("prev failed", nextTask.get());
    }

    @Test
    public void whenAllTest1() throws Exception {
        ListenableFuture<Integer> f1 = TaskUtils.fromResult(3);
        ListenableFuture<Integer> f2 = TaskUtils.fromResult(2);
        ListenableFuture<List<Integer>> allTask = TaskUtils.whenAll(f1, f2);

        ListenableFuture<String> nextTask = TaskUtils.continueWith(allTask, tsk -> {
            try {
                List<Integer> result = tsk.get();
                int sum = result.stream().mapToInt(it -> it).sum();
                Assert.assertEquals(5, sum);
                return "success";
            } catch (Exception ex) {

            }
            return null;
        });

        Assert.assertEquals("success", nextTask.get());

        ListenableFuture<Integer> f11 = TaskUtils.fromResult(3);
        ListenableFuture<String> f21 = TaskUtils.fromResult("2");

        TaskUtils.whenAll(new ListenableFuture[]{f11, f21});
    }


    // @Test
    // public void whenAllTest2() throws Exception {
    //     SettableFuture<Object> settableFuture = SettableFuture.create();
    //     List<Integer> map = new ArrayList<>();
    //
    //     ListenableFuture<Integer> f1 = TaskUtils.run(() -> {
    //         return 1;
    //     });
    //
    //
    //     ListenableFuture<Integer> f2 = TaskUtils.run(() -> {
    //         return 2;
    //     });
    //
    //     TaskUtils.continueWith(Futures.allAsList(f1, f2), tsk -> {
    //         map.add(f1.get());
    //         map.add(f2.get());
    //         settableFuture.set("views/HomeIndex");
    //     });
    // }
    //
    //
    // @Test
    // public void whenAllTest3() throws Exception {
    //     SettableFuture<Object> settableFuture = SettableFuture.create();
    //     List<Integer> map = new ArrayList<>();
    //
    //     ListenableFuture<Integer> f1 = TaskUtils.run(() -> {
    //         return 1;
    //     });
    //
    //
    //     ListenableFuture<Integer> f2 = TaskUtils.run(() -> {
    //         return 3;
    //     });
    //
    //
    //     ListenableFuture f3 = TaskUtils.run(() -> {
    //         map.add(3);
    //     });
    //
    //     TaskUtils.continueWith(Futures.allAsList(f1, f2), tsk -> {
    //         map.add(f1.get());
    //         map.add(f2.get());
    //         settableFuture.set("views/HomeIndex");
    //     });
    //
    //
    //     TaskUtils.continueWith(Futures.allAsList(f1, f2), tsk -> {
    //         map.add(f1.get());
    //         map.add(f2.get());
    //     });
    // }

    private volatile boolean isExceptionTaskCompleted;

    @Test
    public void whenAllTestFail2() throws Exception {

        ListenableFuture<Integer> f1 = TaskUtils.run(() -> {
            Thread.sleep(10);
            System.out.println("in ex task");
            isExceptionTaskCompleted = true;
            return 1;
        });
        ListenableFuture<Integer> f2 = TaskUtils.fromException(new Exception("test"));

        ListenableFuture<List<Integer>> allTask = TaskUtils.whenAll(f1, f2);

        ListenableFuture<Boolean> nextTask = TaskUtils.continueWith(allTask, tsk -> {
            System.out.println("in ex continueWith");
            return isExceptionTaskCompleted;
        });

        nextTask.get();
        Assert.assertEquals(true, nextTask.get());
    }

    @Test
    public void whenAllExceptionTest1() throws ExecutionException, InterruptedException {
        ListenableFuture future = TaskUtils.run(() -> {
            throw new IllegalAccessException("tex2");
        });

        ListenableFuture<Integer> f2 = TaskUtils.fromResult(2);

        ListenableFuture allTask = TaskUtils.whenAll(future, f2);

        try {
            Object result = allTask.get();
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertEquals("java.lang.IllegalAccessException: tex2", ex.getMessage());
        }
    }


    @Test
    public void waitAllTest1() throws Exception {
        ListenableFuture<Integer> f1 = TaskUtils.fromResult(3);
        ListenableFuture<Integer> f2 = TaskUtils.fromResult(2);
        TaskUtils.waitAll(f1, f2);

        Assert.assertEquals(5, f1.get() + f2.get());
    }

    @Test
    public void waitAllExceptionTest1() throws ExecutionException, InterruptedException {
        ListenableFuture f1 = TaskUtils.run(() -> {
            throw new IllegalAccessException("tex2");
        });

        ListenableFuture<Integer> f2 = TaskUtils.fromResult(2);

        try {
            TaskUtils.waitAll(f1, f2);
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertEquals(ExecutionException.class, ex.getClass());
            Assert.assertEquals(IllegalAccessException.class, ex.getCause().getClass());
            Assert.assertEquals("tex2", ex.getCause().getMessage());
            //ex.printStackTrace();
            System.out.println("--------------------");
            //ex.getCause().printStackTrace();
        }
    }


    @Test
    public void fromExceptionTest() {
        ListenableFuture<String> task1 = TaskUtils.fromException(new Exception("test"));
        try {
            task1.get();
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertEquals("java.lang.Exception: test", ex.getLocalizedMessage());
        }
    }

}
