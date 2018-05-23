package chris;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import org.aolyn.concurrent.guava.TaskUtils;
import org.junit.Test;

/**
 * Created by Chris Huang on 2018-03-21.
 */
public class SynchroizedTest {

    synchronized private void s1() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("s1");
    }

    synchronized private void s2() {
        System.out.println("s2");
    }

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        SynchroizedTest to = new SynchroizedTest();

        ListenableFuture task1 = TaskUtils.run(() -> {
            to.s1();
        });
        Thread.sleep(10);
        ListenableFuture task2 = TaskUtils.run(() -> {
            to.s2();
        });

        TaskUtils.waitAll(task1, task2);
    }
}
