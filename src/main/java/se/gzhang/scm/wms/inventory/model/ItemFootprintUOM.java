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
import se.gzhang.scm.wms.common.model.UnitOfMeasure;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "item_footprint_uom")
@JsonSerialize(using = ItemFootprintUOMSerializer.class)
public class ItemFootprintUOM implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_footprint_uom_id")
    private Integer id;

    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="unit_of_measure_id")
    private UnitOfMeasure unitOfMeasure;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_footprint_id")
    private ItemFootprint itemFootprint;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "length")
    private Double length;
    @Column(name = "width")
    private Double width;
    @Column(name = "height")
    private Double height;
    @Column(name = "weight")
    private Double weight;

    @Column(name = "stock_flag")
    private Boolean stockUOM = false;
    @Column(name = "case_flag")
    private Boolean caseUOM = false;
    @Column(name = "pallet_flag")
    private Boolean palletUOM = false;


    @Column(name = "carton_flag")
    private Boolean cartonUOM = false;

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof ItemFootprintUOM) {
            ItemFootprintUOM anotherItemFootprintUOM = (ItemFootprintUOM)anObject;
            if (anotherItemFootprintUOM.getId() != null &&
                    getId() != null &&
                    anotherItemFootprintUOM.getId().equals(getId())) {
                return true;
            }
            if (anotherItemFootprintUOM.getItemFootprint().equals(getItemFootprint()) &&
                    anotherItemFootprintUOM.getUnitOfMeasure().equals(getUnitOfMeasure())) {
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

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public ItemFootprint getItemFootprint() {
        return itemFootprint;
    }

    public void setItemFootprint(ItemFootprint itemFootprint) {
        this.itemFootprint = itemFootprint;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isStockUOM() {
        return stockUOM;
    }

    public void setStockUOM(boolean stockUOM) {
        this.stockUOM = stockUOM;
    }

    public boolean isCaseUOM() {
        return caseUOM;
    }

    public void setCaseUOM(boolean caseUOM) {
        this.caseUOM = caseUOM;
    }

    public boolean isPalletUOM() {
        return palletUOM;
    }

    public void setPalletUOM(boolean palletUOM) {
        this.palletUOM = palletUOM;
    }

    public boolean isCartonUOM() {
        return cartonUOM;
    }

    public void setCartonUOM(boolean cartonUOM) {
        this.cartonUOM = cartonUOM;
    }
}
