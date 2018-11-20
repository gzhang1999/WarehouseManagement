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
import se.gzhang.scm.wms.common.service.ClientService;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    private static final String APPLICATION_ID = "Inventory";
    private static final String FORM_ID = "Item";
    @Autowired
    ItemService itemService;
    @Autowired
    WarehouseService warehouseService;
    @Autowired
    ClientService clientService;

    @RequestMapping(value="/inventory/item", method = RequestMethod.GET)
    public ModelAndView listItems() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("inventory/item");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/list")
    public WebServiceResponseWrapper queryItem(@RequestParam Map<String, String> parameters) {

        List<Item> itemList = itemService.findItems(parameters);
        return new WebServiceResponseWrapper<List<Item>>(0, "", itemList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/query/{id}")
    public WebServiceResponseWrapper getItem(@PathVariable("id") int itemID) {

        Item item = itemService.findByItemId(itemID);
        if (item == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item by id: " + itemID);
        }
        return new WebServiceResponseWrapper<Item>(0, "", item);
    }
    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/query/name/{name}")
    public WebServiceResponseWrapper getItemByName(@PathVariable("name") String itemName) {

        Item item = itemService.findByItemName(itemName);
        if (item == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item by name: " + itemName);
        }
        return new WebServiceResponseWrapper<Item>(0, "", item);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/delete")
    public WebServiceResponseWrapper deleteItem(@RequestParam("itemID") int itemID) {

        Item item = itemService.findByItemId(itemID);
        if (item == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item by id: " + itemID);
        }
        itemService.deleteByItemId(itemID);
        return new WebServiceResponseWrapper<Item>(0, "", item);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/edit")
    public WebServiceResponseWrapper changeItem(@RequestParam("itemID") int itemID,
                                                @RequestParam("name") String name,
                                                @RequestParam("description") String description) {

        Item item = itemService.findByItemId(itemID);
        if (item == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item by id: " + itemID);
        }

        // Currently we only allow change the description but not the name
        item.setDescription(description);

        Item newItem = itemService.save(item);

        return new WebServiceResponseWrapper<Item>(0, "", newItem);
    }
    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/new")
    public WebServiceResponseWrapper createItem(HttpServletRequest request) {

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        int warehouseID = Integer.parseInt(request.getSession().getAttribute("warehouse_id").toString());
        System.out.println("current warehouse id: " + warehouseID);

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setWarehouse(warehouseService.findByWarehouseId(warehouseID));
        item.setClient(clientService.getDefaultClient());
        Item newItem = itemService.save(item);

        return new WebServiceResponseWrapper<Item>(0, "", newItem);
    }
}
