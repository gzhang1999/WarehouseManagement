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
import se.gzhang.scm.wms.inbound.service.PutawayService;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;
import se.gzhang.scm.wms.work.service.WorkInstructionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PutawayController {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private WorkInstructionService workInstructionService;
    @Autowired
    private PutawayService putawayService;
    @Autowired
    AreaService areaService;



    private static final String APPLICATION_ID = "Inbound";
    private static final String FORM_ID = "Putaway";


    @RequestMapping(value="/inbound/putaway", method = RequestMethod.GET)
    public ModelAndView listInventoryForPutaway() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);
        modelAndView.addObject("inventoryList",new ArrayList<Inventory>());

        modelAndView.setViewName("inbound/putaway");

        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/putaway/inventory/list")
    public WebServiceResponseWrapper queryInventoryForPutaway(@RequestParam Map<String, String> parameters) {

        // we will only list inventory from receiving stage area

        List<Inventory> inventoryList = new ArrayList<>();
        if (!parameters.containsKey("area_id") || parameters.get("area_id").equals("")) {
            List<Area> areaList = areaService.findReceingStageAreas();
            for(Area receivingStageArea : areaList) {
                Map<String, String> paramtersWithArea = new HashMap<>(parameters);
                paramtersWithArea.put("area_id", String.valueOf(receivingStageArea.getId()));
                List<Inventory> inventoryInArea = inventoryService.findInventory(paramtersWithArea);
                inventoryList.addAll(inventoryInArea);
            }
        }
        else {
            inventoryList = inventoryService.findInventory(parameters);
        }

        return new WebServiceResponseWrapper<List<Inventory>>(0, "", inventoryList);
    }


    @ResponseBody
    @RequestMapping(value="/ws/inbound/putaway/immediate")
    public WebServiceResponseWrapper putaway(@RequestParam("lpn") String lpn,
                                             @RequestParam(name = "suggestDestination", required = false, defaultValue = "") String suggestDestinationStr,
                                             @RequestParam(name = "destinationLocation", required = false, defaultValue = "") String destinationLocationStr) {

        System.out.println("move " + lpn + " to destination: " + suggestDestinationStr);

        List<Inventory> inventoryList = inventoryService.findInventoryByLPN(lpn);
        Location destinationLocation = null;
        if (!destinationLocationStr.equals("")) {
            destinationLocation = locationService.findByLocationName(destinationLocationStr);
        }
        else {
            destinationLocation = locationService.findByLocationName(suggestDestinationStr);
        }
        for(Inventory inventory : inventoryList) {
            inventoryService.moveInventory(inventory, destinationLocation);
        }
        // return the updated inventory
        return new WebServiceResponseWrapper<List<Inventory>>(0, "", inventoryService.findInventoryByLPN(lpn));
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/putaway/work")
    public WebServiceResponseWrapper generatePutawayWork(@RequestParam("lpn") String lpn,
                                                         @RequestParam(name = "suggestDestination", required = false, defaultValue = "") String suggestDestinationStr,
                                                         @RequestParam(name = "destinationLocation", required = false, defaultValue = "") String destinationLocationStr) {


        List<Inventory> inventoryList = inventoryService.findInventoryByLPN(lpn);
        if (destinationLocationStr.equals("") && suggestDestinationStr.equals("")) {
            // The inventory doesn't have any location allocated yet
            // let's run the putaway policy logic to get a suggested location
            for(Inventory inventory : inventoryList) {
                putawayService.generatePutawayWork(inventory);
            }
        }
        else if (!destinationLocationStr.equals("")) {
            Location destinationLocation = locationService.findByLocationName(destinationLocationStr);
            for(Inventory inventory : inventoryList) {
                workInstructionService.generatePutawayWorkInstruction(inventory, destinationLocation);
            }
        }
        else {
            Location destinationLocation = locationService.findByLocationName(suggestDestinationStr);
            for(Inventory inventory : inventoryList) {
                // Allocate the destination location and then generate a putaway work into this location
                locationService.allocateLocation(destinationLocation, inventory);
                workInstructionService.generatePutawayWorkInstruction(inventory, destinationLocation);
            }
        }

        return new WebServiceResponseWrapper<List<Inventory> >(0, "", inventoryService.findInventoryByLPN(lpn));
    }

}
