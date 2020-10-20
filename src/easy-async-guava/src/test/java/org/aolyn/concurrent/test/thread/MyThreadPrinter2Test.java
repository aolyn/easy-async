//package org.aolyn.concurrent.test.chris.thread;
//
//import org.junit.Test;
//
///**
// * Created by Chris Huang on 2018-03-22.
// */
//public class MyThreadPrinter2Test {
//    @Test
//    public void test() throws InterruptedException {
//        Object a = new Object();
//        Object b = new Object();
//        Object c = new Object();
//        MyThreadPrinter2 pa = new MyThreadPrinter2("A", c, a);
//        MyThreadPrinter2 pb = new MyThreadPrinter2("B", a, b);
//        MyThreadPrinter2 pc = new MyThreadPrinter2("C", b, c);
//
//        Thread thread1= new Thread(pa);
//        Thread thread2= new Thread(pb);
//        Thread thread3= new Thread(pc);
//
//        thread1.start();
//        thread2.start();
//        thread3.start();
//
//        thread1.join();
//        thread2.join();
//        thread3.join();
//    }
//
//    @Test
//    public void test3() throws InterruptedException {
//        Object a = new Object();
//        Object b = new Object();
//        Object c = new Object();
//        MyThreadPrinter2 pa = new MyThreadPrinter2("A", c, a);
//        MyThreadPrinter2 pb = new MyThreadPrinter2("B", a, b);
//        MyThreadPrinter2 pc = new MyThreadPrinter2("C", b, c);
//
//        new Thread(pa).start();
//        Thread.sleep(10);
//        new Thread(pb).start();
//        Thread.sleep(10);
//        new Thread(pc).start();
//
//        Thread.sleep(1000);
//    }
//}