/**
 * Copyright 2018
 *
 * @author gzhang
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.gzhang.scm.wms.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import se.gzhang.scm.wms.authorization.service.UserService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// A filter to control the access to a page based on the
// user's role
public class WebPageAccessControllerFilter implements Filter {

    private UserService userService;

    public void setupUserService(UserService userService) {
        this.userService = userService;
    }

    private final static String[] skipAccessValidPages = {"/login","/home","/index","/ws/*","/fileUpload/template/*","/error/*","/js/*","/fonts/*","/css/*","/img/*", "/flow*"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // Check whether the current user has access to current url

        String url = ((HttpServletRequest) servletRequest).getRequestURI();
        if (skipAccessValid(url)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else if (userService == null) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.sendRedirect("/error/accessfail?url=" + url + "&user=null");
        }
        else {
            if (!userService.isMenuAccessible(url)) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendRedirect("/error/accessfail?url=" + url);
            }
            else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    private boolean skipAccessValid(String url) {
        for(String skipPageURL : skipAccessValidPages) {
            // System.out.println("Testing URL: " + url);

            if (skipPageURL.equals(url)) {
                return true;
            }
            else if (skipPageURL.endsWith("*")) {
                String skippedUrl = skipPageURL.substring(0, skipPageURL.lastIndexOf("*"));
                // System.out.println(">> skippedUrl: " + skippedUrl + "\n>> url.startsWith(skippedUrl) " + url.startsWith(skippedUrl));
                if (url.startsWith(skippedUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }
    public void destroy() {
        // Do nothing
    }
}
