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
import se.gzhang.scm.wms.layout.model.AreaType;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.VolumeType;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.layout.service.BuildingService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.*;

@Controller
public class AreaController {


    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "Area";

    @Autowired
    AreaService areaService;

    @Autowired
    BuildingService buildingService;

    @RequestMapping(value="/layout/area", method = RequestMethod.GET)
    public ModelAndView listAreas() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("areaList", areaService.findAll());
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("layout/area");
        return modelAndView;
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/area/list")
    public WebServiceResponseWrapper queryArea(@RequestParam Map<String, String> parameters) {

        List<Area> areaList = areaService.findArea(parameters);
        return new WebServiceResponseWrapper<List<Area>>(0, "", areaList);
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/area/query/{id}")
    public WebServiceResponseWrapper getArea(@PathVariable("id") int areaID) {

        Area area = areaService.findByAreaId(areaID);
        if (area == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the area by id: " + areaID);
        }
        return new WebServiceResponseWrapper<Area>(0, "", area);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/area/delete")
    public WebServiceResponseWrapper deleteArea(@RequestParam("areaID") int areaID) {

        Area area = areaService.findByAreaId(areaID);
        if (area == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the area by id: " + areaID);
        }
        areaService.deleteAreaByAreaId(areaID);
        return new WebServiceResponseWrapper<Area>(0, "", area);
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/area/new")
    public WebServiceResponseWrapper createArea(@RequestParam("name") String name,
                                                @RequestParam("buildingID") int buildingID,
                                                @RequestParam("areaType") String areaType,
                                                @RequestParam("volumeType") String volumeType) {

        Area area = new Area();
        area.setName(name);
        area.setBuilding(buildingService.findByBuildingID(buildingID));
        area.setAreaType(AreaType.valueOf(areaType));
        area.setVolumeType(VolumeType.valueOf(volumeType));

        Area newArea = areaService.save(area);

        return new WebServiceResponseWrapper<Area>(0, "", newArea);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/area/edit")
    public WebServiceResponseWrapper changeArea(@RequestParam("areaID") int areaID,
                                                @RequestParam("name") String name,
                                                @RequestParam("buildingID") int buildingID,
                                                @RequestParam("areaType") String areaType,
                                                @RequestParam("volumeType") String volumeType) {
        Area area = areaService.findByAreaId(areaID);
        if (area == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the area by id: " + areaID);
        }
        area.setName(name);
        area.setBuilding(buildingService.findByBuildingID(buildingID));
        area.setAreaType(AreaType.valueOf(areaType));
        area.setVolumeType(VolumeType.valueOf(volumeType));
        areaService.save(area);

        return new WebServiceResponseWrapper<Area>(0, "", areaService.findByAreaId(areaID));
    }

}
