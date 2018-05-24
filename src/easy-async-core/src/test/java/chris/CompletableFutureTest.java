package chris;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Chris Huang on 2016-07-16.
 */
public class CompletableFutureTest {

    //TaskCompletationSource - new CompletableFuture<>()
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        future.complete("hello");
        String result = future.get();

        Assert.assertEquals(result, "hello");
    }

    //ContinueWith - new CompletableFuture<>()
    @Test
    public void exceptionTest1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.cancel(true);

        try {
            String result = future.get();
            Assert.assertTrue(false);
        } catch (CancellationException ex) {

        }
        //Thread.sleep(10);
    }

    //ContinueWith - new CompletableFuture<>()
    @Test
    public void handleTest1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        CompletableFuture<String> safe =
                future.exceptionally(ex -> "We have a problem: " + ex.getMessage());

        CompletableFuture<Integer> safe2 = future.handle((ok, ex) -> {
            if (ok != null) {
                return Integer.parseInt(ok);
            } else {
                System.out.println("Problem " + ex);
                return -1;
            }
        });

        future.cancel(true);

        try {
            String result = future.get();
            Assert.assertTrue(false);
        } catch (CancellationException ex) {
        }
        //Thread.sleep(10);
    }

    //ContinueWith - new CompletableFuture<>()
    @Test
    public void handleTest2() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        CompletableFuture<Integer> safe = future.handle((ok, ex) -> {
            if (ok != null) {
                System.out.println("result is " + ok);
                return Integer.parseInt(ok);
            } else {
                System.out.println("problem " + ex);
                return -1;
            }
        });

        future.complete("3");

        try {
            String result = future.get();

        } catch (CancellationException ex) {
            Assert.assertTrue(false);
        }
        //Thread.sleep(10);
    }

    //TaskFromResult - supplyAsync
    @Test
    public void thenSupplyTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "3";
        });

        String result = future.get();

        Assert.assertEquals(result, "3");
    }

    //Task.Run - CompletableFuture.runAsync
    @Test
    public void runAsyncTest1() throws ExecutionException, InterruptedException {

        CompletableFuture future = CompletableFuture.runAsync(() -> {
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("finish");
        });

        future.get();
    }

    //ContinueWith(WhenSuccess) - thenApply
    @Test
    public void thenApplyTest()
            throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "3");

        CompletableFuture<Integer> f2 = future.thenApply(Integer::parseInt);
        CompletableFuture<Double> f3 = f2.thenApply(r -> r * r * Math.PI);
        Double result = f3.get();

        Assert.assertTrue(result.equals(3 * 3 * Math.PI));
    }

    //ContinueWith(WhenSuccess) - thenApply
    @Test
    public void thenApplyExceptionTest()
            throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            /*throw new Exception("test exception");*/
            return "3AS";
        });

        final StringBuilder stringBuilder = new StringBuilder();

        CompletableFuture<Integer> f2 = future.thenApply(s -> {
            stringBuilder.append("P|");
            return Integer.parseInt(s);
        });
        CompletableFuture<Double> f3 = f2.thenApply(r -> {
            stringBuilder.append("R|");
            return r * r * Math.PI;
        });

        try {
            Double result = f3.get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assert.assertEquals(stringBuilder.toString(), "P|");
    }

    //ContinueWith(No Return Value) - thenAccept, thenRunAsync
    @Test
    public void thenAcceptTest()
            throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "3");

        future.thenRunAsync(() -> System.out.println("just output"));
        future.thenAccept(rel -> System.out.println("result is " + rel));

        String result = future.get();
        System.out.println("main result is " + result);

        Assert.assertEquals(result, "3");
    }

    //ContinueWith(No Return Value) - thenAccept, thenRunAsync
    @Test
    public void thenComposeTest()
            throws ExecutionException, InterruptedException {
        CompletableFuture<CompletableFuture<String>> future = CompletableFuture.supplyAsync(() -> {
            CompletableFuture<String> future1 = new CompletableFuture<>();
            future1.complete("3");
            return future1;
        });

        CompletableFuture<String> future2 = future.thenCompose(a -> {
            CompletableFuture<String> future1 = new CompletableFuture<>();
            try {
                future1.complete(a.get() + " 31");
            } catch (Exception e) {
                e.printStackTrace();
                future1.complete(" 31");
            }
            return future1;
        });

        String result = future2.get();
        Assert.assertEquals(result, "3 31");
    }

    //WhenAll(taks1, task2)
    @Test
    public void combineTest1() throws ExecutionException, InterruptedException {

        CompletableFuture future1 = CompletableFuture.supplyAsync(() -> 2);

        CompletableFuture future3 = CompletableFuture.supplyAsync(() -> 3);

        CompletableFuture future4 = future1.thenCombine(future3, (r1, r3) -> {
            System.out.println((Integer) r1 + (Integer) r3);
            return null;
        });

        future4.get();
    }

    //WhenAll(taks1, task2, ...) - allOf
    //WhenAny(taks1, task2, ...) - anyOf
    @Test
    public void allOfTest1() throws ExecutionException, InterruptedException {
        Calendar startDate = Calendar.getInstance();

        CompletableFuture future1 = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return 2;
        });

        CompletableFuture future3 = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(30);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return 3;
        });

        CompletableFuture allOfFuture = CompletableFuture.allOf(future1, future3);
        CompletableFuture future5 = allOfFuture.thenApply(r -> {
            Calendar endDate = Calendar.getInstance();
            long duration = endDate.getTime().getTime() - startDate.getTime().getTime();
            System.out.println("all of " + duration);
            return null;
        });

        CompletableFuture anyOfFuture = CompletableFuture.anyOf(future1, future3);
        CompletableFuture future7 = anyOfFuture.thenRun(() -> {
            Calendar endDate = Calendar.getInstance();
            long duration = endDate.getTime().getTime() - startDate.getTime().getTime();
            System.out.println("any of " + duration);
        });

        future5.get();
        future7.get();
    }

    //WhenAll(taks1, task2)
    @Test
    public void combineExceptionTest1() throws ExecutionException, InterruptedException {

        CompletableFuture future1 = CompletableFuture.supplyAsync(() -> 2);

        CompletableFuture future3 = new CompletableFuture();
        future3.cancel(true);

        CompletableFuture future4 = future1.thenCombine(future3, (r1, r3) -> {
            System.out.println((Integer) r1 + (Integer) r3);
            return null;
        });

        try {
            future4.get();
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertEquals("java.util.concurrent.ExecutionException", ex.getClass().getName());
            System.out.println(ex.getClass().getName());
        }
    }

    //WhenAny(taks1, task2)
    @Test
    public void acceptEitherTest1() throws ExecutionException, InterruptedException {

        CompletableFuture future1 = CompletableFuture.supplyAsync(() -> 2);

        CompletableFuture future3 = new CompletableFuture();
        future3.cancel(true);

        CompletableFuture future4 = future1.acceptEither(future3, (r1) -> {
            System.out.println((Integer) r1);
        });

        future4.get();
    }

    //WhenAny(taks1, task2).ContinueWith
    @Test
    public void acceptToEitherTest1() throws ExecutionException, InterruptedException {

        CompletableFuture future1 = CompletableFuture.supplyAsync(() -> 2);

        CompletableFuture future3 = new CompletableFuture();
        future3.cancel(true);

        CompletableFuture future4 = future1.applyToEither(future3, (r1) -> {
            System.out.println((Integer) r1);
            return "8";
        });

        future4.get();
        Assert.assertEquals("8", future4.get());
    }

}