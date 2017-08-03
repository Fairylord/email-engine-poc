package test.redis;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Test put msg into blocking queue.
 */
public class TestOfferToBlockingQueue {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("master")
                .addSentinelAddress("redis://127.0.0.1:26379");

        RedissonClient redisson = Redisson.create(config);

        System.out.println(redisson);

        RBlockingQueue<String> queue = redisson.getBlockingQueue("JobQueue");
        queue.offer("Email-ID-5");

        System.out.println("Putted msg to redis queue!");
        System.out.println("Queue size: " + queue.size());
        for(String msg : queue) {
            System.out.println(msg);
        }

        redisson.shutdown();
    }
}
