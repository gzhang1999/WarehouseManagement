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

package se.gzhang.scm.wms.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.model.EnumWithDescription;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.service.CarrierService;
import se.gzhang.scm.wms.common.service.TrailerService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class TrailerController {
    @Autowired
    private TrailerService trailerService;
    @Autowired
    private CarrierService carrierService;

    private static final String APPLICATION_ID = "Common";
    private static final String FORM_ID = "Trailer";


    @RequestMapping(value="/common/trailer", method = RequestMethod.GET)
    public ModelAndView listTrailers() {
        ModelAndView modelAndView = new ModelAndView();

        // Test for enum
        String enumName = "se.gzhang.scm.wms.common.model.TrailerState";

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("common/trailer");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/list")
    public WebServiceResponseWrapper queryTrailers(@RequestParam Map<String, String> parameters) {

        System.out.println("Start to find trailer with:");
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            System.out.println("key: " + entry.getKey() + " / value: " + entry.getValue());
        }
        List<Trailer> trailerList = trailerService.findTrailers(parameters);
        return new WebServiceResponseWrapper<List<Trailer>>(0, "", trailerList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/query/{id}")
    public WebServiceResponseWrapper getTrailer(@PathVariable("id") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }
        return new WebServiceResponseWrapper<Trailer>(0, "", trailer);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/delete")
    public WebServiceResponseWrapper deleteTrailer(@RequestParam("trailerID") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }
        trailerService.deleteByTrailerID(trailerID);
        return new WebServiceResponseWrapper<Trailer>(0, "", trailer);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/new")
    public WebServiceResponseWrapper createTrailer(@RequestParam("trailerType") String trailerType,
                                                   @RequestParam(name = "trailerNumber", required = false) String trailerNumber,
                                                   @RequestParam(name = "driver", required = false) String driver,
                                                   @RequestParam(name = "driverTelephone", required = false) String driverTelephone,
                                                   @RequestParam(name = "licensePlate", required = false) String licensePlate,
                                                   @RequestParam(name = "carrier", required = false) Integer carrierID) {

        Trailer newTrailer = null;
        if (carrierID != null) {
            // if carrierID is passed in, make sure it is a valid carrier id
            Carrier carrier = carrierService.findByCarrierId(carrierID);
            if (carrier == null) {
                return WebServiceResponseWrapper.raiseError(10000, "Can't find the carrier by id: " + carrierID);
            }
            newTrailer = trailerService.createTrailer(trailerType,trailerNumber,driver,driverTelephone,licensePlate,carrier);
        }
        else {

            newTrailer = trailerService.createTrailer(trailerType,trailerNumber,driver,driverTelephone,licensePlate);
        }
        return new WebServiceResponseWrapper<Trailer>(0, "", newTrailer);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/{id}/edit")
    public WebServiceResponseWrapper changeTrailer(@PathVariable("id") int trailerID,
                                                   @RequestParam(name = "trailer_type", required = false) String trailerType,
                                                   @RequestParam(name = "driver", required = false) String driver,
                                                   @RequestParam(name = "driverTelephone", required = false) String driverTelephone,
                                                   @RequestParam(name = "licensePlate", required = false) String licensePlate,
                                                   @RequestParam(name = "carrier", required = false) Integer carrierID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }

        Trailer newTrailer = null;
        if (carrierID != null) {
            // if carrierID is passed in, make sure it is a valid carrier id
            Carrier carrier = carrierService.findByCarrierId(carrierID);
            if (carrier == null) {
                return WebServiceResponseWrapper.raiseError(10000, "Can't find the carrier by id: " + carrierID);
            }
            newTrailer = trailerService.changeTrailer(trailer, trailerType,driver,driverTelephone,licensePlate,carrier);
        }
        else {

            newTrailer = trailerService.changeTrailer(trailer, trailerType,driver,driverTelephone,licensePlate);
        }
        return new WebServiceResponseWrapper<Trailer>(0, "", newTrailer);
    }


    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/{id}/checkin")
    public WebServiceResponseWrapper checkInTrailer(@PathVariable("id") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }

        return new WebServiceResponseWrapper<Trailer>(0, "", trailerService.checkInTrailer(trailer));
    }

}
