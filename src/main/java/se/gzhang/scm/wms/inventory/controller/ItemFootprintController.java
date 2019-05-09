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
import se.gzhang.scm.wms.common.service.UnitOfMeasureService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemFootprint;
import se.gzhang.scm.wms.inventory.model.ItemFootprintUOM;
import se.gzhang.scm.wms.inventory.service.ItemFootprintService;
import se.gzhang.scm.wms.inventory.service.ItemFootprintUOMService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ItemFootprintController {
    private static final String APPLICATION_ID = "Inventory";
    private static final String FORM_ID = "ItemFootprint";
    @Autowired
    ItemFootprintService itemFootprintService;
    @Autowired
    ItemFootprintUOMService itemFootprintUOMService;
    @Autowired
    ItemService itemService;
    @Autowired
    UnitOfMeasureService unitOfMeasureService;

    @RequestMapping(value="/inventory/item/footprint", method = RequestMethod.GET)
    public ModelAndView listItemFootprints(@RequestParam Map<String, String> parameters) {

        ModelAndView modelAndView = new ModelAndView();
        List<ItemFootprint> itemFootprintList;
        if (parameters.size() > 0) {
            itemFootprintList = itemFootprintService.findItemFootprints(parameters);
        }
        else {
            itemFootprintList = new ArrayList<>();
        }
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);
        modelAndView.addObject("itemFootprintList",itemFootprintList);

        modelAndView.setViewName("inventory/item/footprint");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/list")
    public WebServiceResponseWrapper queryItemFootprint(@RequestParam Map<String, String> parameters) {

        List<ItemFootprint> itemFootprints = itemFootprintService.findItemFootprints(parameters);
        return new WebServiceResponseWrapper<List<ItemFootprint>>(0, "", itemFootprints);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/query/{id}")
    public WebServiceResponseWrapper getItemFootprint(@PathVariable("id") int itemFootprintID) {

        ItemFootprint itemFootprint = itemFootprintService.findByItemFootprintId(itemFootprintID);
        if (itemFootprint == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotFindItemFootprint", "Can't find the item footprint by id: " + itemFootprintID);
        }
        return new WebServiceResponseWrapper<ItemFootprint>(0, "", itemFootprint);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/delete")
    public WebServiceResponseWrapper deleteItemFootprint(@RequestParam("itemFootprintID") int itemFootprintID) {

        ItemFootprint itemFootprint = itemFootprintService.findByItemFootprintId(itemFootprintID);
        if (itemFootprint == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotFindItemFootprint", "Can't find the item footprint by id: " + itemFootprintID);
        }
        try {
            itemFootprintService.deleteByItemFootprintId(itemFootprintID);
            return new WebServiceResponseWrapper<ItemFootprint>(0, "", itemFootprint);
        }
        catch(GenericException ex) {

            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotSaveItemFootprint", "Error while save item footprint: " + ex.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/edit")
    public WebServiceResponseWrapper changeItemFootprint(@RequestParam("itemFootprintID") int itemFootprintID,
                                                         @RequestParam("name") String name,
                                                         @RequestParam("description") String description,
                                                         @RequestParam("default") boolean defaultFlag) {


        ItemFootprint itemFootprint = itemFootprintService.findByItemFootprintId(itemFootprintID);
        if (itemFootprint == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotFindItemFootprint", "Can't find the item footprint by id: " + itemFootprintID);
        }

        // Currently we only allow change the description but not the name
        itemFootprint.setDescription(description);
        itemFootprint.setDefaultFootprint(defaultFlag);


        try {
            ItemFootprint newItemFootprint = itemFootprintService.save(itemFootprint);

            return new WebServiceResponseWrapper<ItemFootprint>(0, "", newItemFootprint);
        }
        catch(GenericException ex) {

            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotSaveItemFootprint", "Error while save item footprint: " + ex.getMessage());
        }
    }
    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/new")
    public WebServiceResponseWrapper createItemFootprint(@RequestParam("itemName") String itemName,
                                                         @RequestParam("name") String name,
                                                         @RequestParam("description") String description,
                                                         @RequestParam("default") boolean defaultFlag) {

        Item item = itemService.findByItemName(itemName);
        if (item == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotFindItemFootprint", "Can't find the item by name: " + itemName);
        }

        ItemFootprint itemFootprint = new ItemFootprint();
        itemFootprint.setName(name);
        itemFootprint.setDescription(description);
        itemFootprint.setItem(item);
        itemFootprint.setDefaultFootprint(defaultFlag);

        try {
            ItemFootprint newItemFootprint = itemFootprintService.save(itemFootprint);

            return new WebServiceResponseWrapper<ItemFootprint>(0, "", newItemFootprint);
        }
        catch(GenericException ex) {

            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotSaveItemFootprint", "Error while save item footprint: " + ex.getMessage());
        }
    }


    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/uom/query/{id}")
    public WebServiceResponseWrapper getFootprintUOMs(@PathVariable("id") int itemFootprintUOMID) {

        ItemFootprintUOM itemFootprintUOM = itemFootprintUOMService.findByItemFootprintUOMId(itemFootprintUOMID);
        if (itemFootprintUOM == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintUOMException.CannotFindItemFootprintUOM", "Can't find the item footprint UOM by id: " + itemFootprintUOMID);
        }
        return new WebServiceResponseWrapper<ItemFootprintUOM>(0, "", itemFootprintUOM);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/uom/edit")
    public WebServiceResponseWrapper changeItemFootprintUOM(@RequestParam("itemFootprintUOMID") int itemFootprintUOMID,
                                                            @RequestParam("quantity") int quantity,
                                                            @RequestParam("weight") double weight,
                                                            @RequestParam("length") double length,
                                                            @RequestParam("width") double width,
                                                            @RequestParam("height") double height,
                                                            @RequestParam("caseFlag") boolean caseFlag,
                                                            @RequestParam("palletFlag") boolean palletFlag,
                                                            @RequestParam("cartonFlag") boolean cartonFlag) {

        ItemFootprintUOM itemFootprintUOM = itemFootprintUOMService.findByItemFootprintUOMId(itemFootprintUOMID);
        if (itemFootprintUOM == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintUOMException.CannotFindItemFootprintUOM", "Can't find the item footprint UOM by id: " + itemFootprintUOMID);
        }
        itemFootprintUOM.setQuantity(quantity);
        itemFootprintUOM.setWeight(weight);
        itemFootprintUOM.setLength(length);
        itemFootprintUOM.setWidth(width);
        itemFootprintUOM.setHeight(height);
        // Stock UOM is calculated based on quantity
        // itemFootprintUOM.setStockUOM(stockFlag);
        itemFootprintUOM.setCaseUOM(caseFlag);
        itemFootprintUOM.setPalletUOM(palletFlag);
        itemFootprintUOM.setCartonUOM(cartonFlag);
        try{
            ItemFootprintUOM newItemFootprintUOM = itemFootprintUOMService.save(itemFootprintUOM);

            return new WebServiceResponseWrapper<ItemFootprintUOM>(0, "", newItemFootprintUOM);
        }
        catch(GenericException ex) {

            return WebServiceResponseWrapper.raiseError("ItemFootprintUOMException.CannotSaveItemFootprintUOM", "Error while save item footprint uom: " + ex.getMessage());
        }
    }
    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/uom/new")
    public WebServiceResponseWrapper createItemFootprintUOM(@RequestParam("itemFootprintID") int itemFootprintID,
                                                            @RequestParam("unitOfMeasure") String unitOfMeasure,
                                                            @RequestParam("quantity") int quantity,
                                                            @RequestParam("weight") double weight,
                                                            @RequestParam("length") double length,
                                                            @RequestParam("width") double width,
                                                            @RequestParam("height") double height,
                                                            @RequestParam("caseFlag") boolean caseFlag,
                                                            @RequestParam("palletFlag") boolean palletFlag,
                                                            @RequestParam("cartonFlag") boolean cartonFlag) {

        ItemFootprint itemFootprint = itemFootprintService.findByItemFootprintId(itemFootprintID);
        if (itemFootprint == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintException.CannotFindItemFootprint", "Can't find the item footprint by id: " + itemFootprintID);
        }

        ItemFootprintUOM itemFootprintUOM = new ItemFootprintUOM();
        itemFootprintUOM.setQuantity(quantity);
        itemFootprintUOM.setWeight(weight);
        itemFootprintUOM.setLength(length);
        itemFootprintUOM.setWidth(width);
        itemFootprintUOM.setHeight(height);
        itemFootprintUOM.setUnitOfMeasure(unitOfMeasureService.findByUOMName(unitOfMeasure));
        itemFootprintUOM.setItemFootprint(itemFootprint);
        // Stock UOM is calculated based on quantity
        // itemFootprintUOM.setStockUOM(stockFlag);
        itemFootprintUOM.setCaseUOM(caseFlag);
        itemFootprintUOM.setPalletUOM(palletFlag);
        itemFootprintUOM.setCartonUOM(cartonFlag);
        try {
            ItemFootprintUOM newItemFootprintUOM = itemFootprintUOMService.save(itemFootprintUOM);
            return new WebServiceResponseWrapper<ItemFootprintUOM>(0, "", newItemFootprintUOM);
        }
        catch(GenericException ex) {

            return WebServiceResponseWrapper.raiseError("ItemFootprintUOMException.CannotSaveItemFootprintUOM", "Error while save item footprint uom: " + ex.getMessage());
        }
    }


    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/footprint/uom/delete")
    public WebServiceResponseWrapper deleteItemFootprintUOM(@RequestParam("itemFootprintUOMID") int itemFootprintUOMID) {

        ItemFootprintUOM itemFootprintUOM = itemFootprintUOMService.findByItemFootprintUOMId(itemFootprintUOMID);
        if (itemFootprintUOM == null) {
            return WebServiceResponseWrapper.raiseError("ItemFootprintUOMException.CannotFindItemFootprintUOM", "Can't find the item footprint uom by id: " + itemFootprintUOMID);
        }
        itemFootprintUOMService.deleteByItemFootprintId(itemFootprintUOMID);
        return new WebServiceResponseWrapper<ItemFootprintUOM>(0, "", itemFootprintUOM);
    }
}
