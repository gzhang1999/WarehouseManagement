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

package se.gzhang.scm.wms.layout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class WarehouseController {

    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "Warehouse";

    @Autowired
    WarehouseService warehouseService;
    @Autowired
    UserService userService;

    @RequestMapping(value="/layout/warehouse", method = RequestMethod.GET)
    public ModelAndView displayWarehouse(HttpSession httpSession) {
        Warehouse warehouse = warehouseService.findByWarehouseId(Integer.parseInt(httpSession.getAttribute("warehouse_id").toString()));
        int buildingCount = warehouse.getBuildings().size();
        int areaCount = 0;
        int locationCount = 0;
        for(Building building : warehouse.getBuildings()) {
            areaCount += building.getAreas().size();
            for(Area area : building.getAreas()) {
                locationCount += area.getLocations().size();
            }
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("warehouse",warehouse);
        modelAndView.addObject("building_count",buildingCount);
        modelAndView.addObject("area_count",areaCount);
        modelAndView.addObject("location_count",locationCount);
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        // Get all the user and the accessible user so the current user
        // can grant the other users with the access to the warehouse
        // only available if the current user is a user manager
        User currentUser = userService.getCurrentLoginUser();

        modelAndView.addObject("isUserManager", currentUser.isUserManager());
        if (currentUser.isUserManager()) {
            modelAndView.addObject("userList", userService.findAll());
            modelAndView.addObject("accessibleUserIDList",warehouseService.getAccessibleUserIDs(warehouse));
        }
        modelAndView.setViewName("layout/warehouse");
        return modelAndView;
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/warehouse/assign/user/{id}", method = RequestMethod.POST)
    public WebServiceResponseWrapper grantUserAccess(@PathVariable("id") int warehouseID,
                                                     @RequestParam("userID") int userID,
                                                     @RequestParam("assigned") boolean assigned) {

        // Check whether the user can manager the menus;
        User currentLoginUser = userService.getCurrentLoginUser();

        // The user need to have the right for menu assignment
        if (currentLoginUser.isUserManager()) {
            User user = userService.findUserById(userID);
            Warehouse warehouse = warehouseService.findByWarehouseId(warehouseID);

            if (user == null) {
                return WebServiceResponseWrapper.raiseError(10001, "Can not find the user by ID: " + userID);
            }
            else if (warehouse == null){
                return WebServiceResponseWrapper.raiseError(10001, "Can not find the warehouse by ID: " + warehouseID);
            }
            else {
                if (assigned) {
                    userService.grantWarehouseAccess(user, warehouse);
                } else {
                    userService.removeWarehouseAccess(user, warehouse);
                }
                String message = "User: " + user.getUsername() + ", Warehouse: " + warehouse.getName() + ", Accessible? " + userService.hasWarehouseAccess(user,warehouse);
                return WebServiceResponseWrapper.raiseError(0, message);
            }

        }
        else {

            return WebServiceResponseWrapper.raiseError(10000, "The user doesn't have right to manager other users");
        }

    }
}
