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
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.layout.model.Warehouse;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipt")
@JsonSerialize(using = ReceiptSerializer.class)
public class Receipt  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "receipt_id")
    private Integer id;

    @Column(name = "external_id",unique=true)
    private String externalID;

    @Column(name = "number",unique=true)
    private String number;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="supplier_id")
    private Supplier supplier;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;


    @OneToMany(
            mappedBy = "receipt",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("line_number asc")
    private List<ReceiptLine> receiptLineList = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trailer_id")
    private Trailer trailer;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Receipt)) {
            return false;
        }

        // If both receipt has ID, make sure the ID are the same
        // Otherwise, the receipts are same as long as the receipt numbers
        // are the same
        Receipt receipt = (Receipt)obj;
        if (this.id != null && receipt.getId() != null) {
            return this.id == receipt.getId();
        }
        else {
            return this.getNumber().equals(receipt.getNumber());
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

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<ReceiptLine> getReceiptLineList() {
        return receiptLineList;
    }

    public void setReceiptLineList(List<ReceiptLine> receiptLineList) {
        this.receiptLineList = receiptLineList;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

}
