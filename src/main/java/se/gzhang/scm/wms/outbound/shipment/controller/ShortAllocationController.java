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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import se.gzhang.scm.wms.exception.Outbound.ShortAllocationException;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.model.ShortAllocation;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentLineService;
import se.gzhang.scm.wms.outbound.shipment.service.ShortAllocationService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

@Controller
public class ShortAllocationController {
    @Autowired
    ShortAllocationService shortAllocationService;


    @ResponseBody
    @RequestMapping(value="/ws/outbound/shortallocation/{id}/cancel")
    public WebServiceResponseWrapper cancelShortAllocation(@PathVariable("id") int shortAllocationID) {

        ShortAllocation shortAllocation = shortAllocationService.findByShortAllocationId(shortAllocationID);
        if (shortAllocation == null) {
            return WebServiceResponseWrapper.raiseError(ShortAllocationException.NO_SUCH_SHORT_ALLOCATION);
        }
        shortAllocationService.cancelShortAllocation(shortAllocation);
        return new WebServiceResponseWrapper<ShortAllocation>(0, "", shortAllocation);
    }
}
