package test.pool.test;

/**
 * Created by HUANGYE2 on 10/24/2017.
 */

import junit.framework.Assert;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;
import test.pool.Worker;
import test.pool.WorkerFactory;
import test.pool.RetriableWorkerPool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.fail;

public class RetriableWorkerPoolTest {

    private RetriableWorkerPool pool;

    private AtomicInteger count = new AtomicInteger(0);

    public static class WorkingThread implements Runnable {

        private AtomicInteger counter;
        private String key;
        private RetriableWorkerPool pool;

        public WorkingThread(AtomicInteger counter, String key, RetriableWorkerPool pool) {
            this.counter = counter;
            this.key = key;
            this.pool = pool;
        }

        @Override
        public void run() {
            {
                Worker worker = null;
                try {
                    int step = counter.getAndIncrement();
                    if (key == null) {
//                            key = ((step % 2 == 0) ? "Even" : "Odd");
                        key = ((step % 2 == 0) ? "Even" : "Even");
                    }
                    worker = pool.borrowObject(key, 1);
                    worker.work(step);
                } catch (Exception e) {
                    System.err.println("[" + Thread.currentThread().getName() + "] ");
                    e.printStackTrace(System.err);
                } finally {
                    if (worker != null) {
                        try {
                            pool.returnObject(key, worker);
                        } catch (Throwable t) {
                            System.err.println("[" + Thread.currentThread().getName() + "] ");
                            t.printStackTrace(System.err);
                        }
                    }
                }
            }

        }
    }

    @Before
    public void setUp() {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();

        // Max element limitation
        config.setMaxTotalPerKey(1);
        config.setMaxIdlePerKey(1);
        config.setMinIdlePerKey(0);

        // When pool exhausted, block the thread
        config.setMaxWaitMillis(40 * 1000L);    // Max wait time to block the thread
        config.setBlockWhenExhausted(true);     // Must be true to block the thread

        // Drop the idle elements after long time no one use
        config.setMinEvictableIdleTimeMillis(4 * 60 * 1000L);   // How long the idle element will be dropped
        config.setTimeBetweenEvictionRunsMillis(1 * 60 * 1000L);    // How long will a idle checking run once

        // Validate the element is still alive
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        // First in First out
        config.setFairness(true);

        this.pool = new RetriableWorkerPool(new WorkerFactory(), config);
    }

    @Test
    public void test() {
        try {
            int limit = 10;
            ExecutorService es = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(limit));
            for (int i = 0; i < limit; i++) {
                Runnable r = new WorkingThread(count, null, pool);
                es.submit(r);
            }
//            es.shutdown();

            ((Runnable) () -> {
                try {
                    Thread.sleep(5000);

                    Runnable r = new WorkingThread(new AtomicInteger(100), "Late", pool);
                    es.submit(r);
                    es.shutdown();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).run();




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

