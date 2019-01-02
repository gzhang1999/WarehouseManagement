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

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "location_name_template")
public class LocationNameTemplate  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_name_template_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @JoinColumn(name = "location_name_template_id")
    @OneToMany(cascade = {CascadeType.ALL})
    @OrderBy("sequence asc")
    private List<LocationNameTemplateItem> locationNameTemplateItemList;

    public LocationNameTemplateItem getItemBySequence(int sequence) {
        // If the user pass in a value that greater than the size of the list
        // return null
        if (sequence > locationNameTemplateItemList.size()) {
            return null;
        }

        int currentSequence = 0;
        Collections.sort(locationNameTemplateItemList, new Comparator<LocationNameTemplateItem>() {
            @Override
            public int compare(LocationNameTemplateItem locationNameTemplateItem1, LocationNameTemplateItem locationNameTemplateItem2) {
                return (locationNameTemplateItem1.getSequence() - locationNameTemplateItem2.getSequence());
            }
        });
        Iterator<LocationNameTemplateItem> itemIterator = locationNameTemplateItemList.iterator();
        while(itemIterator.hasNext()) {
            if (currentSequence == sequence) {
                return itemIterator.next();
            }
            else if (currentSequence > sequence){
                break;
            }
            else {
                itemIterator.next();
                currentSequence++;
            }
        }
        // We should never reach here
        return null;
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

    public List<LocationNameTemplateItem> getLocationNameTemplateItemList() {
        return locationNameTemplateItemList;
    }

    public void setLocationNameTemplateItemList(List<LocationNameTemplateItem> locationNameTemplateItemList) {
        this.locationNameTemplateItemList = locationNameTemplateItemList;
    }
}
