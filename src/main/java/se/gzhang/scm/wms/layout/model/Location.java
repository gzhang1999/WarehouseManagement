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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.gzhang.scm.wms.common.model.VehicleType;
import se.gzhang.scm.wms.common.model.Velocity;
import se.gzhang.scm.wms.inventory.model.Inventory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "location")
@JsonSerialize(using = LocationSerializer.class)
public class Location  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @Column(name = "aisle_id")
    private String aisleID;

    @Column(name = "pickable")
    private Boolean pickable = false;

    @Column(name = "storable")
    private Boolean storable = false;

    @Column(name = "usable")
    private Boolean usable = false;


    @Column(name = "length")
    private Double length = 0.0;
    @Column(name = "width")
    private Double width = 0.0;
    @Column(name = "height")
    private Double height = 0.0;
    @Column(name = "volume")
    private Double volume = 0.0;
    @Column(name = "pending_volume")
    private Double pendingVolume = 0.0;
    @Column(name = "used_volume")
    private Double usedVolume = 0.0;


    @Column(name = "travel_sequence")
    private Integer travelSequence;
    @Column(name = "order_allocation_sequence")
    private Integer orderAllocationSequence;
    @Column(name = "putaway_sequence")
    private Integer putawaySequence;
    @Column(name = "pick_sequence")
    private Integer pickSequence;
    @Column(name = "count_sequence")
    private Integer countSequence;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "location_group_assignment", joinColumns = @JoinColumn(name = "location_id"), inverseJoinColumns = @JoinColumn(name = "location_group_id"))
    private Set<LocationGroup> locationGroups;

    @Column(name = "level")
    private String level;

    // Velocity of the location
    @OneToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="velocity_id")
    private Velocity velocity;

    // which type of vehicle can access this location
    // for example, higher locations can only be accessed by
    // forklift truck while lower locations can be accessed by
    // pallet jet with handheld
    @OneToMany(
            cascade ={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<VehicleType> accessibleVehicleTypes;


    @OneToMany(
            mappedBy = "location",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Inventory> inventoryList = new ArrayList<>();
    // Coordinate used for labor management
    @Column(name = "coordinate_x")
    private Double coordinateX;
    @Column(name = "coordinate_y")
    private Double coordinateY;
    @Column(name = "coordinate_z")
    private Double coordinateZ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location )) return false;
        return id != null && id.equals(((Location) o).id);

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

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getAisleID() {
        return aisleID;
    }

    public void setAisleID(String aisleID) {
        this.aisleID = aisleID;
    }

    public Boolean isPickable() {
        return pickable;
    }

    public void setPickable(Boolean pickable) {
        this.pickable = pickable;
    }

    public Boolean isStorable() {
        return storable;
    }

    public void setStorable(Boolean storable) {
        this.storable = storable;
    }

    public Boolean isUsable() {
        return usable;
    }

    public void setUsable(Boolean usable) {
        this.usable = usable;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getPendingVolume() {
        return pendingVolume;
    }

    public void setPendingVolume(Double pendingVolume) {
        this.pendingVolume = pendingVolume;
    }

    public Double getUsedVolume() {
        return usedVolume;
    }

    public void setUsedVolume(Double usedVolume) {
        this.usedVolume = usedVolume;
    }

    public Set<LocationGroup> getLocationGroups() {
        return locationGroups;
    }

    public void setLocationGroups(Set<LocationGroup> locationGroups) {
        this.locationGroups = locationGroups;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public List<VehicleType> getAccessibleVehicleTypes() {
        return accessibleVehicleTypes;
    }

    public void setAccessibleVehicleTypes(List<VehicleType> accessibleVehicleTypes) {
        this.accessibleVehicleTypes = accessibleVehicleTypes;
    }

    public List<Inventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public Double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(Double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public Double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(Double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public Double getCoordinateZ() {
        return coordinateZ;
    }

    public void setCoordinateZ(Double coordinateZ) {
        this.coordinateZ = coordinateZ;
    }

    public Integer getTravelSequence() {
        return travelSequence;
    }

    public void setTravelSequence(Integer travelSequence) {
        this.travelSequence = travelSequence;
    }

    public Integer getOrderAllocationSequence() {
        return orderAllocationSequence;
    }

    public void setOrderAllocationSequence(Integer orderAllocationSequence) {
        this.orderAllocationSequence = orderAllocationSequence;
    }

    public Integer getPutawaySequence() {
        return putawaySequence;
    }

    public void setPutawaySequence(Integer putawaySequence) {
        this.putawaySequence = putawaySequence;
    }

    public Integer getPickSequence() {
        return pickSequence;
    }

    public void setPickSequence(Integer pickSequence) {
        this.pickSequence = pickSequence;
    }

    public Integer getCountSequence() {
        return countSequence;
    }

    public void setCountSequence(Integer countSequence) {
        this.countSequence = countSequence;
    }

    public LocationStatus getLocationStatus() {
        // it is easy to check whether the location is full.
        if (getUsedVolume() + getPendingVolume() > getVolume()) {
            return LocationStatus.FULL;
        }

        if (getUsedVolume() > 0 && getPendingVolume() > 0){
            // The location has inventory and pending inventory,
            // it can be either FILLED_PENDING(When total volume
            // doesn't exceed the volume of the location) or
            // FULL(when total volume reaches or exceed the
            // volume of the location)
            // since we already return FULL, if we are still here,
            // it is FILLED_PENDING
            return LocationStatus.FILLED_PENDING;

        }
        else if (getUsedVolume() > 0) {
            // The location has inventory inside, without any pending inventory
            // and is not full
            return LocationStatus.FILLED;

        }
        else if (getPendingVolume() > 0) {
            // The location is empty, but has pending inventory,
            // and is not full
            return LocationStatus.EMPTY_PENDING;
        }
        else {
            // The location is empty and doesn't have any pending inventory
            return LocationStatus.EMPTY;
        }
    }
}
