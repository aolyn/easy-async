package chris;

import org.aolyn.concurrent.guava.TaskUtils;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public class FutureTest {

    @Test
    public void test1()
            throws IOException {

        List<Integer> cityIdPages = new ArrayList<Integer>();
        cityIdPages.add(0);
        cityIdPages.add(1);

        try {

            CountDownLatch doneSignal = new CountDownLatch(cityIdPages.size());

            cityIdPages.forEach(i -> {
                try {

                    FutureTask<Integer> feature = new FutureTask<Integer>(() -> {
                        //Thread.sleep(100);
                        System.out.println("page " + i);
                        doneSignal.countDown();
                        return 1;
                    });
                    TaskUtils.execute(feature);

                    // tasks.add(feature);
                } catch (Exception ignored) {
                }
            });

            CompletableFuture.allOf(null);
            doneSignal.await();
            System.out.println("finished success");
        } catch (Exception ex) {
            //LogHelper.warn("GetStartCities - ConvertTourCityIdToDistrictIdAsync", ex, LogErrorCode.GetStartCitiesGetDistrictIdToCityIdMapping);
            System.out.println(ex);
        }

        System.out.println("end");
    }

    @Test
    public void waitAllTest1()
            throws IOException {

        List<Integer> cityIdPages = new ArrayList<Integer>();
        cityIdPages.add(0);
        cityIdPages.add(1);

        try {
            List<ListenableFuture<Integer>> futures = new ArrayList<>();

            cityIdPages.forEach(i -> {
                try {

                    ListenableFutureTask<Integer> futureTask = ListenableFutureTask.create(() -> {
                        //Thread.sleep(30);
                        System.out.println("page " + i);
                        return 1;
                    });
                    TaskUtils.execute(futureTask);

                    futures.add(futureTask);
                } catch (Exception ignored) {
                }
            });

            //(E[]) Array.newInstance(c, s);
            ListenableFuture<Integer>[] zeroArray = (ListenableFuture<Integer>[]) Array.newInstance(ListenableFuture.class, 0);
            ListenableFuture<Integer>[] futureTasks = futures.toArray(zeroArray);
            //ListenableFuture<Integer>[] futureTasks = futures.toArray((ListenableFuture<Integer>[]) null);
            TaskUtils.whenAll(futureTasks).get();


            ListenableFuture<List<Integer>> f2 = Futures.allAsList(futures);
            ListenableFuture<Object> f3 = Futures.transformAsync(f2, integers -> {
                return null;
            });

            System.out.println("finished success");
        } catch (Exception ex) {
            System.out.println(ex);
        }

        System.out.println("end");
    }
}