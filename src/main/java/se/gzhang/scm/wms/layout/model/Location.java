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
    private Double pendingVolumn = 0.0;

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

    public boolean isPickable() {
        return pickable;
    }

    public void setPickable(boolean pickable) {
        this.pickable = pickable;
    }

    public boolean isStorable() {
        return storable;
    }

    public void setStorable(boolean storable) {
        this.storable = storable;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
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

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public double getCoordinateZ() {
        return coordinateZ;
    }

    public void setCoordinateZ(double coordinateZ) {
        this.coordinateZ = coordinateZ;
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

    public double getPendingVolumn() {
        return pendingVolumn;
    }

    public void setPendingVolumn(double pendingVolumn) {
        this.pendingVolumn = pendingVolumn;
    }
}
