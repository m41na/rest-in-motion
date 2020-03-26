package works.hop.handler.impl;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import works.hop.handler.HandlerException;
import works.hop.handler.HandlerFunction;

public class HandlerInterceptPool {

    private static HandlerInterceptPool instance;

    private ObjectPool<DefaultHandlerIntercept> pool;

    private HandlerInterceptPool(ObjectPool<DefaultHandlerIntercept> pool) {
        this.pool = pool;
    }

    public static HandlerInterceptPool instance() {
        if (instance == null) {
            instance = new HandlerInterceptPool(new GenericObjectPool<>(new HandlerInterceptFactory()));
        }
        return instance;
    }

    public DefaultHandlerIntercept borrowObject(HandlerFunction handler) {
        try {
            DefaultHandlerIntercept intercept = pool.borrowObject();
            intercept.handler(handler);
            intercept.next(null);
            return intercept;
        } catch (Exception e) {
            throw new HandlerException(500, e.getMessage());
        }
    }

    public void returnObject(DefaultHandlerIntercept chain) throws Exception {
        pool.returnObject(chain);
    }

    private static class HandlerInterceptFactory extends BasePooledObjectFactory<DefaultHandlerIntercept> {

        @Override
        public DefaultHandlerIntercept create() throws Exception {
            return new DefaultHandlerIntercept();
        }

        @Override
        public PooledObject<DefaultHandlerIntercept> wrap(DefaultHandlerIntercept handlerIntercept) {
            return new DefaultPooledObject<>(handlerIntercept);
        }

        @Override
        public void passivateObject(PooledObject<DefaultHandlerIntercept> pooledObject) {
            pooledObject.getObject().handler(null);
            pooledObject.getObject().next(null);
        }
    }
}
