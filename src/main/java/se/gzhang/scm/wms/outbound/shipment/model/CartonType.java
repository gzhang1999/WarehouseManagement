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
import se.gzhang.scm.wms.layout.model.Warehouse;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "carton_type")
@JsonSerialize(using = CartonTypeSerializer.class)
public class CartonType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carton_type_id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "length", nullable = false)
    private Double length;
    @Column(name = "width", nullable = false)
    private Double width;
    @Column(name = "height", nullable = false)
    private Double height;
    @Column(name = "fillRate", nullable = false)
    private Double fillRate;
    @Column(name = "weight", nullable = false)
    private Double weight;
    @Column(name = "weightCapacity", nullable = false)
    private Double weightCapacity;
    @Column(name = "cost", nullable = false)
    private Double cost;
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    public double getVolume() {
        return getLength() * getWidth() * getHeight();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CartonType)) {
            return false;
        }

        // If both receipt has ID, make sure the ID are the same
        // Otherwise, the receipts are same as long as the receipt numbers
        // are the same
        CartonType cartonType = (CartonType)obj;
        if (this.id != null && cartonType.getId() != null) {
            return this.id == cartonType.getId();
        }
        else {
            return this.getName().equals(cartonType.getName());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getFillRate() {
        return fillRate;
    }

    public void setFillRate(Double fillRate) {
        this.fillRate = fillRate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeightCapacity() {
        return weightCapacity;
    }

    public void setWeightCapacity(Double weightCapacity) {
        this.weightCapacity = weightCapacity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
