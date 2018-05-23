package chris;

import org.aolyn.concurrent.guava.TaskUtils;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ListenableFutureAsListTest {

    private Object allAsListFailsTest_locker = new Object();
    private String allAsListFailsTest_Result = "";

    @Test
    public void allAsListFailsTest() throws Exception {
        ListenableFuture<Integer> f1 = TaskUtils.run(() -> {
            Thread.sleep(500);
            synchronized (allAsListFailsTest_locker) {
                allAsListFailsTest_Result += "f1";
            }
            return 1;
        });
        ListenableFuture<Integer> f2 = TaskUtils.fromException(new Exception("test"));

        ListenableFuture<List<Integer>> allTask = Futures.allAsList(f1, f2);

        ListenableFuture nextTask = TaskUtils.continueWith(allTask, tsk -> {
            Assert.assertTrue(tsk.isDone() == true);
            try {
                tsk.get();
                Assert.assertTrue(false);
            } catch (Throwable ex) {
            }

            synchronized (allAsListFailsTest_locker) {
                allAsListFailsTest_Result += "fn";
            }
        });

        nextTask.get();
        f1.get();

        Assert.assertEquals("fnf1", allAsListFailsTest_Result);
    }


    private Object successfulAsListFailTest_locker = new Object();
    private String successfulAsListFailTest_Result = "";

    @Test
    public void successfulAsListFailTest() throws Exception {
        ListenableFuture<Integer> f1 = TaskUtils.run(() -> {
            Thread.sleep(100);
            synchronized (successfulAsListFailTest_locker) {
                successfulAsListFailTest_Result += "f1";
            }
            return 1;
        });
        ListenableFuture<Integer> f2 = TaskUtils.fromException(new Exception("test"));

        ListenableFuture<List<Integer>> allTask = Futures.successfulAsList(f1, f2);

        ListenableFuture nextTask = TaskUtils.continueWith(allTask, tsk -> {
            synchronized (successfulAsListFailTest_locker) {
                successfulAsListFailTest_Result += "f2";
            }

            List<Integer> results = tsk.get();
            Assert.assertEquals(2, results.size());
            Assert.assertEquals(1, (long) results.get(0));
            Assert.assertTrue(results.get(1) == null);
        });

        nextTask.get();
        f1.get();

        Assert.assertEquals("f1f2", successfulAsListFailTest_Result);
    }
}
