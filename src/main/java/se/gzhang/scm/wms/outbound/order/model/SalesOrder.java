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
import se.gzhang.scm.wms.common.model.Customer;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.outbound.shipment.model.ShippingMethod;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_order")
@JsonSerialize(using = SalesOrderSerializer.class)
public class SalesOrder implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_order_id")
    private Integer id;

    @Column(name = "external_id",unique=true)
    private String externalID;

    @Column(name = "number",unique=true)
    private String number;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ship_to_customer", referencedColumnName="customer_id")
    private Customer shipToCustomer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_to_customer", referencedColumnName="customer_id")
    private Customer billToCustomer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "shipping_method")
    private ShippingMethod shippingMethod;

    @OneToMany(
            mappedBy = "salesOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SalesOrderLine> salesOrderLines = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SalesOrder)) {
            return false;
        }

        // If both receipt has ID, make sure the ID are the same
        // Otherwise, the receipts are same as long as the receipt numbers
        // are the same
        SalesOrder salesOrder = (SalesOrder)obj;
        if (this.id != null && salesOrder.getId() != null) {
            return this.id == salesOrder.getId();
        }
        else {
            return this.getNumber().equals(salesOrder.getNumber());
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Customer getShipToCustomer() {
        return shipToCustomer;
    }

    public void setShipToCustomer(Customer shipToCustomer) {
        this.shipToCustomer = shipToCustomer;
    }

    public Customer getBillToCustomer() {
        return billToCustomer;
    }

    public void setBillToCustomer(Customer billToCustomer) {
        this.billToCustomer = billToCustomer;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<SalesOrderLine> getSalesOrderLines() {
        return salesOrderLines;
    }

    public void setSalesOrderLines(List<SalesOrderLine> salesOrderLines) {
        this.salesOrderLines = salesOrderLines;
    }

    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
}
