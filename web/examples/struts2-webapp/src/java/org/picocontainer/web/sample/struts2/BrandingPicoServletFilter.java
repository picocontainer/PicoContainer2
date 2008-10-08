package org.picocontainer.web.sample.struts2;

import org.picocontainer.web.struts2.PicoObjectFactory;
import org.picocontainer.web.sample.service.Brand;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.adapters.AbstractAdapter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import java.lang.reflect.Type;
import java.io.IOException;

public class BrandingPicoServletFilter extends PicoObjectFactory.ServletFilter {

    protected void initAdditionalScopedComponents(MutablePicoContainer sessionContainer, MutablePicoContainer reqContainer) {
        reqContainer.addAdapter(new BrandFromDomainAdapter());
    }

    private static ThreadLocal<ServletRequest> request = new ThreadLocal<ServletRequest>();

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain filterChain) throws IOException, ServletException {
        request.set(req);
        super.doFilter(req, resp, filterChain);
    }

    private static class BrandFromDomainAdapter extends AbstractAdapter {

        private BrandFromDomainAdapter() {
            super(Brand.class, Brand.class);
        }

        public Object getComponentInstance(PicoContainer picoContainer, Type type) throws PicoCompositionException {
            return new Brand() {
                public String getName() {
                    return request.get().getRemoteHost().toUpperCase();
                }
            };
        }

        public void verify(PicoContainer picoContainer) throws PicoCompositionException {
        }

        public String getDescriptor() {
            return "StoreFromHttpRequest";
        }
    }

}
