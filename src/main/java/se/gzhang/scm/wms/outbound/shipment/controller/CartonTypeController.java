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
import se.gzhang.scm.wms.exception.Outbound.CartonTypeException;
import se.gzhang.scm.wms.exception.Outbound.PickException;
import se.gzhang.scm.wms.outbound.shipment.model.CartonType;
import se.gzhang.scm.wms.outbound.shipment.model.Pick;
import se.gzhang.scm.wms.outbound.shipment.service.CartonTypeService;
import se.gzhang.scm.wms.outbound.shipment.service.PickService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class CartonTypeController {
    @Autowired
    CartonTypeService cartonTypeService;

    private static final String APPLICATION_ID = "Outbound";
    private static final String FORM_ID = "CartonType";



    @RequestMapping(value="/outbound/cartontype", method = RequestMethod.GET)
    public ModelAndView listCartonTypes(@RequestParam Map<String, String> parameters) {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("outbound/cartontype");

        if (parameters.size() > 0) {
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                modelAndView.addObject("url_query_" + entry.getKey() , entry.getValue());
            }
            List<CartonType> cartonTypeList = cartonTypeService.findCartonTypes(parameters);

            modelAndView.addObject("cartonTypeList",cartonTypeList);

        }
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/cartontype/list")
    public WebServiceResponseWrapper queryCartonTypes(@RequestParam Map<String, String> parameters) {

        // escapte the checkbox / enabled
        if (parameters.containsKey("enabled")) {
            if (parameters.get("enabled").equals("on")) {
                parameters.put("enabled", "true");
            }
            else {
                parameters.put("enabled", "false");
            }
        }
        List<CartonType> cartonTypeList = cartonTypeService.findCartonTypes(parameters);
        return new WebServiceResponseWrapper<List<CartonType>>(0, "", cartonTypeList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/outbound/cartontype/{id}/enable")
    public WebServiceResponseWrapper enableCartonTypes(@PathVariable("id") int cartonTypeID) {

        CartonType cartonType = cartonTypeService.findByCartonTypeId(cartonTypeID);
        if(cartonType == null) {
            return WebServiceResponseWrapper.raiseError(CartonTypeException.NO_SUCH_CARTON_TYPE);
        }
        return new WebServiceResponseWrapper<CartonType>(0, "", cartonTypeService.setCartonTypeEnable(cartonType, true));
    }
    @ResponseBody
    @RequestMapping(value="/ws/outbound/cartontype/{id}/disable")
    public WebServiceResponseWrapper disableCartonTypes(@PathVariable("id") int cartonTypeID) {

        CartonType cartonType = cartonTypeService.findByCartonTypeId(cartonTypeID);
        if(cartonType == null) {
            return WebServiceResponseWrapper.raiseError(CartonTypeException.NO_SUCH_CARTON_TYPE);
        }
        return new WebServiceResponseWrapper<CartonType>(0, "", cartonTypeService.setCartonTypeEnable(cartonType, false));
    }
}
