//package org.aolyn.concurrent.test.chris;
//
//import com.google.common.util.concurrent.ListenableFuture;
//import org.aolyn.concurrent.TaskUtils;
//import org.junit.Test;
//
//import java.util.concurrent.ExecutionException;
//
///**
// * Created by Chris Huang on 2018-03-21.
// */
//public class ObjectWaitTest {
//    @Test
//    public void waitNotifyTest1() throws ExecutionException, InterruptedException {
//
//        Object lock1 = new Object();
//
//        ListenableFuture task1 = TaskUtils.run(() -> { //task1
//            synchronized (lock1) {
//                System.out.println("task 1 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lock1.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("task 1 end");
//            }
//        });
//
//
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        ListenableFuture task2 = TaskUtils.run(() -> {
//            synchronized (lock1) {
//                System.out.println("task 2 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                lock1.notify();
//                System.out.println("task 2 end");
//            }
//        });
//
//        TaskUtils.waitAll(task1, task2);
//    }
//
//    @Test
//    public void waitWithMsNotifyTest1() throws ExecutionException, InterruptedException {
//
//        Object lock1 = new Object();
//
//        ListenableFuture task1 = TaskUtils.run(() -> { //task1
//            synchronized (lock1) {
//                System.out.println("task 1 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lock1.wait(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("task 1 end");
//            }
//        });
//
//
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        ListenableFuture task2 = TaskUtils.run(() -> {
//            synchronized (lock1) {
//                System.out.println("task 2 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                lock1.notify();
//                System.out.println("task 2 end");
//            }
//        });
//
//        TaskUtils.waitAll(task1, task2);
//    }
//
//    @Test
//    public void waitNotifyTest3() throws ExecutionException, InterruptedException {
//        Object lock1 = new Object();
//
//        ListenableFuture task1 = TaskUtils.run(() -> { //task1
//            synchronized (lock1) {
//                System.out.println("task 1 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lock1.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lock1.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("task 1 end");
//            }
//        });
//
//
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        ListenableFuture task2 = TaskUtils.run(() -> {
//            synchronized (lock1) {
//                System.out.println("task 2 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                lock1.notify();
//                System.out.println("task 2 end");
//            }
//        });
//
//        TaskUtils.waitAll(task1, task2);
//    }
//
//    @Test
//    public void waitNotify2() throws ExecutionException, InterruptedException {
//
//        Object lockA = new Object();
//
//        ListenableFuture task1 = TaskUtils.run(() -> { //task1
//            synchronized (lockA) {
//                System.out.println("task 1 start");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lockA.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("task 1 end");
//            }
//        });
//
//
//        try {
//            Thread.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ListenableFuture task2 = TaskUtils.run(() -> {
//            synchronized (lockA) {
//                System.out.println("task 2 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lockA.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("task 2 end");
//            }
//        });
//
//        try {
//            Thread.sleep(800);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ListenableFuture task3 = TaskUtils.run(() -> {
//            synchronized (lockA) {
//                System.out.println("task 3 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                lockA.notify();
//                System.out.println("task 3 end");
//            }
//        });
//
//        TaskUtils.waitAll(task1, task2, task3);
//    }
//
//    @Test
//    public void waitNotifyAllTest1() throws ExecutionException, InterruptedException {
//
//        Object lockA = new Object();
//
//        ListenableFuture task1 = TaskUtils.run(() -> { //task1
//            synchronized (lockA) {
//                System.out.println("task 1 start");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lockA.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("task 1 end");
//            }
//        });
//
//
//        try {
//            Thread.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ListenableFuture task2 = TaskUtils.run(() -> {
//            synchronized (lockA) {
//                System.out.println("task 2 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    lockA.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("task 2 end");
//            }
//        });
//
//        try {
//            Thread.sleep(800);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ListenableFuture task3 = TaskUtils.run(() -> {
//            synchronized (lockA) {
//                System.out.println("task 3 start");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                lockA.notifyAll();
//                System.out.println("task 3 end");
//            }
//        });
//
//        TaskUtils.waitAll(task1, task2, task3);
//    }
//}
