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

package se.gzhang.scm.wms.outbound.order.controller;

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
import se.gzhang.scm.wms.outbound.order.model.SalesOrder;
import se.gzhang.scm.wms.outbound.order.service.SalesOrderService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class SalesOrderController {
    @Autowired
    SalesOrderService salesOrderService;

    private static final String APPLICATION_ID = "Outbound";
    private static final String FORM_ID = "SalesOrder";


    @RequestMapping(value="/outbound/salesorder", method = RequestMethod.GET)
    public ModelAndView listSalesOrder(@RequestParam Map<String, String> parameters) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("outbound/salesorder");

        if (parameters.size() > 0) {
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                modelAndView.addObject("url_query_" + entry.getKey() , entry.getValue());
            }
            List<SalesOrder> salesOrderList = salesOrderService.findSalesOrders(parameters);

            modelAndView.addObject("salesOrderList",salesOrderList);

        }
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/salesorder/list")
    public WebServiceResponseWrapper querySalesOrders(@RequestParam Map<String, String> parameters) {

        List<SalesOrder> salesOrderList = salesOrderService.findSalesOrders(parameters);
        return new WebServiceResponseWrapper<List<SalesOrder>>(0, "", salesOrderList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/salesorder/query/{id}")
    public WebServiceResponseWrapper getSalesOrder(@PathVariable("id") int salesOrderID) {

        SalesOrder salesOrder = salesOrderService.findBySalesOrderId(salesOrderID);
        if (salesOrder == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the sales order by id: " + salesOrderID);
        }
        salesOrderService.loadShipmentInformation(salesOrder);

        return new WebServiceResponseWrapper<SalesOrder>(0, "", salesOrder);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/salesorder/{id}/delete")
    public WebServiceResponseWrapper deleteSalesOrder(@PathVariable("id") int salesOrderID) {

        SalesOrder salesOrder = salesOrderService.findBySalesOrderId(salesOrderID);
        if (salesOrder == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the sales order by id: " + salesOrderID);
        }

        salesOrderService.deleteBySalesOrderID(salesOrderID);
        return new WebServiceResponseWrapper<SalesOrder>(0, "", salesOrder);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/salesorder/{id}/edit")
    public WebServiceResponseWrapper changeSalesOrder(@PathVariable("id") int salesOrderID,
                                                      @RequestParam("number") String number,
                                                      @RequestParam("externalID") String externalID,
                                                      @RequestParam("shipToCustomerID") int shipToCustomerID,
                                                      @RequestParam("billToCustomerID") int billToCustomerID) {

        SalesOrder salesOrder = salesOrderService.findBySalesOrderId(salesOrderID);
        if (salesOrder == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the sales order by id: " + salesOrderID);
        }

        try {
            SalesOrder newSalesOrder = salesOrderService.changeSalesOrder(salesOrderID, number, externalID, shipToCustomerID, billToCustomerID);
            return new WebServiceResponseWrapper<SalesOrder>(0, "", newSalesOrder);
        }
        catch (GenericException ex) {
            return WebServiceResponseWrapper.raiseError(ex.getCode(), ex.getMessage());

        }
    }
    @ResponseBody
    @RequestMapping(value="/ws/outbound/salesorder/{id}/reserve")
    public WebServiceResponseWrapper reserveInventoryForSalesOrder(@PathVariable("id") int salesOrderID) {

        SalesOrder salesOrder = salesOrderService.findBySalesOrderId(salesOrderID);
        if (salesOrder == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the sales order by id: " + salesOrderID);
        }

        salesOrderService.allocateInventory(salesOrder);
        return new WebServiceResponseWrapper<SalesOrder>(0, "", salesOrder);
    }

}
