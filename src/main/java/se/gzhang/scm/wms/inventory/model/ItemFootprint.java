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

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "item_footprint")
@JsonSerialize(using = ItemFootprintSerializer.class)
public class ItemFootprint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_footprint_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(
            mappedBy = "itemFootprint",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("quantity asc")
    private List<ItemFootprintUOM> itemFootprintUOMs = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "item_id", insertable = false, updatable = false)
    @JoinColumn(name = "item_id")
    private Item item;

    // Mark whether the item footprint is the default footprint
    // for this item
    @Column(name = "default_footprint")
    private boolean defaultFootprint;

}
