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

package se.gzhang.scm.wms.inbound.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "receipt_line")
@JsonSerialize(using = ReceiptLineSerializer.class)
public class ReceiptLine implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "receipt_line_id")
    private Integer id;

    @Column(name = "external_id",unique=true)
    private String externalID;

    @Column(name = "line_number")
    private String lineNumber;

    @Column(name = "expected_quantity")
    private Integer expectedQuantity;

    @Column(name = "received_quantity")
    private Integer receivedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="inventory_status_id")
    private InventoryStatus inventoryStatus;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ReceiptLine)) {
            return false;
        }

        // If both receipt line has ID, make sure the ID are the same
        // Otherwise, the receipts are same as long as the receipt numbers
        // are the same
        ReceiptLine receiptLine = (ReceiptLine)obj;
        if (this.id != null && receiptLine.getId() != null) {
            return this.id == receiptLine.getId();
        }
        else {
            return this.getLineNumber().equals(receiptLine.getLineNumber());
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

    public Integer getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(Integer expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }
}
