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
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "shipment_line")
@JsonSerialize(using = ShipmentLineSerializer.class)
public class ShipmentLine implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_line_id")
    private Integer id;

    @Column(name = "number",unique=true)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;


    // quantity that is supposed to be
    // shipped by this shipment
    @Column(name = "order_quantity")
    private Integer orderQuantity;


    // Quantity that already has outstanding
    // pick or short allocation
    @Column(name = "inprocess_quantity")
    private Integer inprocessQuantity;

    // quantity that is actually
    // shipped by this shipment
    @Column(name = "shipped_quantity")
    private Integer shippedQuantity;


    @OneToOne
    @JoinColumn(name="order_line_id")
    private SalesOrderLine salesOrderLine;

    @Column(name = "shipment_line_state")
    private ShipmentLineState shipmentLineState;


    @OneToMany(
            mappedBy = "shipmentLine",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Pick> picks = new ArrayList<>();

    @OneToMany(
            mappedBy = "shipmentLine",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ShortAllocation> shortAllocation = new ArrayList<>();

    @Column(name = "cancelled_date")
    private Date cancelledDate;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShipmentLine)) {
            return false;
        }

        // If both receipt has ID, make sure the ID are the same
        // Otherwise, the receipts are same as long as the receipt numbers
        // are the same
        ShipmentLine orderLine = (ShipmentLine)obj;
        if (this.id != null && orderLine.getId() != null) {
            return this.id == orderLine.getId();
        }
        else {
            return this.getNumber().equals(orderLine.getNumber());
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

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Integer getShippedQuantity() {
        return shippedQuantity;
    }

    public void setShippedQuantity(Integer shippedQuantity) {
        this.shippedQuantity = shippedQuantity;
    }

    public SalesOrderLine getSalesOrderLine() {
        return salesOrderLine;
    }

    public void setSalesOrderLine(SalesOrderLine salesOrderLine) {
        this.salesOrderLine = salesOrderLine;
    }

    public ShipmentLineState getShipmentLineState() {
        return shipmentLineState;
    }

    public void setShipmentLineState(ShipmentLineState shipmentLineState) {
        this.shipmentLineState = shipmentLineState;
    }

    public List<Pick> getPicks() {
        return picks;
    }

    public void setPicks(List<Pick> picks) {
        this.picks = picks;
    }

    public List<ShortAllocation> getShortAllocation() {
        return shortAllocation;
    }

    public void setShortAllocation(List<ShortAllocation> shortAllocation) {
        this.shortAllocation = shortAllocation;
    }

    public Integer getInprocessQuantity() {
        return inprocessQuantity;
    }

    public void setInprocessQuantity(Integer inprocessQuantity) {
        this.inprocessQuantity = inprocessQuantity;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }
}
