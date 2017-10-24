package test.pool;

/**
 * Created by HUANGYE2 on 10/24/2017.
 */


import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class WorkerFactory extends BasePooledObjectFactory<Worker>{

    private volatile int count = 0;

    @Override
    public Worker create() throws Exception {
        return new Worker(String.valueOf(++count));
    }

    @Override
    public PooledObject<Worker> wrap(Worker worker) {
        return new DefaultPooledObject<Worker>(worker);
    }

    @Override
    public boolean validateObject(PooledObject<Worker> p) {
        return p.getObject().isAlive();
    }

    @Override
    public void destroyObject(PooledObject<Worker> p) throws Exception {
        p.getObject().die();
    }
}
