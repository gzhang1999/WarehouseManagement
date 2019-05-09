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
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.gzhang.scm.wms.framework.controls.model.DropdownList;
import se.gzhang.scm.wms.framework.controls.model.DropdownOption;
import se.gzhang.scm.wms.framework.controls.model.LookupTextbox;
import se.gzhang.scm.wms.framework.controls.service.DropdownListService;
import se.gzhang.scm.wms.framework.controls.service.LookupTextboxService;
import se.gzhang.scm.wms.framework.controls.service.UniversalIdentifierService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ControlsController {
    @Autowired
    DropdownListService dropdownListService;
    @Autowired
    LookupTextboxService lookupTextboxService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    UniversalIdentifierService universalIdentifierService;

    @ResponseBody
    @RequestMapping(value="/ws/control/dropdown/{variable}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getDropdownList(@PathVariable("variable") String variable,
                                                     @RequestParam Map<String, String> parameters) {

        parameters = processDropdownListCache(parameters);

        DropdownList dropdownList = dropdownListService.findByVariable(variable, parameters);

        if(dropdownList == null) {
            return WebServiceResponseWrapper.raiseError("DropDownListException.CannotFindDropdownList", "Cannot find drop down list content for variable : " + variable);
        }
        else {
            return new WebServiceResponseWrapper<DropdownList>(0, "", dropdownList);

        }

    }

    @ResponseBody
    @RequestMapping(value="/ws/control/autocomplete/{variable}", method = RequestMethod.GET)
    // Auto complete will be treated in the same way as dropdown list but it will only return a list of
    // String
    public WebServiceResponseWrapper getAutoCompleteList(@PathVariable("variable") String variable,
                                                         @RequestParam Map<String, String> parameters) {

        parameters = processDropdownListCache(parameters);

        DropdownList dropdownList = dropdownListService.findByVariable(variable, parameters);

        List<String> autoCompleteList = new ArrayList<>();
        if (dropdownList != null &&
                dropdownList.getDropdownOptions() != null &&
                dropdownList.getDropdownOptions().size() > 0) {
            for (DropdownOption dropdownOption : dropdownList.getDropdownOptions()) {
                autoCompleteList.add(dropdownOption.getValue());
            }
        }

        return new WebServiceResponseWrapper<List<String>>(0, "", autoCompleteList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/control/lookup/{variable}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getLookup(@PathVariable("variable") String variable,
                                               @RequestParam(name = "cache", required = false,defaultValue = "true") Boolean readFromCache) {
        if (readFromCache == false) {
            // Clear the cache of the dropdown list
            cacheManager.getCache("lookupTextbox").evict(variable);
        }

        LookupTextbox lookupTextbox = lookupTextboxService.findByVariable(variable);

        return new WebServiceResponseWrapper<LookupTextbox>(0, "", lookupTextbox);
    }

    // Check if the user explicitly specify that we will need to refresh the cache and
    // load the content again(parameters contains a parameter cache = false)
    private Map<String, String> processDropdownListCache(Map<String, String> parameters) {

        boolean clearCache = false;
        if (parameters.containsKey("cache")) {
            clearCache = (parameters.get("cache").toString().equalsIgnoreCase("false"));
            // Remove it from the map so that the map only contains the business
            // related parameters.
            parameters.remove("cache");
        }

        // cache, key will be variable#Parameters-1=XXXX&Parameters-2=YYYY
        if (clearCache == true) {
            // Clear the cache of the dropdown list
            dropdownListService.evictCache();
        }
        return parameters;
    }

    @ResponseBody
    @RequestMapping(value="/ws/control/nextnumber/{variable}", method = RequestMethod.GET)
    public WebServiceResponseWrapper getNextNumber(@PathVariable("variable") String variable) {

        return new WebServiceResponseWrapper<String>(0, "", universalIdentifierService.getNextNumber(variable));
    }
}
