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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.layout.model.AreaGroup;
import se.gzhang.scm.wms.layout.model.LocationGroup;
import se.gzhang.scm.wms.layout.service.AreaGroupService;
import se.gzhang.scm.wms.layout.service.LocationGroupService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class LocationGroupController {


    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "LocationGroup";

    @Autowired
    LocationGroupService locationGroupService;

    @RequestMapping(value="/layout/location/group", method = RequestMethod.GET)
    public ModelAndView listLocationGroups() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("locationGroupList", locationGroupService.findAll());
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("layout/location/group");
        return modelAndView;
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/location/group/list")
    public WebServiceResponseWrapper queryLocationGroup(@RequestParam Map<String, String> parameters) {

        List<LocationGroup> locationGroupList = locationGroupService.findLocationGroup(parameters);
        return new WebServiceResponseWrapper<List<LocationGroup>>(0, "", locationGroupList);
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/location/group/query/{id}")
    public WebServiceResponseWrapper getLocationGroup(@PathVariable("id") int locationGroupID) {

        LocationGroup locationGroup = locationGroupService.findByLocationGroupId(locationGroupID);
        if (locationGroup == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the location group by id: " + locationGroupID);
        }
        return new WebServiceResponseWrapper<LocationGroup>(0, "", locationGroup);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/group/delete")
    public WebServiceResponseWrapper deleteLocationGroup(@RequestParam("locationGroupID") int locationGroupID) {

        LocationGroup locationGroup = locationGroupService.findByLocationGroupId(locationGroupID);
        if (locationGroup == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the location group by id: " + locationGroupID);
        }
        locationGroupService.deleteLocationGroupByLocationGroupId(locationGroupID);
        return new WebServiceResponseWrapper<LocationGroup>(0, "", locationGroup);
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/location/group/new")
    public WebServiceResponseWrapper createLocationGroup(@RequestParam("name") String name,
                                                @RequestParam("description") String description) {

       LocationGroup locationGroup = new LocationGroup();
       locationGroup.setName(name);
       locationGroup.setDescription(description);

       LocationGroup newLocationGroup = locationGroupService.save(locationGroup);

        return new WebServiceResponseWrapper<LocationGroup>(0, "", newLocationGroup);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/group/edit")
    public WebServiceResponseWrapper changeLocationGroup(@RequestParam("locationGroupID") int locationGroupID,
                                                @RequestParam("name") String name,
                                                @RequestParam("description") String description) {
        LocationGroup locationGroup = locationGroupService.findByLocationGroupId(locationGroupID);
        if (locationGroup == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the location group by id: " + locationGroupID);
        }
        locationGroup.setName(name);
        locationGroup.setDescription(description);
        locationGroupService.save(locationGroup);

        return new WebServiceResponseWrapper<LocationGroup>(0, "", locationGroupService.findByLocationGroupId(locationGroupID));
    }

}
