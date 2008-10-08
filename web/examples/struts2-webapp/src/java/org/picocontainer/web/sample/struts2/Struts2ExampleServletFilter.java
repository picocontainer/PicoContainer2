package org.picocontainer.web.sample.struts2;

import org.picocontainer.web.struts2.PicoObjectFactory;
import org.picocontainer.web.sample.service.Brand;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVisitor;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import java.lang.reflect.Type;
import java.io.IOException;

public class Struts2ExampleServletFilter extends PicoObjectFactory.ServletFilter {

    protected void initAdditionalScopedComponents(MutablePicoContainer sessionContainer, MutablePicoContainer reqContainer) {
        reqContainer.addAdapter(new BrandFromDomainAdapter());
    }

    private static ThreadLocal<ServletRequest> request = new ThreadLocal<ServletRequest>();

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain filterChain) throws IOException, ServletException {
        request.set(req);
        super.doFilter(req, resp, filterChain);
    }

    private static class BrandFromDomainAdapter implements ComponentAdapter {
        public Object getComponentKey() {
            return Brand.class;
        }

        public Class getComponentImplementation() {
            return Brand.class;
        }

        public Object getComponentInstance(PicoContainer picoContainer) throws PicoCompositionException {
            return new Brand() {
                public String getName() {
                    return request.get().getRemoteHost().toUpperCase();
                }
            };
        }

        public Object getComponentInstance(PicoContainer picoContainer, Type type) throws PicoCompositionException {
            return getComponentInstance(picoContainer);
        }

        public void verify(PicoContainer picoContainer) throws PicoCompositionException {
        }

        public void accept(PicoVisitor picoVisitor) {
        }

        public ComponentAdapter getDelegate() {
            return null;
        }

        public ComponentAdapter findAdapterOfType(Class aClass) {
            return null;
        }

        public String getDescriptor() {
            return "StoreFromHttpRequest";
        }
    }

}
