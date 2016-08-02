package chris;

import org.aolyn.concurrent.TaskUtils;
import org.aolyn.concurrent.context.CallContext;
import com.google.common.util.concurrent.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

//http://greengerong.github.io/blog/2014/11/21/guava-bing-xing-bian-cheng-futures/
//http://www.cnblogs.com/whitewolf/p/4113860.html

/**
 * Created by Chris Huang on 2017-06-03.
 */
public class ListenableFutureTest {
    private static String testContextDataKey = "ListenableFutureTest_testContextDataKey";

    //TaskCompletationSource - new CompletableFuture<>()
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        ListenableFuture<String> future = Futures.immediateFuture("3");

        String result = future.get();

        Assert.assertEquals(result, "3");
    }

    @Test
    public void transformTest() throws Exception {

        ListenableFuture future1 = Futures.immediateFuture(2);

        ListenableFuture<String> transform = Futures.transformAsync(future1, (AsyncFunction<Integer, String>) results -> {
            System.out.println(results);
            return Futures.immediateFuture("Hello");
        });
        System.out.println(transform.get());
    }


    @Test
    public void addLtest1()
        throws Exception {
        SettableFuture<String> future = SettableFuture.create();
        future.setException(new Exception("test ex"));

        //Futures.addCallback();


        System.out.println("end");
    }

    @Test
    public void transformExceptionTest() throws Exception {

        SettableFuture<Integer> future1 = SettableFuture.create();
        future1.setException(new NullPointerException("test ex"));

        ListenableFuture<String> transform = Futures.transformAsync(future1, (AsyncFunction<Integer, String>) results -> {
            System.out.println(results);
            return Futures.immediateFuture("Hello");
        });

        try {
            transform.get();
        } catch (NullPointerException ex) {
            System.out.println(ex);
        } catch (ExecutionException ex) {
            System.out.println(ex);
            Throwable ex2 = ex.getCause();
            System.out.println(ex2);
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    @Test
    public void should_test_furture() throws Exception {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        CallContext.setData(testContextDataKey, "context-1-2-3");
        ListenableFuture future1 = service.submit(() -> {
            //Thread.sleep(10);
            System.out.println("call future 1.");
            return 1;
        });

        ListenableFuture future2 = service.submit(() -> {
            //Thread.sleep(10);
            System.out.println("call future 2.");
            return 2;
        });

        ListenableFuture allFutures = Futures.allAsList(future1, future2);

        ListenableFuture<String> transform = Futures.transformAsync(allFutures,
            (AsyncFunction<List<Integer>, String>) results -> {
                System.out.println(results.get(0));
                //Thread.sleep(3000);
                return Futures.immediateFuture("Hello");
            });

        Futures.addCallback(transform, new FutureCallback<Object>() {

            public void onSuccess(Object result) {
                System.out.println(result.getClass());
                System.out.printf("success with: %s%n", result);
            }

            public void onFailure(Throwable thrown) {
                System.out.printf("onFailure%s%n", thrown.getMessage());
            }
        });

        System.out.println("wait");
        System.out.println(transform.get());
    }


//
//    @Test
//    public void allAsListExceptionTest() throws Exception {
//
//        SettableFuture<Integer> future1 = SettableFuture.create();
//        future1.setException(new NullPointerException("test ex"));
//
//        ListenableFuture<String> transform = Futures.transformAsync(future1, (AsyncFunction<Integer, String>) results -> {
//            System.out.println(results);
//            return Futures.immediateFuture("Hello");
//        });
//
//        try {
//            transform.get();
//        } catch (NullPointerException ex) {
//            System.out.println(ex);
//        } catch (ExecutionException ex) {
//            System.out.println(ex);
//            Throwable ex2 = ex.getCause();
//            System.out.println(ex2);
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//
//    }


    @Test
    public void waitAllExceptionTest1() {
        ListenableFuture f1 = TaskUtils.run(() -> {
            throw new IllegalAccessException("tex2");
        });

        ListenableFuture<Integer> f2 = TaskUtils.fromResult(2);

        ListenableFuture<List<Integer>> allTask = Futures.allAsList(f1, f2);
        try {
            List<Integer> results = allTask.get();
            Assert.assertTrue(false);
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("--------------------");
            //ex.getCause().printStackTrace();
        }
    }

    @Test
    public void waitAllExceptionTest2() {
        SettableFuture settableFuture = SettableFuture.create();

        final IllegalAccessException exception = new IllegalAccessException("tex2");
        settableFuture.setException(exception);

        try {
            Object results = settableFuture.get();
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertTrue(ex != exception);
            Assert.assertTrue(ex.getCause() == exception);
            //ex.getCause().printStackTrace();
        }
    }

    @Test
    public void multi_test_furture() throws Exception {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        CallContext.setData(testContextDataKey, "context-1-2-3");
        ListenableFuture future1 = service.submit(() -> {
            //Thread.sleep(10);
            System.out.println("call future 1.");
            return 1;
        });

        ListenableFuture future2 = service.submit(() -> {
            //Thread.sleep(10);
            System.out.println("call future 2.");
            return 2;
        });

        ListenableFuture allFutures = Futures.allAsList(future1, future2);

        ListenableFuture<String> transform = Futures.transformAsync(allFutures,
            (AsyncFunction<List<Integer>, String>) results -> {
                System.out.println(results.get(0));
                //Thread.sleep(3000);
                return Futures.immediateFuture("Hello");
            });

        Futures.addCallback(transform, new FutureCallback<Object>() {

            public void onSuccess(Object result) {
                System.out.println(result.getClass());
                System.out.printf("success with: %s%n", result);
            }

            public void onFailure(Throwable thrown) {
                System.out.printf("onFailure%s%n", thrown.getMessage());
            }
        });

        System.out.println("wait");
        System.out.println(transform.get());
    }


    public static void main(String[] argv) {
//        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
//        httpclient.start();
//
//        final CountDownLatch latch = new CountDownLatch(1);
//        final HttpGet request = new HttpGet("https://www.alipay.com/");
//
//        System.out.println(" caller thread id is : " + Thread.currentThread().getId());
//
//        httpclient.execute(request, new FutureCallback<HttpResponse>() {
//
//            public void completed(final HttpResponse response) {
//                latch.countDown();
//                System.out.println(" callback thread id is : " + Thread.currentThread().getId());
//                System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
//                try {
//                    String content = EntityUtils.toString(response.getEntity(), "UTF-8");
//                    System.out.println(" response content is : " + content);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            public void failed(final Exception ex) {
//                latch.countDown();
//                System.out.println(request.getRequestLine() + "->" + ex);
//                System.out.println(" callback thread id is : " + Thread.currentThread().getId());
//            }
//
//            public void cancelled() {
//                latch.countDown();
//                System.out.println(request.getRequestLine() + " cancelled");
//                System.out.println(" callback thread id is : " + Thread.currentThread().getId());
//            }
//
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            httpclient.close();
//        } catch (IOException ignore) {
//
//        }
    }

//    @Test
//    public void allAsListExceptionTest1() throws Exception {
//        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
//
//        ListenableFuture future1 = service.submit(() -> {
//            Thread.sleep(100);
//            System.out.println("call future 1.");
//            if (Calendar.getInstance().getWeekYear() > 0) {
//                throw new NullPointerException("test ex");
//            }
//            return 1;
//        });
//
//        ListenableFuture future2 = service.submit(() -> {
//            Thread.sleep(100);
//            System.out.println("call future 2.");
//            return 2;
//        });
//
//        ListenableFuture allFutures = Futures.allAsList(future1, future2);
//
//        ListenableFuture<String> transform = Futures.transformAsync(allFutures,
//                (AsyncFunction<List<Integer>, String>) results -> {
//                    try {
//                        System.out.println(results.get(0));
//                    } catch (Exception ex) {
//
//                    }
//
//                    return Futures.immediateFuture("Hello");
//                });
//
//        try {
//            transform.get();
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
//
//
//        Futures.addCallback(transform, new FutureCallback<Object>() {
//
//            public void onSuccess(Object result) {
//                System.out.println(result.getClass());
//                System.out.printf("success with: %s%n", result);
//            }
//
//            public void onFailure(Throwable thrown) {
//                System.out.printf("onFailure%s%n", thrown.getMessage());
//            }
//        });
//
//        System.out.println(transform.get());
//    }

}
