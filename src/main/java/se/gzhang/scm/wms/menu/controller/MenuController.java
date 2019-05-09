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

package se.gzhang.scm.wms.menu.controller;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.authorization.model.Role;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.service.RoleService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.menu.model.MenuItem;
import se.gzhang.scm.wms.menu.service.MenuService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MenuController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    private static final String APPLICATION_ID = "Menu";
    private static final String FORM_ID = "Menu";

    @RequestMapping(value="/menu/menu", method = RequestMethod.GET)
    public ModelAndView displayMenu(HttpSession session){
        List<MenuItem> assignedMenuList = new ArrayList<MenuItem>();

        User currentLoginUser = userService.getCurrentLoginUser();

        List<MenuItem> parentMenuItemList = menuService.getStructuredMenuItemList();
        for(MenuItem parentMenuItem : parentMenuItemList)
        {
            assignedMenuList.add(parentMenuItem);
            for(MenuItem childMenuItem : parentMenuItem.getChildMenuList())
            {
                childMenuItem.setParentMenuName(parentMenuItem.getName());
                assignedMenuList.add(childMenuItem);
            }
        }
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("assignedMenuList",assignedMenuList);
        modelAndView.addObject("currentLoginUser",currentLoginUser);
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);
        modelAndView.setViewName("menu/menu");

        return modelAndView;

    }

    @ResponseBody
    @RequestMapping(value="/ws/menu/list")
    public WebServiceResponseWrapper queryMenu(@RequestParam Map<String, String> parameters) {
        User currentLoginUser = userService.getCurrentLoginUser();
        List<MenuItem> menuList = menuService.findMenu(parameters);
        for(MenuItem menuItem : menuList) {
            // Setup the parent menu name
            System.out.println("Menu Item: " + menuItem.getName());
            if (menuService.getParentMenu(menuItem) != null) {
                menuItem.setParentMenuName(menuService.getParentMenu(menuItem).getName());
            }
        }
        Map<String, String> customFields = new HashMap<>();
        customFields.put("isRoleManager", String.valueOf(currentLoginUser.isRoleManager()));
        customFields.put("isMenuManager", String.valueOf(currentLoginUser.isMenuManager()));
        return new WebServiceResponseWrapper<List<MenuItem>>(0, "", menuList, customFields);
    }

    @ResponseBody
    @RequestMapping(value="/ws/menu/query/{id}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getMenu(@PathVariable("id") int menuID) {
        // Check whether the user can access the menu;
        User currentLoginUser = userService.getCurrentLoginUser();
        if (currentLoginUser.isRoleManager() && currentLoginUser.isMenuManager()) {
            MenuItem menuItem = menuService.findMenuItemById(menuID);
            if(menuItem == null) {
                return WebServiceResponseWrapper.raiseError("MenuException.CannotFindMenu", "Can not find the menu by ID: " + menuID);
            }
            else {
                return new WebServiceResponseWrapper<MenuItem>(0, "", menuItem);
            }
        }
        else {
            // The user doesn't have the access to the menu
            return WebServiceResponseWrapper.raiseError("MenuException.UserNoRightForAssignMenu", "The user doesn't have right to assign menu");

        }
    }

    @ResponseBody
    @RequestMapping(value="/ws/menu/assign/{id}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getMenuAssignment(@PathVariable("id") int menuID) {
        // Check whether the user can access the menu;
        User currentLoginUser = userService.getCurrentLoginUser();

        // The user need to have the right for menu assignment
        if (currentLoginUser.isRoleManager() && currentLoginUser.isMenuManager()) {

            MenuItem menuItem = menuService.findMenuItemById(menuID);
            if(menuItem == null) {
                return WebServiceResponseWrapper.raiseError("MenuException.CannotFindMenu", "Can not find the menu by ID: " + menuID);
            }
            else {
                // load role & menu item, ignore cache as we will need to get the newly
                // assigned menu of the role
                List<Role> roles = roleService.findAll(false);
                return new WebServiceResponseWrapper<List<Role>>(0, "", roles);
            }
        }
        else {

            return WebServiceResponseWrapper.raiseError("MenuException.UserNoRightForAssignMenu", "The user doesn't have right to assign menu");
        }

    }


    @ResponseBody
    @RequestMapping(value="/ws/menu/assign/{id}", method = RequestMethod.POST)
    public WebServiceResponseWrapper changeMenuAssignment(@PathVariable("id") int menuID,
                                                          @RequestParam("roleID") int roleID,
                                                          @RequestParam("assigned") boolean assigned) {
        String message= "Change Role: " + roleID + ", menu: " + menuID + " to: " + assigned;
        MenuItem menuItem = menuService.findMenuItemById(menuID);
        System.out.println(message);
        if (assigned) {
            roleService.grantMenuAccess(roleID,menuItem);
        }
        else  {
            roleService.removeMenuAccess(roleID,menuItem);
        }


        return WebServiceResponseWrapper.raiseError("0", message);

    }

    @ResponseBody
    @RequestMapping(value="/ws/menu/edit", method = RequestMethod.POST)
    public WebServiceResponseWrapper changeMenu(@RequestParam("menuID") int menuID,
                                                @RequestParam("menuGroup") String menuGroup,
                                                @RequestParam("menuName") String menuName,
                                                @RequestParam("menuIcon") String menuIcon,
                                                @RequestParam("menuMLS") String menuMLS,
                                                @RequestParam("menuSequence") int menuSequence,
                                                @RequestParam("menuURL") String menuURL) {
        // Check whether the user can access the menu;
        User currentLoginUser = userService.getCurrentLoginUser();
        if (currentLoginUser.isRoleManager() && currentLoginUser.isMenuManager()) {
            MenuItem menuItem = menuService.findMenuItemById(menuID);
            if(menuItem == null) {
                return WebServiceResponseWrapper.raiseError("MenuException.CannotFindMenu", "Can not find the menu by ID: " + menuID);
            }
            else {
                menuItem.setName(menuName);
                menuItem.setIconClass(menuIcon);
                menuItem.setMultiLanguageSupportID(menuMLS);
                menuItem.setSequence(menuSequence);
                menuItem.setUrl(menuURL);
                menuService.save(menuItem);
                return new WebServiceResponseWrapper<MenuItem>(0, "",
                        menuService.findMenuItemById(menuID));
            }
        }
        else {
            // The user doesn't have the access to the menu
            return WebServiceResponseWrapper.raiseError("MenuException.UserNoRightForAssignMenu", "The user doesn't have right to assign menu");

        }
    }


}