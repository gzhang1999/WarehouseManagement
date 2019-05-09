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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.authorization.model.Role;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.service.RoleService;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    private static final String APPLICATION_ID = "Authorization";
    private static final String FORM_ID = "User";

    @RequestMapping(value="/auth/users")
    public ModelAndView usersDisplay(){


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);
        modelAndView.setViewName("auth/users");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/auth/users/list")
    public WebServiceResponseWrapper queryUsers(@RequestParam Map<String, String> parameters) {
        User currentLoginUser = userService.getCurrentLoginUser();
        List<User> userList = userService.findUsers(parameters);
        Map<String, String> customFields = new HashMap<>();
        customFields.put("isUserManager", String.valueOf(currentLoginUser.isUserManager()));
        customFields.put("isRoleManager", String.valueOf(currentLoginUser.isRoleManager()));
        return new WebServiceResponseWrapper<List<User>>(0, "", userList, customFields);

    }


    @ResponseBody
    @RequestMapping(value="/ws/auth/users/query/{id}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getUser(@PathVariable("id") int userID) {
        User currentLoginUser = userService.getCurrentLoginUser();

        if (currentLoginUser.isUserManager()) {
            User user = userService.findUserById(userID);
            if(user == null) {
                return WebServiceResponseWrapper.raiseError("UserException.CannotFindUser", "Can not find the user by ID: " + userID);
            }
            else {
                return new WebServiceResponseWrapper<User>(0, "", user);
            }
        }
        else {
            // The user doesn't have the access to the menu
            return WebServiceResponseWrapper.raiseError("UserException.NoRightForManageUser", "The user doesn't have right to manager other users");

        }

    }

    /***
     * Save the change for the user
     * The password is supposed to be encrypt at the server side
     */
    @ResponseBody
    @RequestMapping(value="/ws/auth/user/edit", method = RequestMethod.POST)
    public WebServiceResponseWrapper changeUser(@RequestParam("userID") int userID,
                                                @RequestParam("username") String username,
                                                @RequestParam("password") String password,
                                                @RequestParam("firstname") String firstname,
                                                @RequestParam("lastname") String lastname,
                                                @RequestParam("active") boolean active,
                                                @RequestParam("roleManager") boolean roleManager,
                                                @RequestParam("userManager") boolean userManager,
                                                @RequestParam("menuManager") boolean menuManager,
                                                @RequestParam("changePassword") boolean changePassword) {
        User currentLoginUser = userService.getCurrentLoginUser();

        if (currentLoginUser.isUserManager()) {
            User user = userService.findUserById(userID);
            if(user == null) {
                return WebServiceResponseWrapper.raiseError("UserException.CannotFindUser", "Can not find the user by ID: " + userID);
            }
            else {
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setActive(active);
                user.setRoleManager(roleManager);
                user.setUserManager(userManager);
                user.setMenuManager(menuManager);
                user.setChangePassword(changePassword);
                userService.save(user);
                return new WebServiceResponseWrapper<User>(0, "",
                        userService.findUserById(userID));
            }
        }
        else {
            // The user doesn't have the access to the menu
            return WebServiceResponseWrapper.raiseError("UserException.NoRightForManageUser", "The user doesn't have right to manager other users");

        }
    }

    /***
     * Save a new user
     * The password is supposed to be encrypt at the server side
     */
    @ResponseBody
    @RequestMapping(value="/ws/auth/user/new", method = RequestMethod.POST)
    public WebServiceResponseWrapper addNewUser(@RequestParam("username") String username,
                                                @RequestParam("password") String password,
                                                @RequestParam("firstname") String firstname,
                                                @RequestParam("lastname") String lastname,
                                                @RequestParam("active") boolean active,
                                                @RequestParam("roleManager") boolean roleManager,
                                                @RequestParam("userManager") boolean userManager,
                                                @RequestParam("menuManager") boolean menuManager,
                                                @RequestParam("changePassword") boolean changePassword) {
        User currentLoginUser = userService.getCurrentLoginUser();

        System.out.println("Save user: ");
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("firstname: " + firstname);
        System.out.println("lastname: " + lastname);
        System.out.println("active: " + active);
        System.out.println("roleManager: " + roleManager);
        System.out.println("userManager: " + userManager);
        System.out.println("menuManager: " + menuManager);
        System.out.println("changePassword: " + changePassword);
        if (currentLoginUser.isUserManager()) {
            User user = new User();
            // encrypt the password
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setActive(active);
            user.setRoleManager(roleManager);
            user.setUserManager(userManager);
            user.setMenuManager(menuManager);
            user.setChangePassword(changePassword);

            return new WebServiceResponseWrapper<User>(0, "",
                    userService.save(user));
        }
        else {
            // The user doesn't have the access to the menu
            return WebServiceResponseWrapper.raiseError("UserException.NoRightForManageUser", "The user doesn't have right to manager other users");

        }
    }

    @ResponseBody
    @RequestMapping(value="/ws/users/assign/roles/{id}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getRoleAssignment(@PathVariable("id") int userID) {
        // Check whether the user can access the menu;
        User currentLoginUser = userService.getCurrentLoginUser();

        // The user need to have the right for menu assignment
        if (currentLoginUser.isRoleManager()) {

            User user = userService.findUserById(userID);
            if(user == null) {
                return WebServiceResponseWrapper.raiseError("UserException.CannotFindUser", "Can not find the user by ID: " + userID);
            }
            else {
                // load role & menu item, ignore cache as we will need to get the newly
                // assigned menu of the role
                List<Role> roles = roleService.findAll(false);
                return new WebServiceResponseWrapper<List<Role>>(0, "", roles);
            }
        }
        else {

            return WebServiceResponseWrapper.raiseError("UserException.NoRightForAssignMenu", "The user doesn't have right to assign menu");
        }

    }


    @ResponseBody
    @RequestMapping(value="/ws/users/assign/roles/{id}", method = RequestMethod.POST)
    public WebServiceResponseWrapper changeRoleAssignment(@PathVariable("id") int userID,
                                                          @RequestParam("roleID") int roleID,
                                                          @RequestParam("assigned") boolean assigned) {


        User currentLoginUser = userService.getCurrentLoginUser();

        // The user need to have the right for menu assignment
        if (currentLoginUser.isRoleManager()) {

            User user = userService.findUserById(userID);
            if(user == null) {
                return WebServiceResponseWrapper.raiseError("UserException.CannotFindUser", "Can not find the user by ID: " + userID);
            }
            else {
                if (assigned) {
                    userService.grantRoleAccess(user,roleService.findByRoleId(roleID));
                }
                else  {
                    userService.removeRoleAccess(user,roleService.findByRoleId(roleID));
                }
                // load role & menu item, ignore cache as we will need to get the newly
                // assigned menu of the role

                user = userService.findUserById(userID);
                return new WebServiceResponseWrapper<User>(0, "", user);
            }
        }
        else {

            return WebServiceResponseWrapper.raiseError("UserException.NoRightForAssignMenu", "The user doesn't have right to assign menu");
        }

    }
}
