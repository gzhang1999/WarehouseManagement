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

package se.gzhang.scm.wms.outbound.order.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_order_line")
@JsonSerialize(using = SalesOrderLineSerializer.class)
public class SalesOrderLine implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_order_line_id")
    private Integer id;

    @Column(name = "external_id",unique=true)
    private String externalID;

    @Column(name = "line_number")
    private String lineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    // ordered quantity
    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="inventory_status_id")
    private InventoryStatus inventoryStatus;

    @Transient
    List<ShipmentLine> shipmentLineList;


    @OneToMany(
            mappedBy = "salesOrderLine",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SalesOrderLineAllocationStrategy> salesOrderLineAllocationStrategyArrayList = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SalesOrderLine)) {
            return false;
        }

        // If both receipt has ID, make sure the ID are the same
        // Otherwise, the receipts are same as long as the receipt numbers
        // are the same
        SalesOrderLine salesOrderLine = (SalesOrderLine)obj;
        if (this.id != null && salesOrderLine.getId() != null) {
            return this.id == salesOrderLine.getId();
        }
        else {
            return this.getLineNumber().equals(salesOrderLine.getLineNumber());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<SalesOrderLineAllocationStrategy> getSalesOrderLineAllocationStrategyArrayList() {
        return salesOrderLineAllocationStrategyArrayList;
    }

    public void setSalesOrderLineAllocationStrategyArrayList(List<SalesOrderLineAllocationStrategy> salesOrderLineAllocationStrategyArrayList) {
        this.salesOrderLineAllocationStrategyArrayList = salesOrderLineAllocationStrategyArrayList;
    }

    public List<ShipmentLine> getShipmentLineList() {
        return shipmentLineList;
    }

    public void setShipmentLineList(List<ShipmentLine> shipmentLineList) {
        this.shipmentLineList = shipmentLineList;
    }
}
