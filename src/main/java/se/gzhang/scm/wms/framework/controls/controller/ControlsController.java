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

package se.gzhang.scm.wms.framework.controls.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.gzhang.scm.wms.framework.controls.model.DropdownList;
import se.gzhang.scm.wms.framework.controls.service.DropdownListService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

@Controller
public class ControlsController {
    @Autowired
    DropdownListService dropdownListService;

    @ResponseBody
    @RequestMapping(value="/ws/control/dropdown/{variable}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getDropdownList(@PathVariable("variable") String variable) {
        DropdownList dropdownList = dropdownListService.findByVariable(variable);

        if(dropdownList == null) {
            return WebServiceResponseWrapper.raiseError(10002, "Cannot find drop down list content for variable : " + variable);
        }
        else {
            return new WebServiceResponseWrapper<DropdownList>(0, "", dropdownList);

        }

    }
}
