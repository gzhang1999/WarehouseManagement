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

package se.gzhang.scm.wms.inventory.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.layout.model.Location;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "inventory")
@JsonSerialize(using = InventorySerializer.class)
public class Inventory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inventory_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_footprint_id")
    private ItemFootprint itemFootprint;


    @Column(name="lpn")
    private String lpn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name="quantity")
    private int quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="inventory_status_id")
    private InventoryStatus inventoryStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="receipt_line_id")
    private ReceiptLine receiptLine;

    // The next location on the movement path
    // that the inventory will go.
    // For example, when putaway, the destination
    // of the putaway or hop location
    // when shipping, the destination may be the
    // stage or an WIP(Work In Process) location
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "next_location_id", referencedColumnName="location_id")
    private Location nextLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_location_id", referencedColumnName="location_id")
    private Location destinationLocation;

    // Temporary location used when running mock putaway
    // The value is calculated on a fly and will be moved to
    // the destinationLocaiton field after user's confirm
    @Transient
    private Location suggestedDestinationLocation;

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Inventory) {
            Inventory anotherInventory = (Inventory)anObject;
            if (anotherInventory.getId() != null &&
                    getId() != null &&
                    anotherInventory.getId().equals(getId())) {
                return true;
            }
        }
        return false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemFootprint getItemFootprint() {
        return itemFootprint;
    }

    public void setItemFootprint(ItemFootprint itemFootprint) {
        this.itemFootprint = itemFootprint;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public ReceiptLine getReceiptLine() {
        return receiptLine;
    }

    public void setReceiptLine(ReceiptLine receiptLine) {
        this.receiptLine = receiptLine;
    }

    public Location getNextLocation() {
        return nextLocation;
    }

    public void setNextLocation(Location nextLocation) {
        this.nextLocation = nextLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Location getSuggestedDestinationLocation() {
        return suggestedDestinationLocation;
    }

    public void setSuggestedDestinationLocation(Location suggestedDestinationLocation) {
        this.suggestedDestinationLocation = suggestedDestinationLocation;
    }
}
