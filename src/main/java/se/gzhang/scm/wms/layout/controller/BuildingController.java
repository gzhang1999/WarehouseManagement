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
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.service.BuildingService;
import se.gzhang.scm.wms.layout.service.WarehouseService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class BuildingController {

    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "Building";

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    BuildingService buildingService;

    @RequestMapping(value="/layout/building", method = RequestMethod.GET)
    public ModelAndView displayBuilding(HttpSession httpSession) {
        // only display the information in current login warehouse

        Warehouse currentWarehouse = warehouseService.findByWarehouseId(Integer.parseInt(httpSession.getAttribute("warehouse_id").toString()));
        List<Building> buildingList = buildingService.findByWarehouseId(currentWarehouse.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("buildingList", buildingList);
        modelAndView.setViewName("layout/building");
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);
        return modelAndView;

    }
}
