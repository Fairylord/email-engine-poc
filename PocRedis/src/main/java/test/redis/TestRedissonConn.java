package test.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;

/**
 * Created by HUANGYE2 on 8/2/2017.
 */
public class TestRedissonConn {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("master")
                .addSentinelAddress("redis://127.0.0.1:26379");

        RedissonClient redisson = Redisson.create(config);

        System.out.println(redisson);

        RBucket<Integer> b = redisson.getBucket("a");
        System.out.println(b.get());
        b.set(999);
    }
}
