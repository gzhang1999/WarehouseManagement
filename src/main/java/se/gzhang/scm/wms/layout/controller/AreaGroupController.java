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
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.AreaGroup;
import se.gzhang.scm.wms.layout.model.AreaType;
import se.gzhang.scm.wms.layout.model.VolumeType;
import se.gzhang.scm.wms.layout.service.AreaGroupService;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.layout.service.BuildingService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class AreaGroupController {


    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "AreaGroup";

    @Autowired
    AreaGroupService areaGroupService;

    @RequestMapping(value="/layout/area/group", method = RequestMethod.GET)
    public ModelAndView listAreaGroups() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("areaGroupList", areaGroupService.findAll());
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("layout/area/group");
        return modelAndView;
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/area/group/list")
    public WebServiceResponseWrapper queryAreaGroup(@RequestParam Map<String, String> parameters) {

        List<AreaGroup> areaGroupList = areaGroupService.findAreaGroup(parameters);
        return new WebServiceResponseWrapper<List<AreaGroup>>(0, "", areaGroupList);
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/area/group/query/{id}")
    public WebServiceResponseWrapper getAreaGroup(@PathVariable("id") int areaGroupID) {

        AreaGroup areaGroup = areaGroupService.findByAreaGroupId(areaGroupID);
        if (areaGroup == null) {
            return WebServiceResponseWrapper.raiseError("AreaGroupException.CannotFindAreaGroup", "Can't find the area group by id: " + areaGroupID);
        }
        return new WebServiceResponseWrapper<AreaGroup>(0, "", areaGroup);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/area/group/delete")
    public WebServiceResponseWrapper deleteAreaGroup(@RequestParam("areaGroupID") int areaGroupID) {

        AreaGroup areaGroup = areaGroupService.findByAreaGroupId(areaGroupID);
        if (areaGroup == null) {
            return WebServiceResponseWrapper.raiseError("AreaGroupException.CannotFindAreaGroup", "Can't find the area group by id: " + areaGroupID);
        }
        areaGroupService.deleteAreaGroupByAreaGroupId(areaGroupID);
        return new WebServiceResponseWrapper<AreaGroup>(0, "", areaGroup);
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/area/group/new")
    public WebServiceResponseWrapper createAreaGroup(@RequestParam("name") String name,
                                                @RequestParam("description") String description) {

       AreaGroup areaGroup = new AreaGroup();
       areaGroup.setName(name);
       areaGroup.setDescription(description);

        AreaGroup newAreaGroup = areaGroupService.save(areaGroup);

        return new WebServiceResponseWrapper<AreaGroup>(0, "", newAreaGroup);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/area/group/edit")
    public WebServiceResponseWrapper changeAreaGroup(@RequestParam("areaGroupID") int areaGroupID,
                                                @RequestParam("name") String name,
                                                @RequestParam("description") String description) {
        AreaGroup areaGroup = areaGroupService.findByAreaGroupId(areaGroupID);
        if (areaGroup == null) {
            return WebServiceResponseWrapper.raiseError("AreaGroupException.CannotFindAreaGroup", "Can't find the area group by id: " + areaGroupID);
        }
        areaGroup.setName(name);
        areaGroup.setDescription(description);
        areaGroupService.save(areaGroup);

        return new WebServiceResponseWrapper<AreaGroup>(0, "", areaGroupService.findByAreaGroupId(areaGroupID));
    }

}
