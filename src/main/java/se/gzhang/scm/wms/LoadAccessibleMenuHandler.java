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

package se.gzhang.scm.wms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.menu.service.MenuService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class LoadAccessibleMenuHandler implements AuthenticationSuccessHandler {

    // We will use the menu service and user service
    // to load the accessible menu for the login user
    // and save it in the session for later use.
    @Autowired
    MenuService menuService;

    @Autowired
    UserService userService;


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException  {

        int userID = userService.findUserByUsername(authentication.getName()).getId();
        request.getSession().setAttribute("parentMenuItemList",menuService.getAssignedMenuItemList(userID));

        // Always redirect to home page after successfully log in
        response.sendRedirect("/home");
    }

}
