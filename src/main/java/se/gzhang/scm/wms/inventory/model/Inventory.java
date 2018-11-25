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
import lombok.Data;
import se.gzhang.scm.wms.common.model.UnitOfMeasure;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;

import javax.persistence.*;

@Data
@Entity
@Table(name = "inventory")
@JsonSerialize(using = InventorySerializer.class)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inventory_id")
    private Integer id;

    @OneToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="item_footprint_id")
    private ItemFootprint itemFootprint;


    @Column(name="lpn")
    private String lpn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name="quantity")
    private int quantity;

    @OneToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="inventory_status_id")
    private InventoryStatus inventoryStatus;


}
