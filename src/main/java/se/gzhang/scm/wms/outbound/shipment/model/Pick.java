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

package se.gzhang.scm.wms.outbound.shipment.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.layout.model.Location;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pick")
@JsonSerialize(using = PickSerializer.class)
public class Pick implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pick_id")
    private Integer id;

    @Column(name = "number",unique=true)
    private String number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_location_id", referencedColumnName="location_id")
    private Location sourceLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_location_id", referencedColumnName="location_id")
    private Location destinationLocation;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "picked_quantity")
    private Integer pickedQuantity;

    @Column(name = "pick_state")
    private PickState pickState;

    // Only when the pick is cancelled
    @Column(name = "cancelled_date")
    private Date cancelledDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="shipment_line_id")
    private ShipmentLine shipmentLine;

    @OneToMany(
            mappedBy = "pick"
    )
    private List<Inventory> pickedInventory = new ArrayList<>();

    // When we picked by carton, for parcel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="carton_id")
    private Carton carton;

    @Transient
    private CartonType cartonType;

    @Override
    public Pick clone() throws CloneNotSupportedException{
        return (Pick)super.clone();

    }

    public Item getItem() {
        return getShipmentLine().getSalesOrderLine().getItem();
    }

    public InventoryStatus getInventoryStatus() {return getShipmentLine().getSalesOrderLine().getInventoryStatus();}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ShipmentLine getShipmentLine() {
        return shipmentLine;
    }

    public void setShipmentLine(ShipmentLine shipmentLine) {
        this.shipmentLine = shipmentLine;
    }

    public PickState getPickState() {
        return pickState;
    }

    public void setPickState(PickState pickState) {
        this.pickState = pickState;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public Integer getPickedQuantity() {
        return pickedQuantity;
    }

    public void setPickedQuantity(Integer pickedQuantity) {
        this.pickedQuantity = pickedQuantity;
    }

    public List<Inventory> getPickedInventory() {
        return pickedInventory;
    }

    public void setPickedInventory(List<Inventory> pickedInventory) {
        this.pickedInventory = pickedInventory;
    }

    public Carton getCarton() {
        return carton;
    }

    public void setCarton(Carton carton) {
        this.carton = carton;
    }

    public CartonType getCartonType() {
        return cartonType;
    }

    public void setCartonType(CartonType cartonType) {
        this.cartonType = cartonType;
    }
}
