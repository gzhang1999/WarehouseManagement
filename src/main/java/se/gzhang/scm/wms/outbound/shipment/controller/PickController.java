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
import se.gzhang.scm.wms.outbound.shipment.model.Pick;
import se.gzhang.scm.wms.outbound.shipment.model.Shipment;
import se.gzhang.scm.wms.outbound.shipment.service.PickService;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class PickController {
    @Autowired
    PickService pickService;

    private static final String APPLICATION_ID = "Outbound";
    private static final String FORM_ID = "Pick";



    @RequestMapping(value="/outbound/pick", method = RequestMethod.GET)
    public ModelAndView listPick(@RequestParam Map<String, String> parameters) {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("outbound/pick");

        if (parameters.size() > 0) {
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                modelAndView.addObject("url_query_" + entry.getKey() , entry.getValue());
            }
            List<Pick> pickList = pickService.findPicks(parameters);

            modelAndView.addObject("pickList",pickList);

        }
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/pick/list")
    public WebServiceResponseWrapper queryPicks(@RequestParam Map<String, String> parameters) {

        List<Pick> pickList = pickService.findPicks(parameters);
        return new WebServiceResponseWrapper<List<Pick>>(0, "", pickList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/pick/{id}")
    public WebServiceResponseWrapper getPick(@PathVariable("id") int pickID) {

        Pick pick = pickService.findByPickId(pickID);
        if (pick == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the pick by id: " + pickID);
        }
        return new WebServiceResponseWrapper<Pick>(0, "", pick);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/pick/{id}/cancel", method = RequestMethod.DELETE)
    public WebServiceResponseWrapper cancelPick(@PathVariable("id") int pickID) {

        Pick pick = pickService.findByPickId(pickID);
        if (pick == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the pick by id: " + pickID);
        }
        pickService.cancelPick(pick);
        return new WebServiceResponseWrapper<Pick>(0, "", pick);
    }
    @ResponseBody
    @RequestMapping(value="/ws/outbound/pick/{id}/confirm", method = RequestMethod.POST)
    public WebServiceResponseWrapper confirmPick(@PathVariable("id") int pickID,
                                                 @RequestParam("confirmedQuantity") int confirmedQuantity) {

        Pick pick = pickService.findByPickId(pickID);
        if (pick == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the pick by id: " + pickID);
        }
        pickService.confirmPick(pick, confirmedQuantity);
        return new WebServiceResponseWrapper<Pick>(0, "", pick);
    }
}
