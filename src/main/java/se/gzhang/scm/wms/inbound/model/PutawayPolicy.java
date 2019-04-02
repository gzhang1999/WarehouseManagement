/**
 * Copyright 2019
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

package se.gzhang.scm.wms.inbound.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemFamily;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.AreaGroup;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.LocationGroup;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "putaway_policy")
@JsonSerialize(using = PutawayPolicySerializer.class)
public class PutawayPolicy  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "putaway_policy_id")
    private Integer id;

    @Column(name = "sequence")
    private Integer sequence;

    // Available criteria
    // item number
    // item family
    // supplier
    // trailer
    // receipt id
    // purchase order number(TO-DO)
    // inventory attribute(size, weight)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_family_id")
    private ItemFamily itemFamily;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="trailer_id")
    private Trailer trailer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="receipt_id")
    private Receipt receipt;

    @Column(name = "min_size")
    private Double minimumSize;
    @Column(name = "max_size")
    private Double maximumSize;

    @Column(name = "min_weight")
    private Double minimumWeight;
    @Column(name = "max_weight")
    private Double maximumWeight;

    // Specify the destination when
    // the inventory being put away
    // meets the above criteria
    // Area / Area Group
    // Location / Location Group
    // Location Level
    // Aisle
    // Work Zone


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="area_id")
    private Area area;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="area_group_id")
    private AreaGroup areaGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="location_group_id")
    private LocationGroup locationGroup;

    @Column(name = "location_level")
    private String locationLevel;

    @Column(name = "location_aisle_id")
    private String locationAisleID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemFamily getItemFamily() {
        return itemFamily;
    }

    public void setItemFamily(ItemFamily itemFamily) {
        this.itemFamily = itemFamily;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public Double getMinimumSize() {
        return minimumSize;
    }

    public void setMinimumSize(Double minimumSize) {
        this.minimumSize = minimumSize;
    }

    public Double getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Double maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Double getMinimumWeight() {
        return minimumWeight;
    }

    public void setMinimumWeight(Double minimumWeight) {
        this.minimumWeight = minimumWeight;
    }

    public Double getMaximumWeight() {
        return maximumWeight;
    }

    public void setMaximumWeight(Double maximumWeight) {
        this.maximumWeight = maximumWeight;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public AreaGroup getAreaGroup() {
        return areaGroup;
    }

    public void setAreaGroup(AreaGroup areaGroup) {
        this.areaGroup = areaGroup;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationGroup getLocationGroup() {
        return locationGroup;
    }

    public void setLocationGroup(LocationGroup locationGroup) {
        this.locationGroup = locationGroup;
    }

    public String getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(String locationLevel) {
        this.locationLevel = locationLevel;
    }

    public String getLocationAisleID() {
        return locationAisleID;
    }

    public void setLocationAisleID(String locationAisleID) {
        this.locationAisleID = locationAisleID;
    }
}
