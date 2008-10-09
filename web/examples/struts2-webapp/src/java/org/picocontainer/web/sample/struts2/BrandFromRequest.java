package org.picocontainer.web.sample.struts2;

import org.picocontainer.web.sample.service.Brand;

import javax.servlet.http.HttpServletRequest;

public class BrandFromRequest implements Brand {

    private final HttpServletRequest req;

    public BrandFromRequest(HttpServletRequest req) {
        this.req = req;
    }

    public String getName() {
        return req.getRemoteHost().toUpperCase();
    }
}
