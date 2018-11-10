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
import se.gzhang.scm.wms.layout.model.*;
import se.gzhang.scm.wms.layout.service.LocationNameTemplateItemService;
import se.gzhang.scm.wms.layout.service.LocationNameTemplateService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.util.List;
import java.util.Map;

@Controller
public class LocationNameTemplateController {

    private static final String APPLICATION_ID = "Layout";
    private static final String FORM_ID = "LocationNameTemplate";

    @Autowired
    LocationNameTemplateService locationNameTemplateService;
    @Autowired
    LocationNameTemplateItemService locationNameTemplateItemService;


    @RequestMapping(value="/layout/location/nametemplatedisplay", method = RequestMethod.GET)
    public ModelAndView listLocationNameTemplate() {
        ModelAndView modelAndView = new ModelAndView();

        List<LocationNameTemplate> locationNameTemplateList = locationNameTemplateService.findAll();
        int maxTemplateItemCount = 0;
        for(LocationNameTemplate locationNameTemplate: locationNameTemplateList) {
            if(locationNameTemplate.getLocationNameTemplateItemList().size() > maxTemplateItemCount) {
                maxTemplateItemCount = locationNameTemplate.getLocationNameTemplateItemList().size();
            }
        }
        modelAndView.addObject("locationNameTemplateList", locationNameTemplateService.findAll());
        modelAndView.addObject("maxTemplateItemCount", maxTemplateItemCount);
        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("layout/location/nametemplatedisplay");
        return modelAndView;

    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/list")
    public WebServiceResponseWrapper queryLocationNameTemplate(@RequestParam Map<String, String> parameters) {

        List<LocationNameTemplate> locationNameTemplateList = locationNameTemplateService.findLocationNameTemplate(parameters);
        return new WebServiceResponseWrapper<List<LocationNameTemplate>>(0, "", locationNameTemplateList);
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/query/{id}")
    public WebServiceResponseWrapper getLocationNameTemplate(@PathVariable("id") int templateID) {

        LocationNameTemplate locationNameTemplate = locationNameTemplateService.findByLocationNameTemplateID(templateID);
        return new WebServiceResponseWrapper<LocationNameTemplate>(0, "", locationNameTemplate);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/delete")
    public WebServiceResponseWrapper deleteLocationNameTemplate(@RequestParam("templateID") int templateID) {

        LocationNameTemplate locationNameTemplate = locationNameTemplateService.findByLocationNameTemplateID(templateID);
        if(locationNameTemplate == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the template by id: " + templateID);
        }
        locationNameTemplateService.deleteByLocationNameTemplateID(templateID);
        return new WebServiceResponseWrapper<LocationNameTemplate>(0, "", locationNameTemplate);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/new")
    public WebServiceResponseWrapper createTemplate(@RequestParam("name") String templateName) {

        LocationNameTemplate locationNameTemplate = new LocationNameTemplate();
        locationNameTemplate.setName(templateName);

        LocationNameTemplate newTemplate = locationNameTemplateService.save(locationNameTemplate);


        return new WebServiceResponseWrapper<LocationNameTemplate>(0, "", newTemplate);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/edit")
    public WebServiceResponseWrapper changeTemplate(@RequestParam("templateID") int templateID,
                                                @RequestParam("name") String name) {

        LocationNameTemplate locationNameTemplate = locationNameTemplateService.findByLocationNameTemplateID(templateID);
        if (locationNameTemplate == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the template by id: " + templateID);
        }
        locationNameTemplate.setName(name);
        locationNameTemplateService.save(locationNameTemplate);

        return new WebServiceResponseWrapper<LocationNameTemplate>(0, "", locationNameTemplateService.findByLocationNameTemplateID(templateID));
    }


    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/item/query/{id}")
    public WebServiceResponseWrapper getLocationNameTemplateItem(@PathVariable("id") int templateItemID) {

        LocationNameTemplateItem locationNameTemplateItem = locationNameTemplateItemService.findByLocationNameTemplateItemID(templateItemID);
        System.out.println("try to find by template id: " + templateItemID);
        return new WebServiceResponseWrapper<LocationNameTemplateItem>(0, "", locationNameTemplateItem);
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/item/new")
    public WebServiceResponseWrapper createLocationNameTemplateItem(@RequestParam("templateID") int templateID,
                                                                    @RequestParam("name") String name,
                                                                    @RequestParam("fixedValue") boolean fixedValue,
                                                                    @RequestParam("sequence") int sequence,
                                                                    @RequestParam("length") int length,
                                                                    @RequestParam("type") String type,
                                                                    @RequestParam("rangeType") String rangeType) {

        LocationNameTemplate locationNameTemplate = locationNameTemplateService.findByLocationNameTemplateID(templateID);
        if (locationNameTemplate == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the template by id: " + templateID);
        }
        LocationNameTemplateItem locationNameTemplateItem = new LocationNameTemplateItem();
        locationNameTemplateItem.setName(name);
        locationNameTemplateItem.setSequence(sequence);
        if(LocationNameTemplateItemType.valueOf(type) == LocationNameTemplateItemType.ALPHABET) {
            // For alphabetic column, we will only allow 1 characters for now!
            locationNameTemplateItem.setLength(1);
        }
        else {

            locationNameTemplateItem.setLength(length);
        }
        locationNameTemplateItem.setFixedValue(fixedValue);
        locationNameTemplateItem.setLocationNameTemplateItemType(LocationNameTemplateItemType.valueOf(type));
        locationNameTemplateItem.setLocationNameTemplateItemRangeType(LocationNameTemplateItemRangeType.valueOf(rangeType));
        locationNameTemplate.getLocationNameTemplateItemList().add(locationNameTemplateItem);
        locationNameTemplateService.save(locationNameTemplate);

        return new WebServiceResponseWrapper<LocationNameTemplate>(0, "", locationNameTemplateService.findByLocationNameTemplateID(templateID));
    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/item/edit")
    public WebServiceResponseWrapper changeLocationNameTemplateItem(@RequestParam("templateID") int templateID,
                                                                    @RequestParam("templateItemID") int templateItemID,
                                                                    @RequestParam("name") String name,
                                                                    @RequestParam("fixedValue") boolean fixedValue,
                                                                    @RequestParam("sequence") int sequence,
                                                                    @RequestParam("length") int length,
                                                                    @RequestParam("type") String type,
                                                                    @RequestParam("rangeType") String rangeType) {

        LocationNameTemplate locationNameTemplate = locationNameTemplateService.findByLocationNameTemplateID(templateID);
        if (locationNameTemplate == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the template by id: " + templateID);
        }
        // Find the item with the same id within the location name template
        for(LocationNameTemplateItem locationNameTemplateItem : locationNameTemplate.getLocationNameTemplateItemList()) {
            if (locationNameTemplateItem.getId() == templateItemID) {
                // we found such template item, let's change the value, save and return the name template
                locationNameTemplateItem.setName(name);
                locationNameTemplateItem.setSequence(sequence);
                if(LocationNameTemplateItemType.valueOf(type) == LocationNameTemplateItemType.ALPHABET) {
                    // For alphabetic column, we will only allow 1 characters for now!
                    locationNameTemplateItem.setLength(1);
                }
                else {

                    locationNameTemplateItem.setLength(length);
                }
                locationNameTemplateItem.setFixedValue(fixedValue);
                locationNameTemplateItem.setLocationNameTemplateItemType(LocationNameTemplateItemType.valueOf(type));
                locationNameTemplateItem.setLocationNameTemplateItemRangeType(LocationNameTemplateItemRangeType.valueOf(rangeType));
                locationNameTemplateService.save(locationNameTemplate);

                return new WebServiceResponseWrapper<LocationNameTemplate>(0, "", locationNameTemplateService.findByLocationNameTemplateID(templateID));
            }
        }

        // If we are still here, which means the name template is a valid template but we can't find the
        // item with the id being passed in, let's raise the error
        return WebServiceResponseWrapper.raiseError(10000, "Can't find the template item by id: " + templateItemID);

    }

    @ResponseBody
    @RequestMapping(value="/ws/layout/location/nametemplate/item/delete")
    public WebServiceResponseWrapper deleteLocationNameTemplateItem(@RequestParam("templateItemID") int templateItemID) {
        LocationNameTemplateItem locationNameTemplateItem = locationNameTemplateItemService.findByLocationNameTemplateItemID(templateItemID);
        if (locationNameTemplateItem == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the template item by id: " + templateItemID);
        }
        locationNameTemplateItemService.deleteLocationTemplateItemById(templateItemID);
        return new WebServiceResponseWrapper<LocationNameTemplateItem>(0, "", locationNameTemplateItem);


    }


}
