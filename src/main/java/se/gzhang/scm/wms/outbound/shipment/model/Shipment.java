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
import se.gzhang.scm.wms.common.model.Customer;
import se.gzhang.scm.wms.layout.model.Warehouse;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/*
 * Shipment is a object standards for each shipping activities
 * for a typical order. One order may be shipped multiple times
 * thus may have multiple shipments. But we will stick to one shipment
 * can only have one order for simplicity.
 * Shipment is a internal object that won't be shared with other
 * system.
 * */
@Entity
@Table(name = "shipment")
@JsonSerialize(using = ShipmentSerializer.class)
public class Shipment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Integer id;

    @Column(name = "number",unique=true)
    private String number;

    // Ship to Customer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(
            mappedBy = "shipment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ShipmentLine> shipmentLines = new ArrayList<>();

    @Column(name = "shipment_state")
    private ShipmentState shipmentState;

    @Column(name = "shipping_method")
    private ShippingMethod shippingMethod;

    @Column(name = "cancelled_date")
    private Date cancelledDate;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Shipment)) {
            return false;
        }

        // If both receipt has ID, make sure the ID are the same
        // Otherwise, the receipts are same as long as the receipt numbers
        // are the same
        Shipment order = (Shipment)obj;
        if (this.id != null && order.getId() != null) {
            return this.id == order.getId();
        }
        else {
            return this.getNumber().equals(order.getNumber());
        }
    }

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<ShipmentLine> getShipmentLines() {
        return shipmentLines;
    }

    public void setShipmentLines(List<ShipmentLine> shipmentLines) {
        this.shipmentLines = shipmentLines;
    }

    public void addShipmentLine(ShipmentLine shipmentLine) {

        shipmentLines.add(shipmentLine);
    }

    public ShipmentState getShipmentState() {
        return shipmentState;
    }

    public void setShipmentState(ShipmentState shipmentState) {
        this.shipmentState = shipmentState;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
}
