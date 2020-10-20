package org.aolyn.concurrent.test;

import org.junit.Test;

public class ThreadLocalTest {

    @Test
    public void testThreadLocal() {
        final ThreadLocal<String> local = new ThreadLocal<>();
        work(local);
    }

    @Test
    public void testInheritableThreadLocal() {
        final ThreadLocal<String> local = new InheritableThreadLocal<>();
        work(local);
    }

    private void work(final ThreadLocal<String> local) {
        local.set("a");
        System.out.println(Thread.currentThread() + "," + local.get());
        Thread t = new Thread(() -> {
            //Assert.assertEquals("a", local.get());
            System.out.println(Thread.currentThread() + "," + local.get());
            local.set("b");
            System.out.println(Thread.currentThread() + "," + local.get());
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread() + "," + local.get());
        //Assert.assertEquals("a", local.get());
    }
}
