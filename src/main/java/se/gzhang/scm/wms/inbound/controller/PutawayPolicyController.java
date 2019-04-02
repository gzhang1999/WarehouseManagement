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

package se.gzhang.scm.wms.inbound.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.common.service.TrailerService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inbound.model.PutawayPolicy;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inbound.service.PutawayPolicyService;
import se.gzhang.scm.wms.inbound.service.ReceiptLineService;
import se.gzhang.scm.wms.inbound.service.ReceiptService;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class PutawayPolicyController {
    @Autowired
    private PutawayPolicyService putawayPolicyService;


    private static final String APPLICATION_ID = "Inbound";
    private static final String FORM_ID = "PutawayPolicy";


    @RequestMapping(value="/inbound/putaway/policy", method = RequestMethod.GET)
    public ModelAndView listPutawayPolicy(@RequestParam Map<String, String> parameters) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);
        modelAndView.setViewName("inbound/putaway/policy");
        if (parameters.size() > 0) {
            List<PutawayPolicy> putawayPolicyList = putawayPolicyService.findPutawayPolicys(parameters);

            modelAndView.addObject("putawayPolicyList", putawayPolicyList);


        }

        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/inbound/putaway/policy/list")
    public WebServiceResponseWrapper queryPutawayList(@RequestParam Map<String, String> parameters) {

        List<PutawayPolicy> putawayPolicyList = putawayPolicyService.findPutawayPolicys(parameters);
        return new WebServiceResponseWrapper<List<PutawayPolicy>>(0, "", putawayPolicyList);
    }

}
