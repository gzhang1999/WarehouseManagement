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

package se.gzhang.scm.wms.layout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.LocalParameterMap;
import se.gzhang.scm.wms.layout.model.AddLocationFlowModel;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.LocationNameTemplate;
import se.gzhang.scm.wms.layout.model.LocationNameTemplateItem;

import java.util.Map;

@Component
public class AddLocationFlowHandler {

    @Autowired
    LocationNameTemplateService locationNameTemplateService;

    public AddLocationFlowModel init() {
        return new AddLocationFlowModel();
    }

    public void setUseLocationNameTemplateFlag(AddLocationFlowModel addLocationFlowModel, boolean useLocationNameTemplateFlag) {
        addLocationFlowModel.setUseLocationNameTemplateFlag(useLocationNameTemplateFlag);
    }
    public void setLocationNameTemplate(AddLocationFlowModel addLocationFlowModel, LocationNameTemplate locationNameTemplate) {
        addLocationFlowModel.setLocationNameTemplate(locationNameTemplate);
    }

    public void setLocation(AddLocationFlowModel addLocationFlowModel, Location location) {
        addLocationFlowModel.setLocation(location);
    }

    public void setupNameTemplate(AddLocationFlowModel addLocationFlowModel, LocalParameterMap parameters){
        Map<String, Object> parameterMap = parameters.asMap();
        System.out.println("Calling setupNameTemplate with following parameters:");
        for(Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            System.out.println("name: " + entry.getKey() + ", value: " + entry.getValue());
        }


        int templateID = Integer.parseInt(parameterMap.get("nameTemplateID").toString());
        LocationNameTemplate locationNameTemplate = locationNameTemplateService.findByLocationNameTemplateID(templateID);

        for(LocationNameTemplateItem locationNameTemplateItem : locationNameTemplate.getLocationNameTemplateItemList()) {
            int itemID = locationNameTemplateItem.getId();
            String startValue = parameterMap.get("templateItemRangeStartValue-" + itemID).toString();
            locationNameTemplateItem.setStartValue(startValue);

            // For fixed value template item, we will only have a start value as the fixed value.
            // There's no need to have a range
            String endValue = "";
            if (!locationNameTemplateItem.isFixedValue()) {
                endValue = parameterMap.get("templateItemRangeEndValue-" + itemID).toString();
            }
            System.out.println("Item ID: " + itemID + " ,\n" +
                    "Name: " + locationNameTemplateItem.getName() + " ,\n" +
                    "sequence: " + locationNameTemplateItem.getSequence() + " ,\n" +
                    "length: " + locationNameTemplateItem.getLength() + " ,\n" +
                    "locationNameTemplateItemType: " + locationNameTemplateItem.getLocationNameTemplateItemType() + " ,\n" +
                    "locationNameTemplateItemRangeType: " + locationNameTemplateItem.getLocationNameTemplateItemRangeType() + " ,\n" +
                    "startValue: " + startValue + " ,\n" +
                    "endValue: " + endValue);
            locationNameTemplateItem.setEndValue(endValue);
        }
        addLocationFlowModel.setLocationNameTemplate(locationNameTemplate);
    }
}
