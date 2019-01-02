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

import com.fasterxml.jackson.annotation.JsonIgnore;
import se.gzhang.scm.wms.common.model.Address;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "building")
public class Building implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "building_id")
    private Integer id;

    @Column(name = "name")
    private String name;


    @OneToOne(cascade={CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name="address_id")
    private Address address;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Area> areas = new ArrayList<>();

    @Column(name = "pick_sequence")
    private int pickSequence;

    @Column(name = "storage_sequence")
    private int storageSequence;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Building )) return false;
        return id != null && id.equals(((Building) o).id);
    }

    public int getTotalAreaCount() {
        return areas.size();
    }

    public int getTotalLocationCount() {
        int totalLocationCount = 0;
        for(Area area : areas) {
            totalLocationCount += area.getLocations().size();
        }
        return totalLocationCount;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public int getPickSequence() {
        return pickSequence;
    }

    public void setPickSequence(int pickSequence) {
        this.pickSequence = pickSequence;
    }

    public int getStorageSequence() {
        return storageSequence;
    }

    public void setStorageSequence(int storageSequence) {
        this.storageSequence = storageSequence;
    }
}
