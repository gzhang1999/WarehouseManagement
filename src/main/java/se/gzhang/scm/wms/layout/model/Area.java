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

package se.gzhang.scm.wms.layout.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import se.gzhang.scm.wms.common.model.UnitOfMeasure;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "area")
@JsonSerialize(using = AreaSerializer.class)
public class Area implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "area_id")
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;


    @OneToMany(
            mappedBy = "area",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Location> locations = new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UnitOfMeasure> pickableUOMs = new ArrayList<>();

    @Column(name = "area_type")
    private AreaType areaType;

    // How we calculate the volume of the location in this area
    // 1. EACH: Volume is calculated by how many eaches we can
    //          put in the location. Normally feasible for static locations
    // 2. SIZE: Volume is calculated by the size of the location.
    //          Normally feasible for the dynamic locations.
    @Column(name = "volume_type")
    private VolumeType volumeType;

    @Column(name = "allow_consolidation")
    private Boolean allowConsolidation;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Area )) return false;
        return id != null && id.equals(((Area) o).id);
    }

}
