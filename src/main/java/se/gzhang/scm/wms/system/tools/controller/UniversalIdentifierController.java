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

package se.gzhang.scm.wms.system.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.framework.controls.model.UniversalIdentifier;
import se.gzhang.scm.wms.framework.controls.service.UniversalIdentifierService;
import se.gzhang.scm.wms.system.tools.model.FileUploadOption;
import se.gzhang.scm.wms.system.tools.model.FileUploadProcess;
import se.gzhang.scm.wms.system.tools.service.FileUploadOptionService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UniversalIdentifierController {

    private static final String APPLICATION_ID = "SystemTools";
    private static final String FORM_ID = "UniversalIdentifier";
    @Autowired
    UniversalIdentifierService universalIdentifierService;


    @RequestMapping(value="/systemtool/universalidentifier", method = RequestMethod.GET)
    public ModelAndView listUniversalidentifier() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("systemtool/universalidentifier");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/systemtool/universalidentifier/{variable}/nextnumber")
    public WebServiceResponseWrapper getNextUniversalidentifier(@PathVariable("variable") String variable) {

        return new WebServiceResponseWrapper<String>(0, "", universalIdentifierService.getNextNumber(variable));
    }


    @ResponseBody
    @RequestMapping(value="/ws/systemtool/universalidentifier/list")
    public WebServiceResponseWrapper queryUniversalidentifier(@RequestParam Map<String, String> parameters) {

        List<UniversalIdentifier> universalIdentifierList = universalIdentifierService.findUniversalIdentifiers(parameters);
        return new WebServiceResponseWrapper<List<UniversalIdentifier>>(0, "", universalIdentifierList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/systemtool/universalidentifier/{id}")
    public WebServiceResponseWrapper getUniversalidentifier(@PathVariable("id") Integer id) {

        UniversalIdentifier universalidentifier = universalIdentifierService.findById(id);
        if (universalidentifier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the universal identify by id: " + universalidentifier);
        }

        return new WebServiceResponseWrapper<UniversalIdentifier>(0, "", universalidentifier);
    }

    @ResponseBody
    @RequestMapping(value="/ws/systemtool/universalidentifier/new")
    public WebServiceResponseWrapper createUniversalidentifier(
                                     @RequestParam("variable") String variable,
                                     @RequestParam("rollover") Boolean rollover,
                                     @RequestParam("prefix") String prefix,
                                     @RequestParam("postfix") String postfix,
                                     @RequestParam("length") Integer length,
                                     @RequestParam(name="currentNumber", required = false, defaultValue = "-1") Integer currentNumber) {

        return new WebServiceResponseWrapper<UniversalIdentifier>(0, "",
                universalIdentifierService.createUniversalIdentifier(variable, rollover,prefix,postfix,length, currentNumber));


    }
    @ResponseBody
    @RequestMapping(value="/ws/systemtool/universalidentifier/{id}/edit")
    public WebServiceResponseWrapper getUniversalidentifier(@PathVariable("id") Integer id,
                                                            @RequestParam("variable") String variable,
                                                            @RequestParam("rollover") Boolean rollover,
                                                            @RequestParam("prefix") String prefix,
                                                            @RequestParam("postfix") String postfix,
                                                            @RequestParam("length") Integer length,
                                                            @RequestParam("currentNumber") Integer currentNumber) {

        UniversalIdentifier universalidentifier = universalIdentifierService.findById(id);
        if (universalidentifier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the universal identify by id: " + universalidentifier);
        }

        return new WebServiceResponseWrapper<UniversalIdentifier>(0, "",
                universalIdentifierService.editUniversalIdentifier(id, variable, rollover,prefix,postfix,length, currentNumber));
    }

    @ResponseBody
    @RequestMapping(value="/ws/systemtool/universalidentifier/{id}/delete", method = RequestMethod.DELETE)
    public WebServiceResponseWrapper deleteUniversalidentifier(@PathVariable("id") Integer id) {

        UniversalIdentifier universalidentifier = universalIdentifierService.findById(id);
        if (universalidentifier == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the universal identify by id: " + universalidentifier);
        }

        return new WebServiceResponseWrapper<UniversalIdentifier>(0, "",
                universalIdentifierService.deleteUniversalIdentifier(id));
    }
}
