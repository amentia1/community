package com.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author flunggg
 * @date 2020/8/7 14:43
 * @Email: chaste86@163.com
 */
public class BlockingQueueTest {

    public static void main(String[] args) {
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(blockingQueue), "生产者").start();
        new Thread(new Consumer(blockingQueue), "消费者1").start();
        new Thread(new Consumer(blockingQueue), "消费者2").start();
        new Thread(new Consumer(blockingQueue), "消费者3").start();
    }
}

class Producer implements Runnable {

    private BlockingQueue<Integer> blockingQueue;

    public Producer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                blockingQueue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" + blockingQueue.size());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

class Consumer implements Runnable {

    private BlockingQueue<Integer> blockingQueue;

    public Consumer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            while(true) {
                // 0~1000
                Thread.sleep(new Random().nextInt(1000));
                blockingQueue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + blockingQueue.size());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}