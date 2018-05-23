package chris;

import org.aolyn.concurrent.TaskUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskUtilsTest2 {
    public static void main(String[] args) throws Exception {
//        ExecutorService executorService = setExecutorService();
//        TaskUtils.setDefaultExecutor(executorService);

        List<ListenableFuture<Object>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final int index = i;
            ListenableFuture<Object> task = TaskUtils.run(() -> {
                try {
                    if (Math.floorMod(index, 10) == 0) {
                        Thread.sleep(300 * 1000);
                    } else {
                        Thread.sleep(5 * 1000);
                    }
                    System.out.println(index);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return new Object();
            });
            tasks.add(task);
        }

        System.out.println(tasks.size());

        try {
            TaskUtils.whenAll(tasks).get();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static ExecutorService setExecutorService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("TaskUtils-DefaultExecutor-%d")
            .build();
        //ExecutorService threadPool = Executors.newCachedThreadPool(threadFactory);
        return new ThreadPoolExecutor(32, 1024,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), threadFactory);
    }
}
