package test.pool;

/**
 * Created by HUANGYE2 on 10/24/2017.
 */

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

public class WorkerPool extends GenericKeyedObjectPool<String, Worker> {

    public WorkerPool(KeyedPooledObjectFactory<String, Worker> factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }

}
