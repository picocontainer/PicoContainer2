package org.nanocontainer.nanowar.nanoweb;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoWebVelocityServlet extends VelocityViewServlet {
    protected Template handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Context context) throws Exception {
        context.put("action", httpServletRequest.getAttribute("action"));
        return super.handleRequest(httpServletRequest, httpServletResponse, context);
    }
}