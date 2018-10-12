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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.menu.model.MenuItem;
import se.gzhang.scm.wms.menu.repository.MenuItemRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service("menuService")
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Cacheable("assignedMenuList")
    public List<MenuItem> getAssignedMenuItemList(int userID) {
        return getStructuredMenuItemList(userID);
    }

    @Cacheable("structuredMenuList")
    public List<MenuItem> getStructuredMenuItemList() {
        List<MenuItem> menuItemList = findAll();
        System.out.println("Total Menu items: " + menuItemList.size());

        List<MenuItem> parentMenuItemList = sortMenuItem(getParentMenuItemList(menuItemList));
        System.out.println("Total parent Menu items: " + parentMenuItemList.size());

        for (MenuItem parentMenuItem : parentMenuItemList) {
            parentMenuItem.setChildMenuList(getChildMenuItemList(parentMenuItem.getId(), menuItemList));
            System.out.println("> Parent: " + parentMenuItem.getName() + " has " + parentMenuItem.getChildMenuList().size() + " children");
        }

        return parentMenuItemList;
    }
    @Cacheable("structuredAssignedMenuList")
    public List<MenuItem> getStructuredMenuItemList(int userID) {
        List<MenuItem> menuItemList = findAll(userID);
        System.out.println("Total Menu items: " + menuItemList.size());

        List<MenuItem> parentMenuItemList = sortMenuItem(getParentMenuItemList(menuItemList));
        System.out.println("Total parent Menu items: " + parentMenuItemList.size());

        for (MenuItem parentMenuItem : parentMenuItemList) {
            parentMenuItem.setChildMenuList(getChildMenuItemList(parentMenuItem.getId(), menuItemList));
            System.out.println("> Parent: " + parentMenuItem.getName() + " has " + parentMenuItem.getChildMenuList().size() + " children");
        }

        return parentMenuItemList;
    }

    @Cacheable("fullMenuList")
    private List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Cacheable("fullAssignedMenuList")
    private List<MenuItem> findAll(int userId) {
        return menuItemRepository.findAll();
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

        return true;
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

    public MenuItem findParentMenuByName(String name, int userID) {
        return findMenuByName(name, true, 0, userID);
    }


    public List<MenuItem> findMenu(Map<String, String> criteriaList, int userID) {

        System.out.println("1. criteriaList.containsKey('parentMenuID')? " + (criteriaList.containsKey("parentMenuID")));
        if (criteriaList.containsKey("parentMenuID")) {
            System.out.println("2. criteriaList.get('parentMenuID').isEmpty()? " + criteriaList.get("parentMenuID").isEmpty());
        }
        System.out.println("3. criteriaList.containsKey('menuGroup')? " + (criteriaList.containsKey("menuGroup")));
        if (criteriaList.containsKey("menuGroup")) {
            System.out.println("4. criteriaList.get('menuGroup').isEmpty()? " + criteriaList.get("menuGroup").isEmpty());
        }
        System.out.println("5. criteriaList.containsKey('parentMenuName')? " + (criteriaList.containsKey("parentMenuName")));
        if (criteriaList.containsKey("parentMenuName")) {
            System.out.println("6. criteriaList.get('parentMenuName').isEmpty()? " + criteriaList.get("parentMenuName").isEmpty());
        }

        if (!criteriaList.containsKey("parentMenuID") ||
                criteriaList.get("parentMenuID").isEmpty()) {
            // both parent menu id and parent menu name are  valid criterias.
            // If the operator passed in parent menu id, then we will query by the menu id
            // otherwise, if the operator pass in the parent menu name, we will get the menu id
            //       from the name and query by the menu id
            // the parameter name for the parent menu name can be either menuGroup or parentMenuName
            String parentMenuName = "";
            System.out.println(" 1 & 2 ");
            if(criteriaList.containsKey("menuGroup") && !criteriaList.get("menuGroup").isEmpty()) {
                parentMenuName = criteriaList.get("menuGroup");
                System.out.println(" 3 & 4");
            }
            else if (criteriaList.containsKey("parentMenuName") && !criteriaList.get("parentMenuName").isEmpty()) {
                parentMenuName = criteriaList.get("parentMenuName");
                System.out.println(" 5 & 6 ");
            }
            System.out.println(" parentMenuName: " + parentMenuName);
            if (parentMenuName.length() > 0) {
                // Get the parent menu by name
                MenuItem parentMenu = findParentMenuByName(parentMenuName, userID);
                System.out.println(" parentMenu != null: " + (parentMenu != null));
                if (parentMenu != null) {
                    criteriaList.put("parentMenuID", String.valueOf(parentMenu.getId()));
                }
                else {
                    criteriaList.put("parentMenuID", "-1");
                }
            }
        }

        for(Map.Entry<String, String> queryString : criteriaList.entrySet()) {
            System.out.println("key: " + queryString.getKey() + ", value: " + queryString.getValue());
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
