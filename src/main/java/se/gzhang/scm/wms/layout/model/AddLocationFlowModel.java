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

package se.gzhang.scm.wms.layout.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddLocationFlowModel implements Serializable {

    private boolean useLocationNameTemplateFlag;

    private LocationNameTemplate locationNameTemplate;

    private Location location;

    public AddLocationFlowModel() {
        useLocationNameTemplateFlag = true;
        locationNameTemplate = new LocationNameTemplate();
    }

    // Function to get valid name from location name template
    public List<String> getValidName() {
        List<String> validNameList = new ArrayList<String>();

        List<LocationNameTemplateItem> locationNameTemplateItems = locationNameTemplate.getLocationNameTemplateItemList();

        // Loop through all the template item and concatenate all the valid naming of the current item
        // to the all valid naming of the previous item
        // For example, if the items are defined as below
        // Sequence     Type       Length     Start Value       End Value
        //     1        Digit        2           1               10
        //     2        ALPHABET     1           A               D
        //     3        DIGIT        3           1               999
        // The first round loop will get a list of possible name from 01 to 10
        // The second round loop will get a list of possible name from 01A to 10D
        // The third round loop will get a list of possible name from 01a001 to 10D999
        for(LocationNameTemplateItem locationNameTemplateItem : locationNameTemplateItems) {
            validNameList = getValidNameByTemplateItem(validNameList,locationNameTemplateItem);
        }
        return validNameList;

    }

    private List<String> getValidNameByTemplateItem(List<String> originalValidNameList, LocationNameTemplateItem locationNameTemplateItem){
        List<String> validNameList = new ArrayList<>();
        // If the valid name list passed in is empty, then we simply return all the valid possible
        // value based on the name tample item
        if(originalValidNameList.size() == 0){
            int length = locationNameTemplateItem.getLength();
            if (locationNameTemplateItem.isFixedValue() == true) {
                validNameList.add(locationNameTemplateItem.getStartValue());
            }
            else if (locationNameTemplateItem.getLocationNameTemplateItemType() == LocationNameTemplateItemType.DIGIT) {
                for(int i = Integer.parseInt(locationNameTemplateItem.getStartValue()); i <= Integer.parseInt(locationNameTemplateItem.getEndValue()); i++) {
                    if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.BOTH) {
                        validNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.ODD && i % 2 == 1){
                        validNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.EVEN && i % 2 == 0){
                        validNameList.add(String.format("%0" + length + "d", i));
                    }
                }
            }
            else {
                // TO-DO: Currently we will only support ALPHABET with length = 1;
                char startChar = locationNameTemplateItem.getStartValue().charAt(0);
                char endChar = locationNameTemplateItem.getEndValue().charAt(0);
                for(char alphabet = startChar; alphabet <= endChar; alphabet++ )
                {
                    validNameList.add(Character.toString(alphabet));
                }
            }
        }
        else {
            // We will get all the possible value from current template item and then
            // concatenate the new value to the original list
            List<String> newValidNameList = new ArrayList<>();
            int length = locationNameTemplateItem.getLength();
            if (locationNameTemplateItem.isFixedValue() == true) {
                newValidNameList.add(locationNameTemplateItem.getStartValue());
            }
            else if (locationNameTemplateItem.getLocationNameTemplateItemType() == LocationNameTemplateItemType.DIGIT) {
                for(int i = Integer.parseInt(locationNameTemplateItem.getStartValue()); i <= Integer.parseInt(locationNameTemplateItem.getEndValue()); i++) {

                    if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.BOTH) {
                        newValidNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.ODD && i % 2 == 1){
                        newValidNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.EVEN && i % 2 == 0){
                        newValidNameList.add(String.format("%0" + length + "d", i));

                    }
                }
            }
            else {
                // TO-DO: Currently we will only support ALPHABET with length = 1;
                char startChar = locationNameTemplateItem.getStartValue().charAt(0);
                char endChar = locationNameTemplateItem.getEndValue().charAt(0);
                for(char alphabet = startChar; alphabet <= endChar; alphabet++ )
                {
                    newValidNameList.add(Character.toString(alphabet));
                }
            }
            for(String originalValidName : originalValidNameList) {
                for(String newValidName : newValidNameList) {
                    validNameList.add(originalValidName+newValidName);
                }
            }
        }
        return validNameList;
    }

    public boolean isUseLocationNameTemplateFlag() {
        return useLocationNameTemplateFlag;
    }

    public void setUseLocationNameTemplateFlag(boolean useLocationNameTemplateFlag) {
        this.useLocationNameTemplateFlag = useLocationNameTemplateFlag;
    }

    public LocationNameTemplate getLocationNameTemplate() {
        return locationNameTemplate;
    }

    public void setLocationNameTemplate(LocationNameTemplate locationNameTemplate) {
        this.locationNameTemplate = locationNameTemplate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
