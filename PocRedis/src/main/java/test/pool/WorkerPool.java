package test.pool;

/**
 * Created by HUANGYE2 on 10/24/2017.
 */

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class WorkerPool extends GenericObjectPool<Worker> {

    public WorkerPool(PooledObjectFactory<Worker> factory) {
        super(factory);
    }

    public WorkerPool(PooledObjectFactory<Worker> factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

}
