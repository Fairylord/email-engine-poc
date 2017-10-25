package test.pool;

/**
 * Created by HUANGYE2 on 10/24/2017.
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class WorkerPoolTest {

    private WorkerPool pool;

    private AtomicInteger count = new AtomicInteger(0);

    @Before
    public void setUp() {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();

        // Max element limitation
        config.setMaxTotalPerKey(1);
        config.setMaxIdlePerKey(1);
        config.setMinIdlePerKey(0);

        // When pool exhausted, block the thread
        config.setMaxWaitMillis(2 * 60 * 1000L);
        config.setBlockWhenExhausted(true);

        // Drop the idle elements after long time no one use
        config.setMinEvictableIdleTimeMillis(4 * 60 * 1000L);
        config.setTimeBetweenEvictionRunsMillis(1 * 60 * 1000L);

        // Validate the element is still alive when take & return
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        // First in First out
        config.setFairness(true);

        this.pool = new WorkerPool(new WorkerFactory(), config);
    }

    @Test
    public void test() {
        try {
            int limit = 10;
            ExecutorService es = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(limit));
            for (int i = 0; i < limit; i++) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Worker worker = null;
                        String key = null;
                        try {
                            int step = count.getAndIncrement();
                            key = ((step % 2 == 0) ? "Even" : "Odd");

                            worker = pool.borrowObject(key);
                            worker.work(step);
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        } finally {
                            if (worker != null) {
                                pool.returnObject(key, worker);
                            }
                        }
                    }
                };
                es.submit(r);
            }
            es.shutdown();
            try {
                es.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {
            }
            System.out.println("Pool Stats:\n Created:[" + pool.getCreatedCount() + "], Borrowed:[" + pool.getBorrowedCount() + "]");
            Assert.assertEquals(limit, count.get());
            Assert.assertEquals(count.get(), pool.getBorrowedCount());
//            Assert.assertEquals(3, pool.getCreatedCount());
        } catch (Exception ex) {
            fail("Exception:" + ex);
        }
    }
}

