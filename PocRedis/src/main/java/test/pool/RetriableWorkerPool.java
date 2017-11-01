package test.pool;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.util.NoSuchElementException;

/**
 * Test for very simple retry
 */
public class RetriableWorkerPool extends WorkerPool {
    public RetriableWorkerPool(KeyedPooledObjectFactory<String, Worker> factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }

    /**
     *
     * @param key
     * @param maxAttempts
     * @return
     * @throws Exception
     */
    public Worker borrowObject(String key, int maxAttempts) throws Exception {
        Worker resource = null;
        int attempts = 0;

        for (; attempts < maxAttempts; ++attempts) {
            try {
                resource = super.borrowObject(key);
                if (resource != null) {
                    System.out.println("[" + Thread.currentThread().getName() + "] " + "Attempted times: " + (attempts + 1) + " succeed.");
                    break;
                }
            } catch (Throwable t) {
                System.err.println("[" + Thread.currentThread().getName() + "] " + "Attempted times: " + (attempts + 1) + " but failed.");
                t.printStackTrace(System.err);
                continue;
            }
        }

        if(resource != null) {
            return resource;
        }
        else {
            throw new NoSuchElementException("Failed to get resource after max attempts.");
        }
    }
}
