package org.aolyn.concurrent.test;

import org.aolyn.concurrent.guava.TaskUtils;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest {

    private static final ThreadLocal<String> threadId = new ThreadLocal<String>();

    @Test
    public void test() throws ExecutionException, InterruptedException {
        System.out.println(ThreadId.get());
        threadId.set("hello");
        Future task = TaskUtils.run(() -> {
            Thread thread = Thread.currentThread();
            String name = thread.getName();
            int threadId2 = ThreadId.get();
            System.out.println(threadId2);
            String id2 = threadId.get();
            System.out.println(name);
        });

        task.get();
    }

    static class ThreadId {
        //一个递增的序列，使用AtomicInger原子变量保证线程安全
        private static final AtomicInteger nextId = new AtomicInteger(0);
        //线程本地变量，为每个线程关联一个唯一的序号
        private static final ThreadLocal<Integer> threadId =
                new ThreadLocal<Integer>() {
                    @Override
                    protected Integer initialValue() {
                        return nextId.getAndIncrement();//相当于nextId++,由于nextId++这种操作是个复合操作而非原子操作，会有线程安全问题(可能在初始化时就获取到相同的ID，所以使用原子变量
                    }
                };

        //返回当前线程的唯一的序列，如果第一次get，会先调用initialValue，后面看源码就了解了
        public static int get() {
            return threadId.get();
        }
    }
}


