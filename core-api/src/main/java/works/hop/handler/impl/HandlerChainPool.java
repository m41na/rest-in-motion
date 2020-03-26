package works.hop.handler.impl;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import works.hop.handler.HandlerException;

public class HandlerChainPool {

    private static HandlerChainPool instance;

    private ObjectPool<DefaultHandlerChain> pool;

    private HandlerChainPool(ObjectPool<DefaultHandlerChain> pool) {
        this.pool = pool;
    }

    public static HandlerChainPool instance() {
        if (instance == null) {
            instance = new HandlerChainPool(new GenericObjectPool<>(new HandlerChainFactory()));
        }
        return instance;
    }

    public DefaultHandlerChain borrowObject() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new HandlerException(500, e.getMessage());
        }
    }

    public void returnObject(DefaultHandlerChain chain) throws Exception {
        pool.returnObject(chain);
    }

    private static class HandlerChainFactory extends BasePooledObjectFactory<DefaultHandlerChain> {

        @Override
        public DefaultHandlerChain create() throws Exception {
            return new DefaultHandlerChain();
        }

        @Override
        public PooledObject<DefaultHandlerChain> wrap(DefaultHandlerChain handlerChain) {
            return new DefaultPooledObject<>(handlerChain);
        }

        @Override
        public void passivateObject(PooledObject<DefaultHandlerChain> pooledObject) {
            pooledObject.getObject().reset();
        }
    }
}
