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

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class AddLocationFlowModel implements Serializable {

    private boolean useLocationNameTemplateFlag;

    private LocationNameTemplate locationNameTemplate;

    private Location location;

    // Function to get valid name from location name template
    public List<String> getValidName() {
        System.out.println("Strat to get valid name!");
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
            System.out.println(">> after item: " + locationNameTemplateItem.getName() + ", size of the validName is " + validNameList.size());
        }
        return validNameList;

    }

    private List<String> getValidNameByTemplateItem(List<String> originalValidNameList, LocationNameTemplateItem locationNameTemplateItem){
        List<String> validNameList = new ArrayList<>();
        // If the valid name list passed in is empty, then we simply return all the valid possible
        // value based on the name tample item
        System.out.println("## Start to get valid name while originalValidNameList.size is " + originalValidNameList.size());
        if(originalValidNameList.size() == 0){
            int length = locationNameTemplateItem.getLength();
            if (locationNameTemplateItem.isFixedValue() == true) {
                validNameList.add(locationNameTemplateItem.getStartValue());
            }
            else if (locationNameTemplateItem.getLocationNameTemplateItemType() == LocationNameTemplateItemType.DIGIT) {
                for(int i = Integer.parseInt(locationNameTemplateItem.getStartValue()); i <= Integer.parseInt(locationNameTemplateItem.getEndValue()); i++) {
                    if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.BOTH) {
                        System.out.print("## 1. Add DIGIT name:" + String.format("%0" + length + "d", i));
                        validNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.ODD && i % 2 == 1){
                        System.out.print("## 1. Add DIGIT name:" + String.format("%0" + length + "d", i));
                        validNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.EVEN && i % 2 == 0){
                        System.out.print("## 1. Add DIGIT name:" + String.format("%0" + length + "d", i));
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
                    System.out.print("## 1. Add ALPHABET name:" + Character.toString(alphabet));
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
                        System.out.print("## 2. Add DIGIT name:" + String.format("%0" + length + "d", i));
                        newValidNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.ODD && i % 2 == 1){
                        System.out.print("## 2. Add DIGIT name:" + String.format("%0" + length + "d", i));
                        newValidNameList.add(String.format("%0" + length + "d", i));
                    }
                    else if (locationNameTemplateItem.getLocationNameTemplateItemRangeType() == LocationNameTemplateItemRangeType.EVEN && i % 2 == 0){
                        System.out.print("## 2. Add DIGIT name:" + String.format("%0" + length + "d", i));
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
                    System.out.print("## 2. Add ALPHABET name:" + Character.toString(alphabet));
                    newValidNameList.add(Character.toString(alphabet));
                }
            }
            for(String originalValidName : originalValidNameList) {
                for(String newValidName : newValidNameList) {
                    System.out.print("## 2.1. Concatenate value: " + originalValidName + " & " + newValidName);
                    validNameList.add(originalValidName+newValidName);
                }
            }
        }
        return validNameList;
    }
}
