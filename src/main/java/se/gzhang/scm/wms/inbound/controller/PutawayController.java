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

package se.gzhang.scm.wms.inbound.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.inbound.model.PutawayPolicy;
import se.gzhang.scm.wms.inbound.service.PutawayPolicyService;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class PutawayController {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private LocationService locationService;

    @ResponseBody
    @RequestMapping(value="/ws/inbound/putaway/")
    public WebServiceResponseWrapper putaway(@RequestParam("lpn") String lpn,
                                             @RequestParam("suggestDestination") String suggestDestination) {

        System.out.println("move " + lpn + " to destination: " + suggestDestination);

        List<Inventory> inventoryList = inventoryService.findInventoryByLPN(lpn);
        Location destinationLocation = locationService.findByLocationName(suggestDestination);
        for(Inventory inventory : inventoryList) {
            inventoryService.moveInventory(inventory, destinationLocation);
        }
        // return the updated inventory
        return new WebServiceResponseWrapper<List<Inventory>>(0, "", inventoryService.findInventoryByLPN(lpn));
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/putaway/work")
    public WebServiceResponseWrapper generatePutawayWork(@RequestParam("lpn") String lpn,
                                                         @RequestParam("suggestDestination") String suggestDestination) {

        System.out.println("generate work queue to move " + lpn + " to destination: " + suggestDestination);
        return new WebServiceResponseWrapper<String >(0, "", "Success!");
    }

}
