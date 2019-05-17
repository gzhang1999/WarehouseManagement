/**
 * Copyright 2019
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

package se.gzhang.scm.wms.outbound.shipment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.exception.Outbound.ShipmentException;
import se.gzhang.scm.wms.outbound.order.model.SalesOrder;
import se.gzhang.scm.wms.outbound.shipment.model.Shipment;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class ShipmentController {
    @Autowired
    ShipmentService shipmentService;

    private static final String APPLICATION_ID = "Outbound";
    private static final String FORM_ID = "Shipment";



    @RequestMapping(value="/outbound/shipment", method = RequestMethod.GET)
    public ModelAndView listShipment(@RequestParam Map<String, String> parameters) {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("outbound/shipment");

        if (parameters.size() > 0) {
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                modelAndView.addObject("url_query_" + entry.getKey() , entry.getValue());
            }
            List<Shipment> shipmentList = shipmentService.findShipments(parameters);

            modelAndView.addObject("shipmentList",shipmentList);

        }
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/shipment/list")
    public WebServiceResponseWrapper queryShipments(@RequestParam Map<String, String> parameters) {

        List<Shipment> shipmentList = shipmentService.findShipments(parameters);
        return new WebServiceResponseWrapper<List<Shipment>>(0, "", shipmentList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/shipment/query/{id}")
    public WebServiceResponseWrapper getShipment(@PathVariable("id") int shipmentID) {

        Shipment shipment = shipmentService.findByShipmentId(shipmentID);
        if (shipment == null) {
            return WebServiceResponseWrapper.raiseError(ShipmentException.NO_SUCH_SHIPMENT);
        }
        return new WebServiceResponseWrapper<Shipment>(0, "", shipment);
    }


    @ResponseBody
    @RequestMapping(value="/ws/outbound/shipment/{id}/allocate")
    public WebServiceResponseWrapper allocateShipment(@PathVariable("id") int shipmentID) {

        Shipment shipment = shipmentService.findByShipmentId(shipmentID);
        if (shipment == null) {
            return WebServiceResponseWrapper.raiseError(ShipmentException.NO_SUCH_SHIPMENT);
        }
        shipmentService.allocateShipment(shipment);
        return new WebServiceResponseWrapper<Shipment>(0, "", shipment);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/shipment/{id}/cancel")
    public WebServiceResponseWrapper cancelShipment(@PathVariable("id") int shipmentID) {

        Shipment shipment = shipmentService.findByShipmentId(shipmentID);
        if (shipment == null) {
            return WebServiceResponseWrapper.raiseError(ShipmentException.NO_SUCH_SHIPMENT);
        }
        try {
            shipmentService.cancelShipment(shipment);
            return new WebServiceResponseWrapper<Shipment>(0, "", shipment);
        }
        catch (GenericException ex) {
            return WebServiceResponseWrapper.raiseError(ex.getCode(), ex.getMessage());
        }
    }
}
