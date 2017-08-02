package test.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Test redisson Lock by multiple threads
 *
 * Normal output:
 *
 * Job-1| Try to get the Lock...
 * Job-2| Try to get the Lock...
 * Job-1| Get the Lock! Start to do something...
 * Job-2| Other thread get the Lock! T_T
 * Job-1| Finish the job! Free the Lock.
 *
 *
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

            try {
                System.out.println(this.threadName + "| Try to get the Lock...");
                boolean res = lock.tryLock(3, 10, TimeUnit.SECONDS);
                if(res) {
                    System.out.println(this.threadName + "| Get the Lock! Start to do something...");
                    Thread.sleep(5 * 1000);
                    System.out.println(this.threadName + "| Finish the job! Free the Lock.");
                    lock.unlock();
                }
                else {
                    System.out.println(this.threadName + "| Other thread get the Lock! T_T");
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
