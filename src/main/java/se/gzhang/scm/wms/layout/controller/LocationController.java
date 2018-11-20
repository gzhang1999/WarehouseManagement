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
import se.gzhang.scm.wms.common.model.Velocity;
import se.gzhang.scm.wms.common.service.VelocityService;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Building;
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
    @Autowired
    BuildingService buildingService;
    @Autowired
    AreaService areaService;
    @Autowired
    VelocityService velocityService;


    @RequestMapping(value="/layout/location", method = RequestMethod.GET)
    public ModelAndView listLocations() {
        ModelAndView modelAndView = new ModelAndView();

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
    @ResponseBody
    @RequestMapping(value="/ws/layout/location/query/{id}")
    public WebServiceResponseWrapper getLocation(@PathVariable("id") int locationID) {

        Location location = locationService.findByLocationId(locationID);
        if (location == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the location by id: " + locationID);
        }
        return new WebServiceResponseWrapper<Location>(0, "", location);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/delete")
    public WebServiceResponseWrapper deleteLocation(@RequestParam("locationID") int locationID) {

        Location location = locationService.findByLocationId(locationID);
        if (location == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the location by id: " + locationID);
        }
        locationService.deleteByLocationId(locationID);
        return new WebServiceResponseWrapper<Location>(0, "", location);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/edit")
    public WebServiceResponseWrapper changeLocation(@RequestParam("locationID") int locationID,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("buildingID") int buildingID,
                                                    @RequestParam("areaID") int areaID,
                                                    @RequestParam("aisleID") String aisleID,
                                                    @RequestParam("length") double length,
                                                    @RequestParam("width") double width,
                                                    @RequestParam("height") double height,
                                                    @RequestParam("volume") double volume,
                                                    @RequestParam("pickable") boolean pickable,
                                                    @RequestParam("storable") boolean storable,
                                                    @RequestParam("usable") boolean usable,
                                                    @RequestParam("velocity") int velocityID,
                                                    @RequestParam("coordinateX") double coordinateX,
                                                    @RequestParam("coordinateY") double coordinateY,
                                                    @RequestParam("coordinateZ") double coordinateZ) {

        Location location = locationService.findByLocationId(locationID);
        if (location == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the location by id: " + locationID);
        }
        Building buidling = buildingService.findByBuildingID(buildingID);
        if (buidling == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the buidling by id: " + buildingID);
        }
        Area area = areaService.findByAreaId(areaID);
        if (area == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the area by id: " + areaID);
        }
        if (area.getBuilding() != buidling) {
            return WebServiceResponseWrapper.raiseError(10001, "Area " + area.getName() + " is not in Building " + buidling.getName());

        }

        Velocity velocity = velocityService.findByVelocityId(velocityID);
        if (velocity == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the velocity by ID: " + velocityID);
        }
        else {
            location.setVelocity(velocity);
        }
        location.setName(name);
        location.setArea(area);
        location.setAisleID(aisleID);
        location.setLength(length);
        location.setWidth(width);
        location.setHeight(height);
        location.setVolume(volume);
        location.setPickable(pickable);
        location.setStorable(storable);
        location.setUsable(usable);
        location.setCoordinateX(coordinateX);
        location.setCoordinateY(coordinateY);
        location.setCoordinateZ(coordinateZ);

        Location newLocation = locationService.save(location);

        return new WebServiceResponseWrapper<Location>(0, "", newLocation);
    }
    @ResponseBody
    @RequestMapping(value="/ws/layout/location/new")
    public WebServiceResponseWrapper createLocation(@RequestParam("name") String name,
                                                    @RequestParam("areaID") int areaID,
                                                    @RequestParam(value="aisleID",required=false) String aisleID,
                                                    @RequestParam("length") double length,
                                                    @RequestParam("width") double width,
                                                    @RequestParam("height") double height,
                                                    @RequestParam("volume") double volume,
                                                    @RequestParam("pickable") boolean pickable,
                                                    @RequestParam("storable") boolean storable,
                                                    @RequestParam("usable") boolean usable,
                                                    @RequestParam(value="velocity",required=false) Integer velocityID,
                                                    @RequestParam(value="coordinateX",required=false) Double coordinateX,
                                                    @RequestParam(value="coordinateY",required=false) Double coordinateY,
                                                    @RequestParam(value="coordinateZ",required=false) Double coordinateZ) {

        Location location = locationService.findByLocationName(name);
        if (location != null) {
            return WebServiceResponseWrapper.raiseError(10000, "The location " + name  + " already exists");
        }
        Area area = areaService.findByAreaId(areaID);
        if (area == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the area by id: " + areaID);
        }
        if (aisleID == null) {
            aisleID = "";
        }

        if (velocityID == null) {
            velocityID = velocityService.findAll().get(0).getId();
        }
        if (coordinateX == null) {
            coordinateX = 0.0;
        }
        if (coordinateY == null) {
            coordinateY =  0.0;
        }
        if (coordinateZ == null) {
            coordinateZ =  0.0;
        }

        location = new Location();
        location.setName(name);
        location.setArea(area);
        location.setAisleID(aisleID);
        location.setLength(length);
        location.setWidth(width);
        location.setHeight(height);
        location.setVolume(volume);
        location.setPickable(pickable);
        location.setStorable(storable);
        location.setUsable(usable);
        location.setVelocity(velocityService.findByVelocityId(velocityID));
        location.setCoordinateX(coordinateX);
        location.setCoordinateY(coordinateY);
        location.setCoordinateZ(coordinateZ);

        locationService.save(location);

        return new WebServiceResponseWrapper<Location>(0, "", locationService.findByLocationName(name));
    }

}
