package chris;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutionException;
import org.aolyn.concurrent.guava.TaskUtils;
import org.aolyn.concurrent.guava.TaskUtilsProvider;
import org.junit.Assert;
import org.junit.Test;

public class ExecutorTest {

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        TaskUtilsProvider provider = new TaskUtilsProvider();
        provider.setContinueWithExecutor(MoreExecutors.directExecutor());
        TaskUtils.setProvider(provider);

        final long threadId = Thread.currentThread().getId();
        ListenableFuture<Integer> task1 = TaskUtils.fromResult(1);
        ListenableFuture processTask = TaskUtils.continueWith(task1, task -> {
            long threadId2 = Thread.currentThread().getId();
            Assert.assertEquals(threadId, threadId2);
        });

        processTask.get();
    }

}
