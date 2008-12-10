package org.picocontainer.web.sample.jqueryemailui;

import javax.servlet.http.HttpSession;

public class SecuritySeed {

    public SecuritySeed(String sec, HttpSession session) {
        String securitySeed = (String) session.getAttribute("securitySeed");
        if (!sec.equals(securitySeed)) {
            throw new RuntimeException("request attempted without correct security seed");
        }
    }
}
