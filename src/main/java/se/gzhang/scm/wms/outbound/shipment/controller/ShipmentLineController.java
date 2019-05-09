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
import se.gzhang.scm.wms.outbound.shipment.model.Shipment;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentLineService;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class ShipmentLineController {
    @Autowired
    ShipmentLineService shipmentLineService;


    @ResponseBody
    @RequestMapping(value="/ws/outbound/shipment/line/{id}/cancel")
    public WebServiceResponseWrapper cancelShipmentLine(@PathVariable("id") int shipmentLineID) {

        ShipmentLine shipmentLine = shipmentLineService.findByShipmentLineId(shipmentLineID);
        if (shipmentLine == null) {
            return WebServiceResponseWrapper.raiseError(ShipmentException.NO_SUCH_SHIPMENT_LINE_EXCEPTION);
        }
        try {
            shipmentLineService.cancelShipmentLine(shipmentLine);
            return new WebServiceResponseWrapper<ShipmentLine>(0, "", shipmentLine);
        }

        catch (GenericException ex) {
            return WebServiceResponseWrapper.raiseError(ex.getCode(), ex.getMessage());
        }
    }
}
