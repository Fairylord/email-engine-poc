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
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class WorkerPoolTest {

    private WorkerPool pool;

    private AtomicInteger count = new AtomicInteger(0);

    @Before
    public void setUp() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(1);
        config.setMaxIdle(3);
        config.setMaxTotal(3);
        config.setBlockWhenExhausted(true);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

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
                        try {
                            worker = pool.borrowObject();
                            worker.work(count.getAndIncrement());
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        } finally {
                            if (worker != null) {
                                pool.returnObject(worker);
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

