package org.picocontainer.web.sample.ajaxemail;

import javax.cache.CacheManager;
import javax.cache.CacheFactory;
import javax.cache.CacheException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.picocontainer.web.remoting.Cache;
import org.picocontainer.injectors.ProviderAdapter;

public class CacheProvider extends ProviderAdapter {
    
    public Cache provide() throws CacheException {

        Map props = new HashMap();
        // com.google.appengine.api.memcache.stdimpl.GCacheFactory.EXPIRATION_DELTA (yeesh) == 0
        // 120 == 1 min
        props.put(0, 120);

        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        try {
            final javax.cache.Cache jCache = cacheFactory.createCache(props);
            return new Cache() {
                public Object get(Object key) {
                    return jCache.get(key);
                }

                public void put(Object key, Object toCache) {
                    jCache.put(key, toCache);
                }
            };
        } catch (NullPointerException e) {
            //return new CrudeCache();
            return new NotACache();
        }
    }

    private static class CrudeCache implements Cache {
        final Map fallBackImpl = new HashMap();

        public Object get(Object key) {
            return fallBackImpl.get(key);
        }

        public void put(Object key, Object toCache) {
            fallBackImpl.put(key, toCache);
        }
    }

    private static class NotACache implements Cache {

        public Object get(Object key) {
            return null;
        }

        public void put(Object key, Object toCache) {
        }
    }
}
