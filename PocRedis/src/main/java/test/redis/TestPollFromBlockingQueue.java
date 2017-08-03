package test.redis;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * Test get msg from blocking queue.
 */
public class TestPollFromBlockingQueue {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("master")
                .addSentinelAddress("redis://127.0.0.1:26379");

        RedissonClient redisson = Redisson.create(config);

        System.out.println(redisson);

        RBlockingQueue<String> queue = redisson.getBlockingQueue("JobQueue");

        try {
            System.out.println("Try to get msg from queue...");
            String msg = queue.poll(5, TimeUnit.SECONDS);
            if(null != msg) {
                System.out.println("Got a msg: " + msg);
            }
            else {
                System.out.println("Queue is still empty after waiting!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            redisson.shutdown();
        }

    }
}
