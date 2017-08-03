package test.redis;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Test producer-consumer on blocking queue by multiple threads
 *
 * Normal output:
 *
 *
 *
 */
public class TestOfferAndPollBlockingQueue {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("master")
                .addSentinelAddress("redis://127.0.0.1:26379");

        RedissonClient redisson = Redisson.create(config);

        System.out.println(redisson);

        Producer p1 = new Producer(redisson, "job-queue", "Producer-1");
        Consumer c1 = new Consumer(redisson, "job-queue", "Consumer-1");
        Consumer c2 = new Consumer(redisson, "job-queue", "Consumer-2");

        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(c1);
        Thread t3 = new Thread(c2);

        t1.start();
        t2.start();
        t3.start();
    }

    public static class Producer implements Runnable {

        private RedissonClient redisson;
        private String queueName;
        private String threadName;
        private int counter;

        public Producer(RedissonClient redisson, String queueName, String threadName) {
            this.redisson = redisson;
            this.threadName = threadName;
            this.queueName = queueName;
            counter = 0;
        }

        @Override
        public void run() {
            RBlockingQueue<String> queue = redisson.getBlockingQueue(queueName);
            try {
                while(true) {
                    queue.offer(Integer.toString(counter++));
                    System.out.println(LocalDateTime.now().toString() + " | " + threadName + " | putted a msg into queue: " + (counter - 1));
                    Thread.sleep(3 * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Consumer implements Runnable {

        private RedissonClient redisson;
        private String queueName;
        private String threadName;

        public Consumer(RedissonClient redisson, String queueName, String threadName) {
            this.redisson = redisson;
            this.threadName = threadName;
            this.queueName = queueName;
        }

        @Override
        public void run() {
            RBlockingQueue<String> queue = redisson.getBlockingQueue(queueName);
            try {
                while(true) {
                    System.out.println(LocalDateTime.now().toString() + " | " + threadName + " | Try to get msg from queue...");
                    String msg = queue.poll(5, TimeUnit.SECONDS);
                    if (null != msg) {
                        System.out.println(LocalDateTime.now().toString() + " | " + threadName + " | Got a msg: " + msg + " and processing it.");
                        Thread.sleep(2 * 1000);
                        System.out.println(LocalDateTime.now().toString() + " | " + threadName + " | Processed a msg: " + msg);
                    } else {
                        System.out.println(LocalDateTime.now().toString() + " | " + threadName + " | Queue is still empty after waiting!");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
