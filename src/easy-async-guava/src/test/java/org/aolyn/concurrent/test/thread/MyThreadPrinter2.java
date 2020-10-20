//package org.aolyn.concurrent.test.chris.thread;
//
//import org.junit.Test;
//
///**
// * Created by Chris Huang on 2018-03-22.
// */
//public class MyThreadPrinter2 implements Runnable {
//
//    private String name;
//    private Object prev;
//    private Object self;
//
//    public MyThreadPrinter2(String name, Object prev, Object self) {
//        this.name = name;
//        this.prev = prev;
//        this.self = self;
//    }
//
//    @Override
//    public void run() {
//        int count = 10;
//        while (count > 0) {
//            synchronized (prev) {
//                synchronized (self) {
//                    System.out.print(name);
//                    count--;
//
//                    self.notify();
//                }
//                try {
//                    prev.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
//
//}