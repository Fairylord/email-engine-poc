package test.pool;

/**
 * Created by HUANGYE2 on 10/24/2017.
 */

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class WorkerFactory implements KeyedPooledObjectFactory<String, Worker> {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public PooledObject<Worker> makeObject(String key) throws Exception {
        Thread.sleep(50);
        int i = count.incrementAndGet();
        return new DefaultPooledObject<Worker>(new Worker(key + "--" + i, i));
    }

    @Override
    public void destroyObject(String key, PooledObject<Worker> pooledObject) throws Exception {
        pooledObject.getObject().die();
    }

    @Override
    public boolean validateObject(String s, PooledObject<Worker> pooledObject) {
        return pooledObject.getObject().isAlive();
    }

    @Override
    public void activateObject(String s, PooledObject<Worker> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(String s, PooledObject<Worker> pooledObject) throws Exception {

    }


    ///////////////////////////////////////////////////////////

//    @Override
//    public Worker create() throws Exception {
//        return new Worker(String.valueOf(++count));
//    }
//
//    @Override
//    public PooledObject<Worker> wrap(Worker worker) {
//        return new DefaultPooledObject<Worker>(worker);
//    }
//
//    @Override
//    public boolean validateObject(PooledObject<Worker> p) {
//        return p.getObject().isAlive();
//    }
//
//    @Override
//    public void destroyObject(PooledObject<Worker> p) throws Exception {
//        p.getObject().die();
//    }
}
