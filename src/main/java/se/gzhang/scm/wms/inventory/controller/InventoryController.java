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

package se.gzhang.scm.wms.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.ItemFootprint;
import se.gzhang.scm.wms.inventory.model.ItemFootprintUOM;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.inventory.service.InventoryStatusService;
import se.gzhang.scm.wms.inventory.service.ItemFootprintService;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class InventoryController {
    private static final String APPLICATION_ID = "Inventory";
    private static final String FORM_ID = "Inventory";
    @Autowired
    InventoryService inventoryService;
    @Autowired
    LocationService locationService;
    @Autowired
    ItemFootprintService itemFootprintService;
    @Autowired
    InventoryStatusService inventoryStatusService;

    @RequestMapping(value="/inventory/inventory", method = RequestMethod.GET)
    public ModelAndView listInventorys() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("inventory/inventory");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/inventory/list")
    public WebServiceResponseWrapper queryInventory(@RequestParam Map<String, String> parameters) {

        List<Inventory> inventoryList = inventoryService.findInventory(parameters);
        //List<Inventory> inventoryList = new ArrayList<>();
        return new WebServiceResponseWrapper<List<Inventory>>(0, "", inventoryList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/inventory/query/{id}")
    public WebServiceResponseWrapper getInventory(@PathVariable("id") int inventoryID) {

        Inventory inventory = inventoryService.findByInventoryID(inventoryID);
        if (inventory == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the inventory by id: " + inventoryID);
        }
        return new WebServiceResponseWrapper<Inventory>(0, "", inventory);
    }


    @ResponseBody
    @RequestMapping(value="/ws/inventory/inventory/new")
    public WebServiceResponseWrapper createInventory(@RequestParam("locationID") int locationID,
                                                     @RequestParam("itemFootprintID") int itemFootprintID,
                                                     @RequestParam("lpn") String lpn,
                                                     @RequestParam("quantity") int quantity,
                                                     @RequestParam("itemFootprintUOM") String itemFootprintUOM,
                                                     @RequestParam("reasonCode") String reasonCode,
                                                     @RequestParam("reason") String reason) {

        ItemFootprint itemFootprint = itemFootprintService.findByItemFootprintId(itemFootprintID);
        if (itemFootprint == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item footprint by id: " + itemFootprintID);
        }
        Location location = locationService.findByLocationId(locationID);
        if (location == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the location by id: " + locationID);
        }
        int unitQuantity = quantity;
        for(ItemFootprintUOM itemFootprintUOMIterator : itemFootprint.getItemFootprintUOMs()) {
            if (itemFootprintUOMIterator.getUnitOfMeasure().getName().equals(itemFootprintUOM)) {
                unitQuantity = quantity * itemFootprintUOMIterator.getQuantity();
            }
        }

        Inventory inventory = new Inventory();
        inventory.setItemFootprint(itemFootprint);
        inventory.setLocation(location);
        inventory.setQuantity(unitQuantity);
        inventory.setLpn(lpn);
        // TO-DO: Setup the inventory with default inventory status
        inventory.setInventoryStatus(inventoryStatusService.getDefaultInventoryStatus());

        Inventory newInventory = inventoryService.createInventory(inventory, reasonCode, reason);

        return new WebServiceResponseWrapper<Inventory>(0, "", newInventory);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/inventory/adjustQuantity")
    public WebServiceResponseWrapper changeInventory(@RequestParam("inventoryID") int inventoryID,
                                                     @RequestParam("locationID") int locationID,
                                                     @RequestParam("itemFootprintID") int itemFootprintID,
                                                     @RequestParam("lpn") String lpn,
                                                     @RequestParam("quantity") int quantity,
                                                     @RequestParam("itemFootprintUOM") String itemFootprintUOM,
                                                     @RequestParam("reasonCode") String reasonCode,
                                                     @RequestParam("reason") String reason) {

        Inventory inventory = inventoryService.findByInventoryID(inventoryID);
        if (inventory == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the inventory by id: " + inventoryID);
        }

        ItemFootprint itemFootprint = itemFootprintService.findByItemFootprintId(itemFootprintID);
        if (itemFootprint == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item footprint by id: " + itemFootprintID);
        }

        int unitQuantity = quantity;
        for(ItemFootprintUOM itemFootprintUOMIterator : itemFootprint.getItemFootprintUOMs()) {
            if (itemFootprintUOMIterator.getUnitOfMeasure().getName().equals(itemFootprintUOM)) {
                unitQuantity = quantity * itemFootprintUOMIterator.getQuantity();
            }
        }

        // User is only allow to change the quantity
        Inventory newInventory = inventoryService.adjustInventory(inventory, unitQuantity, reasonCode, reason);

        return new WebServiceResponseWrapper<Inventory>(0, "", newInventory);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/inventory/delete")
    public WebServiceResponseWrapper deleteInventory(@RequestParam("inventoryID") int inventoryID,
                                                     @RequestParam("reasonCode") String reasonCode,
                                                     @RequestParam("reason") String reason) {

        Inventory inventory = inventoryService.findByInventoryID(inventoryID);
        if (inventory == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the inventory by id: " + inventoryID);
        }

        System.out.println("## 1. Delete inventory: " + inventory.getId());
        inventoryService.deleteInventory(inventory, reasonCode, reason);

        return new WebServiceResponseWrapper<Inventory>(0, "", inventory);
    }
}
