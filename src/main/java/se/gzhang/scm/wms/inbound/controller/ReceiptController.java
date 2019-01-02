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
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.model.CarrierServiceLevel;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.service.CarrierService;
import se.gzhang.scm.wms.common.service.CarrierServiceLevelService;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.common.service.TrailerService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.service.ReceiptService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class ReceiptController {
    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private TrailerService trailerService;


    private static final String APPLICATION_ID = "Inbound";
    private static final String FORM_ID = "Receipt";


    @RequestMapping(value="/inbound/receipt", method = RequestMethod.GET)
    public ModelAndView listReceipts() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("inbound/receipt");
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
    public WebServiceResponseWrapper getReceipt(@PathVariable("id") int receiptID) {

        Receipt receipt = receiptService.findByReceiptId(receiptID);
        if (receipt == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the receipt by id: " + receiptID);
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

}
