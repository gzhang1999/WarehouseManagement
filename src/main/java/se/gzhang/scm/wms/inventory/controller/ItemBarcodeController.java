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
import se.gzhang.scm.wms.inventory.model.ItemBarcode;
import se.gzhang.scm.wms.inventory.model.ItemBarcodeType;
import se.gzhang.scm.wms.inventory.service.ItemBarcodeService;
import se.gzhang.scm.wms.inventory.service.ItemBarcodeTypeService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class ItemBarcodeController {
    private static final String APPLICATION_ID = "Inventory";
    private static final String FORM_ID = "ItemBarcode";
    @Autowired
    ItemBarcodeService itemBarcodeService;
    @Autowired
    ItemService itemService;
    @Autowired
    ItemBarcodeTypeService itemBarcodeTypeService;

    @RequestMapping(value="/inventory/item/barcode", method = RequestMethod.GET)
    public ModelAndView listItems() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("inventory/item/barcode");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/barcode/list")
    public WebServiceResponseWrapper queryItemBarcode(@RequestParam Map<String, String> parameters) {

        List<ItemBarcode> itemBarcodeList = itemBarcodeService.findItemBarcodes(parameters);
        return new WebServiceResponseWrapper<List<ItemBarcode>>(0, "", itemBarcodeList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/barcode/query/{id}")
    public WebServiceResponseWrapper getItemBarcode(@PathVariable("id") int itemBarcodeID) {

        ItemBarcode itemBarcode = itemBarcodeService.findByItemBarcodeId(itemBarcodeID);
        if (itemBarcode == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item barcode by id: " + itemBarcodeID);
        }
        return new WebServiceResponseWrapper<ItemBarcode>(0, "", itemBarcode);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/barcode/delete")
    public WebServiceResponseWrapper deleteItemBarcode(@RequestParam("itemBarcodeID") int itemBarcodeID) {

        ItemBarcode itemBarcode = itemBarcodeService.findByItemBarcodeId(itemBarcodeID);
        if (itemBarcode == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item barcode by id: " + itemBarcodeID);
        }
        itemBarcodeService.deleteByItemBarcodeId(itemBarcodeID);
        return new WebServiceResponseWrapper<ItemBarcode>(0, "", itemBarcode);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/barcode/edit")
    public WebServiceResponseWrapper changeItemBarcode(@RequestParam("itemBarcodeID") int itemBarcodeID,
                                                       @RequestParam("barcode") String barcode,
                                                       @RequestParam("barcodeType") String barcodeType) {

        ItemBarcode itemBarcode = itemBarcodeService.findByItemBarcodeId(itemBarcodeID);
        if (itemBarcode == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item barcode by id: " + itemBarcodeID);
        }

        ItemBarcodeType itemBarcodeType = itemBarcodeTypeService.findByItemBarcodeTypeName(barcodeType);
        if (itemBarcodeType == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item barcode type by name: " + barcodeType);
        }
        itemBarcode.setBarcode(barcode);
        itemBarcode.setItemBarcodeType(itemBarcodeType);

        ItemBarcode newItemBarcode = itemBarcodeService.save(itemBarcode);

        return new WebServiceResponseWrapper<ItemBarcode>(0, "", newItemBarcode);
    }
    @ResponseBody
    @RequestMapping(value="/ws/inventory/item/barcode/new")
    public WebServiceResponseWrapper createItemBarcode(@RequestParam("itemName") String itemName,
                                                       @RequestParam("barcode") String barcode,
                                                       @RequestParam("barcodeType") String barcodeType) {
        Item item = itemService.findByItemName(itemName);
        if (item == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item by name: " + itemName);
        }

        ItemBarcodeType itemBarcodeType = itemBarcodeTypeService.findByItemBarcodeTypeName(barcodeType);
        if (itemBarcodeType == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the item barcode type by name: " + barcodeType);
        }

        ItemBarcode itemBarcode = new ItemBarcode();
        itemBarcode.setItem(item);
        itemBarcode.setBarcode(barcode);
        itemBarcode.setItemBarcodeType(itemBarcodeType);

        ItemBarcode newItemBarcode = itemBarcodeService.save(itemBarcode);

        return new WebServiceResponseWrapper<ItemBarcode>(0, "", newItemBarcode);
    }
}
