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

package se.gzhang.scm.wms.menu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.authorization.model.Role;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.service.RoleService;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.menu.model.MenuItem;
import se.gzhang.scm.wms.menu.repository.MenuItemRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service("menuService")

@PropertySource("classpath:application.properties")
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    // Specific url for
    // 1. user manager
    // 2. role manager
    // 3. menu manager
    // Those 3 url are managed in a different way as the normal menu
    @Value( "${se.gzhang.menuManager.url}" )
    private String menuManagerUrl;
    @Value( "${se.gzhang.roleManager.url}" )
    private String roleManagerUrl;
    @Value( "${se.gzhang.userManager.url}" )
    private String userManagerUrl;


    @Cacheable("assignedMenuList")
    public List<MenuItem> getAssignedMenuItemList(int userID) {
        return getStructuredMenuItemList(userID);

    }

    @Cacheable("structuredMenuList")
    public List<MenuItem> getStructuredMenuItemList() {
        List<MenuItem> menuItemList = findAll();

        List<MenuItem> parentMenuItemList = sortMenuItem(getParentMenuItemList(menuItemList));

        for (MenuItem parentMenuItem : parentMenuItemList) {
            parentMenuItem.setChildMenuList(getChildMenuItemList(parentMenuItem.getId(), menuItemList));
        }

        return parentMenuItemList;
    }
    @Cacheable("structuredAssignedMenuList")
    public List<MenuItem> getStructuredMenuItemList(int userID) {
        List<MenuItem> menuItemList = findAll(userID);
        List<MenuItem> parentMenuItemList = sortMenuItem(getParentMenuItemList(menuItemList));

        for (MenuItem parentMenuItem : parentMenuItemList) {
            parentMenuItem.setChildMenuList(sortMenuItem(getChildMenuItemList(parentMenuItem.getId(), menuItemList)));
        }

        return parentMenuItemList;
    }

    @Cacheable("fullMenuList")
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> findByUrl(String url){
        return menuItemRepository.findByUrl(url);
    }

    // Whether load the parent menu's info. The parent menu
    // info will be loaded mainly for display purpose
    public List<MenuItem> findAll(boolean loadParentMenu) {
        if (loadParentMenu) {
            List<MenuItem> menuItemList = findAll();

            for(MenuItem menuItem : menuItemList) {
                // Setup the parent menu name
                if (getParentMenu(menuItem) != null) {
                    menuItem.setParentMenuName(getParentMenu(menuItem).getName());
                }
                else {
                    menuItem.setParentMenuName("");
                }

            }
            return menuItemList;
        }
        else {
            return findAll();
        }
    }

    @Cacheable("fullAssignedMenuList")
    public List<MenuItem> findAll(int userId) {
        List<MenuItem> assignedMenuList = menuItemRepository.findAll();
        List<MenuItem> fullMenuList = menuItemRepository.findAll();

        User user = userService.findUserById(userId);
        // Remove any child menu item that the user doesn't have access to
        for(Iterator<MenuItem> itemIterator = assignedMenuList.iterator(); itemIterator.hasNext();) {
            MenuItem menuItem = itemIterator.next();
            if (!isParentMenu(menuItem.getId(), fullMenuList) &&
                    !isAccessible(user, menuItem)) {
                // remove the menu from the return if the user
                // doesn't have access
                itemIterator.remove();
            }
        }
        // Remove any parent menu item which doesn't have any child menu item
        // that the current user has access
        for(Iterator<MenuItem> itemIterator = assignedMenuList.iterator(); itemIterator.hasNext();) {
            MenuItem menuItem = itemIterator.next();
            if (isParentMenu(menuItem.getId(), fullMenuList)) {
                // Get all children of this parent menu
                List<MenuItem> childMenuItemList = getChildMenuItemList(menuItem.getId(), assignedMenuList);
                if (childMenuItemList.size() == 0) {
                    // There's no child menu item left in the assignedMenuList,
                    // which means the current user doesn't have any access to
                    // any of the child menu of this parent menu
                    // let's remove the parent menu as well
                    itemIterator.remove();
                }
            }

        }
        return assignedMenuList;
    }



    private List<MenuItem> sortMenuItem(List<MenuItem> menuItemList) {
        Collections.sort(menuItemList, new Comparator<MenuItem>() {
                @Override
            public int compare(MenuItem menuItem1, MenuItem menuItem2) {
                    return menuItem1.getSequence() - menuItem2.getSequence();
                }
        });
        return menuItemList;
    }

    private List<MenuItem> getParentMenuItemList(List<MenuItem> menuItemList) {

        List<MenuItem> parentMenuItemList = new ArrayList<MenuItem>();
        for(MenuItem menuItem : menuItemList) {
            if (isParentMenu(menuItem.getId(), menuItemList)) {
                parentMenuItemList.add(menuItem);
            }
        }
        return parentMenuItemList;
    }

    private boolean isParentMenu(int menuID, List<MenuItem> menuItemList) {

        for(MenuItem menuItem : menuItemList) {
            if (menuItem.getParentMenuID() == menuID) {
                return true;
            }
        }
        return false;

    }

    private List<MenuItem> getChildMenuItemList(int parentMenuItemId, List<MenuItem> menuItemList) {

        List<MenuItem> childMenuItemList = new ArrayList<MenuItem>();
        for(MenuItem menuItem : menuItemList) {
            if (menuItem.getParentMenuID() == parentMenuItemId) {
                childMenuItemList.add(menuItem);
            }
        }
        return childMenuItemList;
    }

    // After we get the raw data from the database by JPA, this method
    // will fill in the parent menu name as it is not persist in database
    public MenuItem getParentMenu(MenuItem childMenuItem, List<MenuItem> menuItemList){
        for(MenuItem menu : menuItemList) {
            if (menu.getId() == childMenuItem.getParentMenuID()) {
                return  menu;
            }
        }
        return null;
    }

    public MenuItem getParentMenu(MenuItem menuItem) {
        return getParentMenu(menuItem, findAll());
    }

    // TO-DO: Check wither the user can access the menu
    public boolean isAccessible(int userID, int menuID) {

        return isAccessible(userService.findUserById(userID), findMenuItemById(menuID));
    }

    public boolean isAccessible(User user, MenuItem menuItem) {
        // if the menu URL is one of the following specific url,
        // check the correspondent flag from the user,
        // otherwise, check the authentication against the role
        if (menuItem.getUrl().equals(menuManagerUrl) && user.isMenuManager()) {
            return true;
        }
        else if (menuItem.getUrl().equals(userManagerUrl) && user.isUserManager()) {
            return true;
        }
        else if (menuItem.getUrl().equals(roleManagerUrl) && user.isRoleManager()) {
            return true;
        }
        // The user has access to the menu as long as
        // one of the role has access
        for (Role role : user.getRoles()) {
            if (isAccessible(role, menuItem)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAccessible(Role role, MenuItem menuItem) {
        return roleService.isAccessible(role,menuItem);
    }

    public MenuItem findMenuItemById(int menuID, boolean fullyLoaded) {
        if (!fullyLoaded) {
            return menuItemRepository.findById(menuID);
        }
        else {
            // load both fields form database and fields not
            // saved directly in database
            MenuItem menuItem = menuItemRepository.findById(menuID);
            MenuItem parentMenu = menuItemRepository.findById(menuItem.getParentMenuID());
            if (parentMenu != null) {
                menuItem.setParentMenuName(parentMenu.getName());
            }
            menuItem.setChildMenuList(menuItemRepository.findByParentMenuID(menuID));
            return menuItem;
        }
    }
    public MenuItem findMenuItemById(int menuID) {
        return findMenuItemById(menuID, true);
    }

    public MenuItem save(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);

    }

    public MenuItem findMenuByName(String name, boolean isParentMenu, int parentMenuID, int userID) {
        List<MenuItem> assignedMenuItemList = getAssignedMenuItemList(userID);
        if (isParentMenu) {
            for(MenuItem menuItem : assignedMenuItemList) {
                if (menuItem.getName().equals(name)) {
                    return menuItem;
                }
            }
            return null;
        }
        else {
            for(MenuItem menuItem : assignedMenuItemList) {
                if (menuItem.getId() == parentMenuID) {
                    for(MenuItem childMenuItem : menuItem.getChildMenuList()) {
                        if (childMenuItem.getName().equals(name)) {
                            return childMenuItem;
                        }
                    }
                }
            }
            return null;
        }
    }

    // Find menu by name. If the menu is parent menu(isParentMenu = true), then we will only
    // return the parent menu that matches with the name. If the menu to be searched is not
    // a parent menu, then the function is supposed to be invoke with a parentMenuID and it
    // will only returns the menu matches with the menu name and under certain parent menu
    public MenuItem findMenuByName(String name, boolean isParentMenu, int parentMenuID) {
        List<MenuItem> menuItemList = getStructuredMenuItemList();
        if (isParentMenu) {
            for(MenuItem menuItem : menuItemList) {
                if (menuItem.getName().equals(name)) {
                    return menuItem;
                }
            }
            return null;
        }
        else {
            for(MenuItem menuItem : menuItemList) {
                if (menuItem.getId() == parentMenuID) {
                    for(MenuItem childMenuItem : menuItem.getChildMenuList()) {
                        if (childMenuItem.getName().equals(name)) {
                            return childMenuItem;
                        }
                    }
                }
            }
            return null;
        }
    }

    public MenuItem findParentMenuByName(String name, int userID) {
        return findMenuByName(name, true, 0, userID);
    }

    public MenuItem findParentMenuByName(String name) {
        return findMenuByName(name, true, 0);
    }

    public List<MenuItem> findMenu(Map<String, String> criteriaList) {

        if (!criteriaList.containsKey("parentMenuID") ||
                criteriaList.get("parentMenuID").isEmpty()) {
            // both parent menu id and parent menu name are  valid criterias.
            // If the operator passed in parent menu id, then we will query by the menu id
            // otherwise, if the operator pass in the parent menu name, we will get the menu id
            //       from the name and query by the menu id
            // the parameter name for the parent menu name can be either menuGroup or parentMenuName
            String parentMenuName = "";
            if(criteriaList.containsKey("menuGroup") && !criteriaList.get("menuGroup").isEmpty()) {
                parentMenuName = criteriaList.get("menuGroup");
            }
            else if (criteriaList.containsKey("parentMenuName") && !criteriaList.get("parentMenuName").isEmpty()) {
                parentMenuName = criteriaList.get("parentMenuName");
            }
            if (parentMenuName.length() > 0) {
                // Get the parent menu by name
                MenuItem parentMenu = findParentMenuByName(parentMenuName);
                if (parentMenu != null) {
                    criteriaList.put("parentMenuID", String.valueOf(parentMenu.getId()));
                }
                else {
                    criteriaList.put("parentMenuID", "-1");
                }
            }
        }

        return menuItemRepository.findAll(new Specification<MenuItem>() {
            @Override
            public Predicate toPredicate(Root<MenuItem> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(criteriaList.containsKey("parentMenuID") && !criteriaList.get("parentMenuID").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("parentMenuID"), Integer.parseInt(criteriaList.get("parentMenuID"))));
                }

                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }

                if(criteriaList.containsKey("multiLanguageSupportID") && !criteriaList.get("multiLanguageSupportID").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("multiLanguageSupportID"), criteriaList.get("multiLanguageSupportID")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });

    }

}
