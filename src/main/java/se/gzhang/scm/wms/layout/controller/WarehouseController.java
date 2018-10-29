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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.service.WarehouseService;

import javax.servlet.http.HttpSession;

@Controller
public class WarehouseController {

    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "Warehouse";

    @Autowired
    WarehouseService warehouseService;

    @RequestMapping(value="/layout/warehouse", method = RequestMethod.GET)
    public ModelAndView displayWarehouse(HttpSession httpSession) {
        Warehouse warehouse = warehouseService.findByWarehouseId(Integer.parseInt(httpSession.getAttribute("warehouse_id").toString()));
        System.out.println("Warehouse: " + warehouse.getName());
        System.out.println(">> Address: " + warehouse.getAddress() == null ? " N/A" : warehouse.getAddress().getName());
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
        modelAndView.setViewName("layout/warehouse");
        return modelAndView;
    }
}
