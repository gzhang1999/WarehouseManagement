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

package se.gzhang.scm.wms.authorization.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import se.gzhang.scm.wms.authorization.model.Role;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.repository.RoleRepository;
import se.gzhang.scm.wms.authorization.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.menu.model.MenuItem;
import se.gzhang.scm.wms.menu.service.MenuService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

@Service("userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private MenuService menuService;
    @Autowired
    private WarehouseService warehouseService;

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public  User findUserById(int id){return userRepository.findById(id).orElse(null);}

    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleService.findByName("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    public User getCurrentLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return findUserByUsername(auth.getName());
    }


    public List<User> findUsers(Map<String, String> criteriaList) {
        System.out.println("query user by >>> ");
        for(Map.Entry<String, String> entry : criteriaList.entrySet()) {
            System.out.println("key: " + entry.getKey() + " / value: " + entry.getValue());

        }
        List<User> userList =
                userRepository.findAll(new Specification<User>() {
                    @Override
                    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<Predicate>();

                        if(criteriaList.containsKey("username") && !criteriaList.get("username").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("username"), criteriaList.get("username")));
                        }

                        if(criteriaList.containsKey("firstname") && !criteriaList.get("firstname").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("firstname"), criteriaList.get("firstname")));
                        }
                        if(criteriaList.containsKey("lastname") && !criteriaList.get("lastname").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("lastname"), criteriaList.get("lastname")));
                        }
                        if(criteriaList.containsKey("email") && !criteriaList.get("email").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("email"), criteriaList.get("email")));
                        }
                        if(criteriaList.containsKey("active") && !criteriaList.get("active").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("active"), criteriaList.get("active").equals("on")));
                        }
                        if(criteriaList.containsKey("userManager") && !criteriaList.get("userManager").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("userManager"), criteriaList.get("userManager").equals("on")));

                        }
                        if(criteriaList.containsKey("roleManager") && !criteriaList.get("roleManager").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("roleManager"), criteriaList.get("roleManager").equals("on")));

                        }
                        if(criteriaList.containsKey("menuManager") && !criteriaList.get("menuManager").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("menuManager"), criteriaList.get("menuManager").equals("on")));

                        }

                        Predicate[] p = new Predicate[predicates.size()];
                        return criteriaBuilder.and(predicates.toArray(p));

                    }
                });
        // if role is passed in, filter out the result by role
        if(criteriaList.containsKey("roleId") && !criteriaList.get("roleId").isEmpty()) {
            for(Iterator<User> iterator = userList.iterator(); iterator.hasNext();) {
                User user = iterator.next();
                Role role = roleService.findByRoleId(Integer.parseInt(criteriaList.get("roleId")));
                if (!isRoleAccessible(user,role)) {
                    // Current user doesn't belong to the specific role, let's not
                    // return this user
                    System.out.println("user " + user.getUsername() + " doesn't have access to " + role.getName());
                    iterator.remove();
                }
            }
        }
        return userList;
    }

    public User save(User user) {
        User newUser = userRepository.save(user);
        userRepository.flush();
        System.out.println("Saved user with id: " + newUser.getId());
        return newUser;

    }

    // TO-DO: Check wither the user can access the menu
    public boolean isRoleAccessible(User user, Role role) {
        for(Role assignedRole : user.getRoles()) {
            if (assignedRole.getId() == role.getId()) {
                return true;
            }

        }
        return false;
    }

    public boolean isMenuAccessible(String url) {
        // Get the menu item from url
        List<MenuItem> menuItems = menuService.findByUrl(url);
        if (menuItems.size() == 0) {
            // Can't find menu from the URL
            // let's just reutrn false
            return false;
        }
        // Get the current user
        User currentUser = getCurrentLoginUser();
        // If the user has no access to any one of the
        // menu item from the URL, the user
        // doesn't have access
        for(MenuItem menuItem : menuItems) {
            if (!menuService.isAccessible(currentUser,menuItem)) {
                return false;
            }
        }
        return true;
    }


    public void grantRoleAccess(User user, Role role) {
        if (!isRoleAccessible(user, role)) {
            user.getRoles().add(role);
            System.out.println("grantRoleAccess / The user: " + user.getUsername() + " now has the following roles: ");
            for(Role currentRole : user.getRoles()) {
                System.out.println(">> " + currentRole.getName());
            }
            save(user);
        }
    }
    public void removeRoleAccess(User user, Role removedRole) {
        if (isRoleAccessible(user, removedRole)) {
            for (Iterator<Role> iterator = user.getRoles().iterator(); iterator.hasNext(); ) {
                Role role = iterator.next();
                if (role.getId() == removedRole.getId()){
                    iterator.remove();
                    save(user);

                    System.out.println("The user: " + user.getUsername() + " now has the following roles: ");
                    for(Role currentRole : user.getRoles()) {
                        System.out.println(">> " + currentRole.getName());
                    }
                    break;
                }
            }
        }
    }

    public void grantWarehouseAccess(int userID, int warehouseID) {
        grantWarehouseAccess(findUserById(userID), warehouseService.findByWarehouseId(warehouseID));
    }

    public void grantWarehouseAccess(User user, Warehouse warehouse) {
        // Only grant the warehouse when the user doesn't have the access yet
        if (!hasWarehouseAccess(user, warehouse)) {
            user.getWarehouses().add(warehouse);
            save(user);
        }
    }


    public void removeWarehouseAccess(int userID, int warehouseID) {
        removeWarehouseAccess(findUserById(userID), warehouseService.findByWarehouseId(warehouseID));
    }

    public void removeWarehouseAccess(User user, Warehouse warehouse) {
        // Only remove the warehouse when the user has the access already
        if (hasWarehouseAccess(user, warehouse)) {
            user.getWarehouses().remove(warehouse);
            save(user);
        }
    }

    public boolean hasWarehouseAccess(User user, Warehouse warehouse) {
        return user.getWarehouses().contains(warehouse);
    }






}