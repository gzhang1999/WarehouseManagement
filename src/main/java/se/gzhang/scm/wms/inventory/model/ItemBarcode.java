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

import javax.persistence.*;

@Entity
@Table(name = "item_barcode")
public class ItemBarcode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_barcode_id")
    private Integer id;


    @Column(name = "barcode")
    private String barcode;

    @OneToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="item_barcode_type_id")
    private ItemBarcodeType itemBarcodeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ItemBarcodeType getItemBarcodeType() {
        return itemBarcodeType;
    }

    public void setItemBarcodeType(ItemBarcodeType itemBarcodeType) {
        this.itemBarcodeType = itemBarcodeType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
