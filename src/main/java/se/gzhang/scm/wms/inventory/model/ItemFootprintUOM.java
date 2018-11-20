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
import javax.persistence.*;

@Data
@Entity
@Table(name = "item_footprint_uom")
@JsonSerialize(using = ItemFootprintUOMSerializer.class)
public class ItemFootprintUOM {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_footprint_uom_id")
    private Integer id;

    @OneToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="unit_of_measure_id")
    private UnitOfMeasure unitOfMeasure;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_footprint_id")
    private ItemFootprint itemFootprint;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "length")
    private double length;
    @Column(name = "width")
    private double width;
    @Column(name = "height")
    private double height;
    @Column(name = "weight")
    private double weight;

    @Column(name = "stock_flag")
    private boolean stockUOM = false;
    @Column(name = "case_flag")
    private boolean caseUOM = false;
    @Column(name = "pallet_flag")
    private boolean palletUOM = false;


    @Column(name = "carton_flag")
    private boolean cartonUOM = false;



}
