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
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.service.CarrierService;
import se.gzhang.scm.wms.common.service.CarrierServiceLevelService;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    private static final String APPLICATION_ID = "Common";
    private static final String FORM_ID = "Supplier";


    @RequestMapping(value="/common/supplier", method = RequestMethod.GET)
    public ModelAndView listSuppliers() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("common/supplier");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/supplier/list")
    public WebServiceResponseWrapper querySuppliers(@RequestParam Map<String, String> parameters) {

        List<Supplier> supplierList = supplierService.findSuppliers(parameters);
        return new WebServiceResponseWrapper<List<Supplier>>(0, "", supplierList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/supplier/query/{id}")
    public WebServiceResponseWrapper getSupplier(@PathVariable("id") int supplierID) {

        Supplier supplier = supplierService.findBySupplierId(supplierID);
        if (supplier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the supplier by id: " + supplierID);
        }
        return new WebServiceResponseWrapper<Supplier>(0, "", supplier);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/supplier/delete")
    public WebServiceResponseWrapper deleteSupplier(@RequestParam("supplierID") int supplierID) {

        Supplier supplier = supplierService.findBySupplierId(supplierID);
        if (supplier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the supplier by id: " + supplierID);
        }
        supplierService.deleteBySupplierID(supplierID);
        return new WebServiceResponseWrapper<Supplier>(0, "", supplier);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/supplier/new")
    public WebServiceResponseWrapper createSupplier(@RequestParam("name") String name,
                                                   @RequestParam("description") String description,
                                                   @RequestParam("contactPerson") String contactPerson,
                                                   @RequestParam("contactPersonTelephone") String contactPersonTelephone) {

        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setDescription(description);
        supplier.setContactPerson(contactPerson);
        supplier.setContactPersonTelephone(contactPersonTelephone);

        Supplier newSupplier = supplierService.save(supplier);

        return new WebServiceResponseWrapper<Supplier>(0, "", newSupplier);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/supplier/edit")
    public WebServiceResponseWrapper changeSupplier(@RequestParam("supplierID") int supplierID,
                                                   @RequestParam("description") String description,
                                                   @RequestParam("contactPerson") String contactPerson,
                                                   @RequestParam("contactPersonTelephone") String contactPersonTelephone) {

        Supplier supplier = supplierService.findBySupplierId(supplierID);
        if (supplier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the supplier by id: " + supplierID);
        }
        supplier.setDescription(description);
        supplier.setContactPerson(contactPerson);
        supplier.setContactPersonTelephone(contactPersonTelephone);
        Supplier newSupplier = supplierService.save(supplier);

        return new WebServiceResponseWrapper<Supplier>(0, "", newSupplier);
    }
}
