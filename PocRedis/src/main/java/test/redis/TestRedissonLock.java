package test.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.client.RedisException;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Test redisson Lock by multiple threads
 * <p>
 * Normal output:
 * <p>
 * Job-1| Try to get the Lock...
 * Job-2| Try to get the Lock...
 * Job-1| Get the Lock! Start to do something...
 * Job-2| Other thread get the Lock! T_T
 * Job-1| Finish the job! Free the Lock.
 */
public class TestRedissonLock {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("master")
                .addSentinelAddress("redis://127.0.0.1:26379");

        RedissonClient redisson = Redisson.create(config);

        System.out.println(redisson);

        ScheduleJob job1 = new ScheduleJob(redisson, "Job-1");
        ScheduleJob job2 = new ScheduleJob(redisson, "Job-2");

        Thread t1 = new Thread(job1);
        Thread t2 = new Thread(job2);

        t1.start();
        t2.start();

    }

    public static class ScheduleJob implements Runnable {

        private RedissonClient redisson;
        private String threadName;

        public ScheduleJob(RedissonClient redisson, String threadName) {
            this.redisson = redisson;
            this.threadName = threadName;
        }

        @Override
        public void run() {
            RLock lock = redisson.getLock("Job:1:Lock");

            while (true) {
                try {
                    System.out.println(LocalDateTime.now().toString() + " | " + this.threadName + "| Try to get the Lock...");
                    boolean res = lock.tryLock(3, 10, TimeUnit.SECONDS);
                    if (res) {
                        System.out.println(LocalDateTime.now().toString() + " | " + this.threadName + "| Get the Lock! Start to do something...");
                        Thread.sleep(5 * 1000);
                        System.out.println(LocalDateTime.now().toString() + " | " + this.threadName + "| Finish the job! Free the Lock.");
                        lock.unlock();
                        Thread.sleep(5 * 1000);
                    } else {
                        System.out.println(LocalDateTime.now().toString() + " | " + this.threadName + "| Other thread get the Lock! T_T");
                        Thread.sleep(7 * 1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RedisException e) {    // For case Redis master dies.
                    System.out.println(LocalDateTime.now().toString() + " | " + threadName + "| Encounter RedisException !");
                    e.printStackTrace();
                }
            }
        }
    }
}
