package com.example.myapplication;

import java.sql.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Demo {
    public static void main(String[] args) {
        System.out.printf("kkkkkkkkk\n");
        System.out.printf("kkkkkkkkk\n");
        System.out.printf("kkkkkkkkk\n");
        System.out.printf("kkkkkkkkk\n");

        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.printf("task run\n");
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.printf("thread run\n");
            }
        });

        ThreadPoolExecutor executor = new ThreadPoolExecutor(10,10,60L, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));
        executor.execute(task);
        Future future = executor.submit(task);
//        System.out.printf("Future="+future.toString()+'\n');
        executor.execute(thread);

        Vector vector = new Vector<String> ();
        for ( int i=0;i<50;i++){
            vector.add(i);
        }
        System.out.printf("vector="+vector.toString());

        LinkedList linkedList = new LinkedList<String>();
        for (int i=0;i<50;i++){
            linkedList.add(i);
        }
        System.out.printf("\nlinkedList="+linkedList.toString());

        Stack stack =new Stack<String>();

    }
}
