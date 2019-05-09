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
import se.gzhang.scm.wms.common.model.CarrierServiceLevel;
import se.gzhang.scm.wms.common.service.CarrierService;
import se.gzhang.scm.wms.common.service.CarrierServiceLevelService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class CarrierController {
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private CarrierServiceLevelService carrierServiceLevelService;

    private static final String APPLICATION_ID = "Common";
    private static final String FORM_ID = "Carrier";


    @RequestMapping(value="/common/carrier", method = RequestMethod.GET)
    public ModelAndView listCarriers() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("carrierList", carrierService.findAll());
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("common/carrier");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/list")
    public WebServiceResponseWrapper queryCarriers(@RequestParam Map<String, String> parameters) {

        List<Carrier> carrierList = carrierService.findCarriers(parameters);
        return new WebServiceResponseWrapper<List<Carrier>>(0, "", carrierList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/query/{id}")
    public WebServiceResponseWrapper getCarrier(@PathVariable("id") int carrierID) {

        Carrier carrier = carrierService.findByCarrierId(carrierID);
        if (carrier == null) {
            return WebServiceResponseWrapper.raiseError("CarrierException.CannotFindCarrier", "Can't find the carrier by id: " + carrierID);
        }
        return new WebServiceResponseWrapper<Carrier>(0, "", carrier);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/delete")
    public WebServiceResponseWrapper deleteCarrier(@RequestParam("carrierID") int carrierID) {

        Carrier carrier = carrierService.findByCarrierId(carrierID);
        if (carrier == null) {
            return WebServiceResponseWrapper.raiseError("CarrierException.CannotFindCarrier", "Can't find the carrier by id: " + carrierID);
        }
        carrierService.deleteByCarrierID(carrierID);
        return new WebServiceResponseWrapper<Carrier>(0, "", carrier);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/new")
    public WebServiceResponseWrapper createCarrier(@RequestParam("name") String name,
                                                   @RequestParam("description") String description,
                                                   @RequestParam("contactPerson") String contactPerson,
                                                   @RequestParam("contactPersonTelephone") String contactPersonTelephone) {

        Carrier carrier = new Carrier();
        carrier.setName(name);
        carrier.setDescription(description);
        carrier.setContactPerson(contactPerson);
        carrier.setContactPersonTelephone(contactPersonTelephone);

        Carrier newCarrier = carrierService.save(carrier);

        return new WebServiceResponseWrapper<Carrier>(0, "", newCarrier);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/edit")
    public WebServiceResponseWrapper changeCarrier(@RequestParam("carrierID") int carrierID,
                                                   @RequestParam("description") String description,
                                                   @RequestParam("contactPerson") String contactPerson,
                                                   @RequestParam("contactPersonTelephone") String contactPersonTelephone) {

        Carrier carrier = carrierService.findByCarrierId(carrierID);
        if (carrier == null) {
            return WebServiceResponseWrapper.raiseError("CarrierException.CannotFindCarrier", "Can't find the carrier by id: " + carrierID);
        }
        carrier.setDescription(description);
        carrier.setContactPerson(contactPerson);
        carrier.setContactPersonTelephone(contactPersonTelephone);
        carrierService.save(carrier);

        return new WebServiceResponseWrapper<Carrier>(0, "", carrierService.findByCarrierId(carrierID));
    }
    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/servicelevel/new")
    public WebServiceResponseWrapper createCarrierServicelLevel(@RequestParam("carrierID") int carrierID,
                                                   @RequestParam("carrierServiceLevelname") String carrierServiceLevelname,
                                                   @RequestParam("carrierServiceLeveldescription") String carrierServiceLeveldescription) {


        Carrier carrier = carrierService.findByCarrierId(carrierID);
        if (carrier == null) {
            return WebServiceResponseWrapper.raiseError("CarrierException.CannotFindCarrier", "Can't find the carrier by id: " + carrierID);
        }

        CarrierServiceLevel carrierServiceLevel = new CarrierServiceLevel();
        carrierServiceLevel.setName(carrierServiceLevelname);
        carrierServiceLevel.setDescription(carrierServiceLeveldescription);
        carrierServiceLevel.setCarrier(carrier);

        CarrierServiceLevel newCarrierServiceLevel = carrierServiceLevelService.save(carrierServiceLevel);

        return new WebServiceResponseWrapper<CarrierServiceLevel>(0, "", newCarrierServiceLevel);
    }


    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/servicelevel/edit")
    public WebServiceResponseWrapper changeCarrierServicelLevel(@RequestParam("carrierServiceLevelID") int carrierServiceLevelID,
                                                   @RequestParam("carrierServiceLeveldescription") String carrierServiceLeveldescription) {


        CarrierServiceLevel carrierServiceLevel = carrierServiceLevelService.findByCarrierServiceLevelId(carrierServiceLevelID);
        if (carrierServiceLevel == null) {
            return WebServiceResponseWrapper.raiseError("CarrierException.CannotFindCarrierService", "Can't find the carrier service level by id: " + carrierServiceLevelID);
        }

        // The user is only allowed to change the description
        carrierServiceLevel.setDescription(carrierServiceLeveldescription);

        CarrierServiceLevel newCarrierServiceLevel = carrierServiceLevelService.save(carrierServiceLevel);

        return new WebServiceResponseWrapper<CarrierServiceLevel>(0, "", newCarrierServiceLevel);
    }


    @ResponseBody
    @RequestMapping(value="/ws/common/carrier/servicelevel/delete")
    public WebServiceResponseWrapper deleteCarrierServicelLevel(@RequestParam("carrierServiceLevelID") int carrierServiceLevelID) {


        CarrierServiceLevel carrierServiceLevel = carrierServiceLevelService.findByCarrierServiceLevelId(carrierServiceLevelID);
        if (carrierServiceLevel == null) {
            return WebServiceResponseWrapper.raiseError("CarrierException.CannotFindCarrierService", "Can't find the carrier service level by id: " + carrierServiceLevelID);
        }

        carrierServiceLevelService.deleteByCarrierServiceLevelID(carrierServiceLevelID);
        return new WebServiceResponseWrapper<CarrierServiceLevel>(0, "", carrierServiceLevel);
    }
}
