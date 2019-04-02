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
import se.gzhang.scm.wms.common.model.Client;
import se.gzhang.scm.wms.common.model.UnitOfMeasure;
import se.gzhang.scm.wms.layout.model.Warehouse;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
@JsonSerialize(using = ItemSerializer.class)
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private Integer id;


    @Column(name = "name",unique=true)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name="warehouse_id")
    private Warehouse warehouse;

    @ManyToOne(optional = false)
    @JoinColumn(name="client_id")
    private Client client;


    // At which level we will track the LPN.
    // For example, do we need a LPN for each pallet
    // or each box, or every 'Each'
    @OneToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="lpn_uom_id")
    private UnitOfMeasure lpnUnitOfMeasure;

    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemFootprint> itemFootprints = new ArrayList<>();


    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemBarcode> itemBarcodes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_family_id")
    private ItemFamily itemFamily;

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Item) {
            Item anotherItem = (Item)anObject;
            if (anotherItem.getId() != null &&
                    getId() != null &&
                    anotherItem.getId().equals(getId())) {
                return true;
            }
            if (anotherItem.getName().equals(getName()) &&
                    anotherItem.getWarehouse().getId().equals(getWarehouse().getId())) {
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public UnitOfMeasure getLpnUnitOfMeasure() {
        return lpnUnitOfMeasure;
    }

    public void setLpnUnitOfMeasure(UnitOfMeasure lpnUnitOfMeasure) {
        this.lpnUnitOfMeasure = lpnUnitOfMeasure;
    }

    public List<ItemFootprint> getItemFootprints() {
        return itemFootprints;
    }

    public void setItemFootprints(List<ItemFootprint> itemFootprints) {
        this.itemFootprints = itemFootprints;
    }

    public List<ItemBarcode> getItemBarcodes() {
        return itemBarcodes;
    }

    public void setItemBarcodes(List<ItemBarcode> itemBarcodes) {
        this.itemBarcodes = itemBarcodes;
    }

    public ItemFamily getItemFamily() {
        return itemFamily;
    }

    public void setItemFamily(ItemFamily itemFamily) {
        this.itemFamily = itemFamily;
    }
}
