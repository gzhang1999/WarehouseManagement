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

package se.gzhang.scm.wms.layout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.layout.service.BuildingService;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class LocationController {

    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "Location";

    @Autowired
    LocationService locationService;


    @RequestMapping(value="/layout/location", method = RequestMethod.GET)
    public ModelAndView listLocations() {
        ModelAndView modelAndView = new ModelAndView();

        System.out.println("Find all locations: " + locationService.findAll().size());
        modelAndView.addObject("locationList", locationService.findAll());
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("layout/location");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/list")
    public WebServiceResponseWrapper queryLocation(@RequestParam Map<String, String> parameters) {

        List<Location> locationList = locationService.findLocation(parameters);
        return new WebServiceResponseWrapper<List<Location>>(0, "", locationList);
    }

}
