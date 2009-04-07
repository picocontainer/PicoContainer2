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
        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        try {
            final javax.cache.Cache jCache = cacheFactory.createCache(Collections.emptyMap());
            return new Cache() {
                public Object get(Object key) {
                    return jCache.get(key);
                }

                public void put(Object key, Object toCache) {
                    jCache.put(key, toCache);
                }
            };
        } catch (NullPointerException e) {
            final Map fallBackImpl = new HashMap();
            return new Cache() {
                public Object get(Object key) {
                    return fallBackImpl.get(key);
                }

                public void put(Object key, Object toCache) {
                    fallBackImpl.put(key, toCache);
                }
            };
        }
    }
}
