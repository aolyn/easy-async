package chris;

import org.aolyn.concurrent.TaskUtils;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClassLockTest {
    private List<String> list = new ArrayList<>();

    @Test
    public void test() throws ExecutionException, InterruptedException {
        ListenableFutureTask task1 = TaskUtils.run(() -> {
            lock1(1 * 10);
        });

        ListenableFutureTask task2 = TaskUtils.run(() -> {
            lock2();
        });

        TaskUtils.waitAll(task1, task2);
        Assert.assertEquals("lock1", list.get(0));
        Assert.assertEquals("lock2", list.get(1));
    }

    public synchronized void lock1(int ms) {
        try {
            Thread.sleep(ms);
            list.add("lock1");
            System.out.println("lock1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void lock2() {
        System.out.println("lock2");
        list.add("lock2");
    }
}
