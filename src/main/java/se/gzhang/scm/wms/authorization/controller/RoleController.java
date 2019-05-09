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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.authorization.model.Role;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.service.RoleService;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.menu.model.MenuItem;
import se.gzhang.scm.wms.menu.service.MenuService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RoleController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    private static final String APPLICATION_ID = "Authorization";
    private static final String FORM_ID = "Role";

    @RequestMapping(value="/auth/roles")
    public ModelAndView rolesDisplay(){


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);
        modelAndView.setViewName("auth/roles");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/auth/roles/list")
    public WebServiceResponseWrapper queryRoles(@RequestParam Map<String, String> parameters) {
        User currentLoginUser = userService.getCurrentLoginUser();
        List<Role> roleList = roleService.findRoles(parameters);
        Map<String, String> customFields = new HashMap<>();
        customFields.put("isUserManager", String.valueOf(currentLoginUser.isUserManager()));
        customFields.put("isRoleManager", String.valueOf(currentLoginUser.isRoleManager()));
        customFields.put("isMenuManager", String.valueOf(currentLoginUser.isMenuManager()));
        return new WebServiceResponseWrapper<List<Role>>(0, "", roleList, customFields);

    }


    @ResponseBody
    @RequestMapping(value="/ws/auth/roles/query/{id}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getRole(@PathVariable("id") int roleID) {
        User currentLoginUser = userService.getCurrentLoginUser();

        if (currentLoginUser.isRoleManager()) {
            Role role = roleService.findByRoleId(roleID);
            if(role == null) {
                return WebServiceResponseWrapper.raiseError("RoleException.CannotFindRole", "Can not find the role by ID: " + roleID);
            }
            else {
                return new WebServiceResponseWrapper<Role>(0, "", role);
            }
        }
        else {
            // The user doesn't have the access to the role
            return WebServiceResponseWrapper.raiseError("RoleException.NoRightForManageRole", "The user doesn't have right to manager roles");

        }

    }

    @ResponseBody
    @RequestMapping(value="/ws/auth/role/edit", method = RequestMethod.POST)
    public WebServiceResponseWrapper changeRole(@RequestParam("roleID") int roleID,
                                                @RequestParam("roleName") String roleName,
                                                @RequestParam("roleDescription") String roleDescription) {
        User currentLoginUser = userService.getCurrentLoginUser();

        if (currentLoginUser.isRoleManager()) {
            Role role = roleService.findByRoleId(roleID);
            if(role == null) {
                return WebServiceResponseWrapper.raiseError("RoleException.CannotFindRole", "Can not find the role by ID: " + roleID);
            }
            else {
                role.setName(roleName);
                role.setDescription(roleDescription);
                roleService.save(role);
                return new WebServiceResponseWrapper<Role>(0, "",
                        roleService.findByRoleId(roleID));
            }
        }
        else {
            // The user doesn't have the access to the role
            return WebServiceResponseWrapper.raiseError("RoleException.NoRightForManageRole", "The user doesn't have right to manager roles");
        }
    }

    @ResponseBody
    @RequestMapping(value="/ws/auth/role/new", method = RequestMethod.POST)
    public WebServiceResponseWrapper addNewRole( @RequestParam("roleName") String roleName,
                                                 @RequestParam("roleDescription") String roleDescription) {
        User currentLoginUser = userService.getCurrentLoginUser();


        if (currentLoginUser.isRoleManager()) {
            Role role = new Role();
            role.setName(roleName);
            role.setDescription(roleDescription);
            return new WebServiceResponseWrapper<Role>(0, "",
                    roleService.save(role));

        }
        else {
            // The user doesn't have the access to the role
            return WebServiceResponseWrapper.raiseError("RoleException.NoRightForManageRole", "The user doesn't have right to manager roles");
        }
    }

    @ResponseBody
    @RequestMapping(value="/ws/roles/assign/users/{id}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getRoleAssignment(@PathVariable("id") int roleID) {
        // Check whether the user can manager the role;
        User currentLoginUser = userService.getCurrentLoginUser();

        // The user need to have the right for menu assignment
        if (currentLoginUser.isRoleManager() && currentLoginUser.isUserManager()) {

            Role role = roleService.findByRoleId(roleID);
            if(role == null) {
                return WebServiceResponseWrapper.raiseError("RoleException.CannotFindRole", "Can not find the role by ID: " + roleID);
            }
            else {
                List<User> users = userService.findAll();
                // Check which users has access to the current role
                List<String> assignedUsers = new ArrayList<>();
                for(User user : users) {
                    if (userService.isRoleAccessible(user, role)) {
                        assignedUsers.add(String.valueOf(user.getId()));
                    }
                }
                Map<String, String> customFields = new HashMap<>();
                customFields.put("assignedUsers", String.join(",", assignedUsers));
                return new WebServiceResponseWrapper<List<User>>(0, "", users, customFields);
            }
        }
        else {

            return WebServiceResponseWrapper.raiseError("RoleException.NoRightForManageRole", "The user doesn't have right to manager role");
        }

    }

    @ResponseBody
    @RequestMapping(value="/ws/roles/assign/menus/{id}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getMenuAssignment(@PathVariable("id") int roleID) {
        // Check whether the user can manager the menus;
        User currentLoginUser = userService.getCurrentLoginUser();

        // The user need to have the right for menu assignment
        if (currentLoginUser.isRoleManager() && currentLoginUser.isMenuManager()) {

            Role role = roleService.findByRoleId(roleID);
            if(role == null) {
                return WebServiceResponseWrapper.raiseError("RoleException.CannotFindRole", "Can not find the role by ID: " + roleID);
            }
            else {
                List<MenuItem> menuItems = menuService.findAll(true);
                // Check which users has access to the current role
                List<String> assignedMenus = new ArrayList<>();
                for(MenuItem menuItem : role.getMenuItems()) {
                    assignedMenus.add(String.valueOf(menuItem.getId()));
                }
                Map<String, String> customFields = new HashMap<>();
                customFields.put("assignedMenus", String.join(",", assignedMenus));
                return new WebServiceResponseWrapper<List<MenuItem>>(0, "", menuItems, customFields);
            }
        }
        else {

            return WebServiceResponseWrapper.raiseError("RoleException.NoRightForManageRole", "The user doesn't have right to manager role");
        }

    }
}
