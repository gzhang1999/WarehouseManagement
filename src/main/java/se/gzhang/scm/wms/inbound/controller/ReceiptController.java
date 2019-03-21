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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.common.service.TrailerService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inbound.service.ReceiptLineService;
import se.gzhang.scm.wms.inbound.service.ReceiptService;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class ReceiptController {
    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private ReceiptLineService receiptLineService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private TrailerService trailerService;




    private static final String APPLICATION_ID = "Inbound";
    private static final String FORM_ID = "Receipt";


    @RequestMapping(value="/inbound/receipt", method = RequestMethod.GET)
    public ModelAndView listReceipts(@RequestParam Map<String, String> parameters) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("inbound/receipt");

        if (parameters.size() > 0) {
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                modelAndView.addObject("url_query_" + entry.getKey() , entry.getValue());
            }
            List<Receipt> receiptList = receiptService.findReceipts(parameters);

            modelAndView.addObject("receiptList",receiptList);

        }
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/list")
    public WebServiceResponseWrapper queryReceipts(@RequestParam Map<String, String> parameters) {

        List<Receipt> receiptList = receiptService.findReceipts(parameters);
        return new WebServiceResponseWrapper<List<Receipt>>(0, "", receiptList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/query/{id}")
    public WebServiceResponseWrapper getReceipt(@PathVariable("id") int receiptID,
                                                @RequestParam(value = "showInventory", required = false, defaultValue =  "false") boolean showInventory) {

        Receipt receipt = receiptService.findByReceiptId(receiptID);
        if (receipt == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the receipt by id: " + receiptID);
        }

        // add received inventory to the customized field
        if (showInventory) {
            // Setup the received inventory for each line
            receiptService.loadReceivedInventoryByReceipt(receipt);
            int totalInventoryCount = 0;
            for(ReceiptLine receiptLine : receipt.getReceiptLineList()) {
                int inventoryCount = receiptLine.getReceivedInventory() == null ? 0 : receiptLine.getReceivedInventory().size();
                totalInventoryCount += inventoryCount;

            }
        }
        return new WebServiceResponseWrapper<Receipt>(0, "", receipt);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/{id}/delete")
    public WebServiceResponseWrapper deleteReceipt(@PathVariable("id") int receiptID) {

        Receipt receipt = receiptService.findByReceiptId(receiptID);
        if (receipt == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the receipt by id: " + receiptID);
        }
        receiptService.deleteByReceiptID(receiptID);
        return new WebServiceResponseWrapper<Receipt>(0, "", receipt);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/new")
    public WebServiceResponseWrapper createReceipt(@RequestParam("externalID") String externalID,
                                                   @RequestParam("number") String number,
                                                   @RequestParam("purchaseOrderNumber") String purchaseOrderNumber,
                                                   @RequestParam("supplierID") int supplierID,
                                                   @RequestParam("trailerID") int trailerID) {

        Supplier supplier = supplierService.findBySupplierId(supplierID);
        if (supplier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the supplier by id: " + supplierID);
        }
        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }
        Receipt receipt = receiptService.createReceipt(externalID, number, purchaseOrderNumber, supplier, trailer);

        return new WebServiceResponseWrapper<Receipt>(0, "", receipt);
    }


    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/{id}/edit")
    public WebServiceResponseWrapper changeReceipt(@PathVariable("id") int receiptID,
                                                   @RequestParam("externalID") String externalID,
                                                   @RequestParam("purchaseOrderNumber") String purchaseOrderNumber,
                                                   @RequestParam("supplierID") int supplierID) {

        Receipt receipt = receiptService.findByReceiptId(receiptID);
        if (receipt == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the receipt by id: " + receiptID);
        }

        Supplier supplier = supplierService.findBySupplierId(supplierID);
        if (supplier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the supplier by id: " + supplierID);
        }

        try {
            Receipt newReceipt = receiptService.changeReceipt(receipt, externalID, purchaseOrderNumber, supplier);
            return new WebServiceResponseWrapper<Receipt>(0, "", newReceipt);
        }
        catch (GenericException ex) {
            return WebServiceResponseWrapper.raiseError(ex.getCode(), ex.getMessage());

        }
    }


    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/line/query/{id}")
    public WebServiceResponseWrapper getReceiptLine(@PathVariable("id") int receiptLineID) {

        ReceiptLine receiptLine = receiptLineService.findByReceiptLineId(receiptLineID);
        if (receiptLine == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the receipt line by id: " + receiptLineID);
        }
        return new WebServiceResponseWrapper<ReceiptLine>(0, "", receiptLine);
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/{receiptID}/lines/{receiptLineID}/receiving")
    public WebServiceResponseWrapper receiving(@PathVariable("receiptID") int receiptID,
                                               @PathVariable("receiptLineID") int receiptLineID,
                                               @RequestParam("location") String location,
                                               @RequestParam("quantity") int quantity,
                                               @RequestParam("itemFootprint") int itemFootprintID,
                                               @RequestParam("inventoryStatus") String inventoryStatus,
                                               @RequestParam(value = "lpn", required = false) String lpn,
                                               @RequestParam("putawayWork") boolean generatePutawayWork) {

        ReceiptLine receiptLine = receiptLineService.findByReceiptLineId(receiptLineID);
        if (receiptLine == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the receipt line by id: " + receiptLineID);
        }

        Inventory inventory = receiptService.receiving(receiptLine,location,quantity,itemFootprintID,
                                                       inventoryStatus,lpn,generatePutawayWork);

        return new WebServiceResponseWrapper<Inventory>(0, "", inventory);
    }


    @ResponseBody
    @RequestMapping(value="/ws/inbound/receipt/lines/{receiptLineID}/inventory")
    public WebServiceResponseWrapper getReceivedInventoryByReceiptLine(@PathVariable("receiptLineID") int receiptLineID) {

        ReceiptLine receiptLine = receiptLineService.findByReceiptLineId(receiptLineID);
        if (receiptLine == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the receipt line by id: " + receiptLineID);
        }

        List<Inventory> receivedInventory = receiptService.getReceivedInventoryByReceiptLine(receiptLineID);

        return new WebServiceResponseWrapper<List<Inventory>>(0, "", receivedInventory);
    }

}
