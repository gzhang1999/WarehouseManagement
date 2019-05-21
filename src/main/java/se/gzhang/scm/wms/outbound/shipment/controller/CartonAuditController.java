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
import se.gzhang.scm.wms.exception.Outbound.CartonAuditResultException;
import se.gzhang.scm.wms.exception.Outbound.CartonTypeException;
import se.gzhang.scm.wms.outbound.shipment.model.CartonAuditResult;
import se.gzhang.scm.wms.outbound.shipment.model.CartonAuditResultLine;
import se.gzhang.scm.wms.outbound.shipment.model.CartonType;
import se.gzhang.scm.wms.outbound.shipment.service.CartonAuditResultLineService;
import se.gzhang.scm.wms.outbound.shipment.service.CartonAuditResultService;
import se.gzhang.scm.wms.outbound.shipment.service.CartonTypeService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class CartonAuditController {
    @Autowired
    CartonAuditResultService cartonAuditResultService;
    @Autowired
    CartonAuditResultLineService cartonAuditResultLineService;

    private static final String APPLICATION_ID = "Outbound";
    private static final String FORM_ID = "CartonAudit";

    @RequestMapping(value="/outbound/cartonaudit", method = RequestMethod.GET)
    public ModelAndView auditCarton(@RequestParam Map<String, String> parameters) {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("outbound/cartonaudit");

        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/cartonaudit")
    public WebServiceResponseWrapper initCartonAudit(@RequestParam(name="cartonNumber") String cartonNumber) {

        CartonAuditResult cartonAuditResult = null;
        try {

            cartonAuditResult = cartonAuditResultService.initCartonAuditResult(cartonNumber);
        }
        catch(GenericException ex) {
            return WebServiceResponseWrapper.raiseError(ex);
        }

        return new WebServiceResponseWrapper<CartonAuditResult>(0, "", cartonAuditResult);
    }


}
