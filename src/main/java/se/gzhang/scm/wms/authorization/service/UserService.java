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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service("userService")
public class UserService {

    private UserRepository userRepository;
    private RoleService roleService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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


    public List<User> findUsers(Map<String, String> criteriaList, int userID) {
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

                        Predicate[] p = new Predicate[predicates.size()];
                        return criteriaBuilder.and(predicates.toArray(p));

                    }
                });
        // if role is passed in, filter out the result by role
        if(criteriaList.containsKey("roleId") && !criteriaList.get("roleId").isEmpty()) {
            for(Iterator<User> iterator = userList.iterator(); iterator.hasNext();) {
                User user = iterator.next();
                Role role = roleService.findById(Integer.parseInt(criteriaList.get("roleId")));
                if (!isAccessible(user,role)) {
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
    public boolean isAccessible(User user, Role role) {
        for(Role assignedRole : user.getRoles()) {
            if (assignedRole.getId() == role.getId()) {
                System.out.println(user.getUsername() + " has access to role " + role.getName());
                return true;
            }

        }
        System.out.println(user.getUsername() + " has NO access to role " + role.getName());
        return false;
    }


    public void grantRoleAccess(User user, Role role) {
        if (!isAccessible(user, role)) {
            user.getRoles().add(role);
            System.out.println("grantRoleAccess / The user: " + user.getUsername() + " now has the following roles: ");
            for(Role currentRole : user.getRoles()) {
                System.out.println(">> " + currentRole.getName());
            }
            save(user);
        }
    }
    public void removeRoleAccess(User user, Role removedRole) {
        if (isAccessible(user, removedRole)) {
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



}