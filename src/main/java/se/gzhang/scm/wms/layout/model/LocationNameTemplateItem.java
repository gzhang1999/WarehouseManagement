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

import se.gzhang.scm.wms.common.model.EnumWithDescription;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "location_name_template_item")
public class LocationNameTemplateItem  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_name_template_item_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "sequence")
    private int sequence;


    // Whether the value is fixed or dynamically generated
    // from startValue and endValue
    @Column(name = "fixed_value_flag")
    private boolean fixedValue = false;

    // Fixed length of the item
    // If the user input digit / alphabetic
    // less than the fix length, we will append
    // 0 at front
    @Column(name = "length")
    private int length;

    @Column(name = "type")
    // We are only allow
    // 1. pure digit
    // 2. pure alphabet
    private LocationNameTemplateItemType locationNameTemplateItemType;

    // Values that will be not saved in the database
    // but we will need to create actual locations
    private String startValue;

    private String endValue;

    private LocationNameTemplateItemRangeType locationNameTemplateItemRangeType;


    @Override
    public String toString() {
        if (fixedValue) {

            return "{\n" +
                    "name:" + name + ",\n" +
                    "sequence:" + sequence + ",\n" +
                    "fixed value: true" +
                    "\n}";
        }
        else {
            return "{\n" +
                    "name:" + name + ",\n" +
                    "sequence:" + sequence + ",\n" +
                    "length:" + length + ",\n" +
                    "type:" + locationNameTemplateItemType + ",\n" +
                    "range_type:" + locationNameTemplateItemRangeType +
                    "\n}";
        }
    }

    public String toHTML() {
        return  "<p>{</p>" +
                "<p>name:" + name + ",</p>" +
                "<p>sequence:" + sequence + ",</p>" +
                "<p>length:" + length + ",</p>" +
                "<p>type:" + locationNameTemplateItemType + ",</p>" +
                "<p>range_type:" + locationNameTemplateItemRangeType + "</p>" +
                "<p>}</p>";

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(boolean fixedValue) {
        this.fixedValue = fixedValue;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public LocationNameTemplateItemType getLocationNameTemplateItemType() {
        return locationNameTemplateItemType;
    }

    public void setLocationNameTemplateItemType(LocationNameTemplateItemType locationNameTemplateItemType) {
        this.locationNameTemplateItemType = locationNameTemplateItemType;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public LocationNameTemplateItemRangeType getLocationNameTemplateItemRangeType() {
        return locationNameTemplateItemRangeType;
    }

    public void setLocationNameTemplateItemRangeType(LocationNameTemplateItemRangeType locationNameTemplateItemRangeType) {
        this.locationNameTemplateItemRangeType = locationNameTemplateItemRangeType;
    }

}
