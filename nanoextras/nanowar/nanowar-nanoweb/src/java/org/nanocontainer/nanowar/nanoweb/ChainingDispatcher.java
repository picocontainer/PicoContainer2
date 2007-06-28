package org.nanocontainer.nanowar.nanoweb;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ChainingDispatcher implements Dispatcher {
    private final String extension;

    public ChainingDispatcher(String extension) {
        this.extension = extension;
    }

    public void dispatch(ServletContext servletContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String scriptPathWithoutExtension, String actionMethod, String result) throws IOException, ServletException {
        String[] views = getViews(scriptPathWithoutExtension, actionMethod, result);

        boolean didDispatch = false;
        for (String view : views) {
            URL viewURL = servletContext.getResource(view);
            if (viewURL != null) {
                RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher(view);
                if (httpServletRequest.getAttribute("javax.servlet.include.servlet_path") == null) {
                    requestDispatcher.forward(httpServletRequest, httpServletResponse);
                } else {
                    requestDispatcher.include(httpServletRequest, httpServletResponse);
                }
                didDispatch = true;
                break;
            }
        }
        if (!didDispatch) {
            throw new ServletException("Couldn't dispatch to any of " + Arrays.asList(views).toString());
        }
    }

    String[] getViews(String scriptPathWithoutExtension, String actionMethod, String result) {
        String[] views = new String[4];

        views[0] = getScriptPathUnderscoreActionNameUnderscoreResultView(scriptPathWithoutExtension, actionMethod, result);
        views[1] = getScriptPathUnderscoreResultView(scriptPathWithoutExtension, result);
        views[2] = getActionFolderPathResultView(scriptPathWithoutExtension, result);
        views[3] = getActionRootResultView(result);

        return views;
    }

    private String getScriptPathUnderscoreResultView(String scriptPathWithoutExtension, String result) {
        return scriptPathWithoutExtension + "_" + result + extension;
    }

    private String getScriptPathUnderscoreActionNameUnderscoreResultView(String scriptPathWithoutExtension, String actionMethod, String result) {
        return scriptPathWithoutExtension + "_" + actionMethod + "_" + result + extension;
    }

    private String getActionFolderPathResultView(String scriptPathWithoutExtension, String result) {
        String actionFolderPath = scriptPathWithoutExtension.substring(0, scriptPathWithoutExtension.lastIndexOf("/") + 1);
        return actionFolderPath + result + extension;
    }

    private String getActionRootResultView(String result) {
        return "/" + result + extension;
    }

}