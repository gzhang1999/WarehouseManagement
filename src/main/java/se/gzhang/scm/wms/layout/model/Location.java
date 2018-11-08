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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import se.gzhang.scm.wms.common.model.VehicleType;
import se.gzhang.scm.wms.common.model.Velocity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "location")
@JsonSerialize(using = LocationSerializer.class)
public class Location  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @Column(name = "aisle_id")
    private String aisleID;

    @Column(name = "pickable")
    private boolean pickable;

    @Column(name = "storable")
    private boolean storable;

    @Column(name = "usable")
    private boolean usable;


    @Column(name = "length")
    private double length;
    @Column(name = "width")
    private double width;
    @Column(name = "height")
    private double height;
    @Column(name = "volume")
    private double volume;

    // Velocity of the location
    @OneToOne(cascade={CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name="velocity_id")
    private Velocity velocity;

    // which type of vehicle can access this location
    // for example, higher locations can only be accessed by
    // forklift truck while lower locations can be accessed by
    // pallet jet with handheld

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<VehicleType> accessibleVehicleTypes;


    // Coordinate used for labor management
    @Column(name = "coordinate_x")
    private double coordinateX;
    @Column(name = "coordinate_y")
    private double coordinateY;
    @Column(name = "coordinate_z")
    private double coordinateZ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location )) return false;
        return id != null && id.equals(((Location) o).id);
    }

}
