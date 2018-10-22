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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.authorization.model.Role;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.repository.RoleRepository;
import se.gzhang.scm.wms.authorization.repository.UserRepository;
import se.gzhang.scm.wms.menu.model.MenuItem;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.awt.*;
import java.util.*;
import java.util.List;

@Service("roleService")
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Cacheable("fullRoles")
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public List<Role> findAll(boolean cacheable) {
        if (cacheable) {
            return findAll();
        }
        else {
            return roleRepository.findAll();
        }
    }
    public Role findByName(String  name) {return roleRepository.findByName(name);}
    public Role findByRoleId(int roleId) {
        return roleRepository.findById(roleId);
    }

    public List<Role> getRolesWithMenu(MenuItem menuItem) {
        List<Role> roleList = findAll();
        List<Role> assignedRoles = new ArrayList<>();
        for(Role role : roleList) {
            for(MenuItem assignedMenu : role.getMenuItems()) {
                if (assignedMenu.getId() == menuItem.getId()) {
                    assignedRoles.add(role);
                    break;
                }
            }
        }
        return assignedRoles;

    }

    // TO-DO: Check wither the user can access the menu
    public boolean isAccessible(Role role, MenuItem menuItem) {
        for(MenuItem assignedMenu : role.getMenuItems()) {
            if (assignedMenu.getId() == menuItem.getId()) {
                return true;
            }
        }
        return false;
    }


    public void grantMenuAccess(int roleID, MenuItem menuItem) {
        grantMenuAccess(findByRoleId(roleID), menuItem);
    }
    public void grantMenuAccess(Role role, MenuItem menuItem) {
        // Only add the menu item to the role when
        // the role had no access to the menu before
        if (!isAccessible(role, menuItem)) {
            role.getMenuItems().add(menuItem);
            save(role);
        }
    }

    public void removeMenuAccess(int roleID, MenuItem menuItem) {
        removeMenuAccess(findByRoleId(roleID), menuItem);

    }
    public void removeMenuAccess(Role role, MenuItem menuItem) {
        // Only add the menu item to the role when
        // the role had no access to the menu before
        if (isAccessible(role, menuItem)) {

            for(Iterator<MenuItem> iterator = role.getMenuItems().iterator(); iterator.hasNext();) {
                MenuItem assignedMenu = iterator.next();
                if (assignedMenu.getId() == menuItem.getId()) {
                    // role.getMenuItems().remove(assignedMenu);
                    iterator.remove();
                }

            }
            save(role);
        }
    }

    public Role save(Role role) {
        Role newRole = roleRepository.save(role);
        roleRepository.flush();
        System.out.println("Saved role with id: " + newRole.getId());
        return newRole;

    }


    public List<Role> findRoles(Map<String, String> criteriaList) {
        System.out.println("query role by >>> ");
        for(Map.Entry<String, String> entry : criteriaList.entrySet()) {
            System.out.println("key: " + entry.getKey() + " / value: " + entry.getValue());

        }
        return roleRepository.findAll(new Specification<Role>() {
                    @Override
                    public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<Predicate>();

                        if(criteriaList.containsKey("rolename") && !criteriaList.get("rolename").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("rolename")));
                        }

                        Predicate[] p = new Predicate[predicates.size()];
                        return criteriaBuilder.and(predicates.toArray(p));

                    }
                });
    }


}