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

package se.gzhang.scm.wms.common.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.Warehouse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "trailer")
@JsonSerialize(using = TrailerSerializer.class)
public class Trailer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "trailer_id")
    private Integer id;

    @OneToMany(
            mappedBy = "trailer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("external_id asc")
    private List<Receipt> receiptList = new ArrayList<>();

    @Column(name = "trailer_type")
    private TrailerType trailerType;


    @Column(name = "trailer_number")
    private String trailerNumber;

    @Column(name = "driver")
    private String driver;

    @Column(name = "driver_telephone")
    private String driverTelephone;

    @Column(name = "license_plate")
    private String licensePlate;

    // Date to capture important activities
    @Column(name = "checked_in_date")
    private Date checkedInDate;
    @Column(name = "closed_date")
    private Date closedDate;
    @Column(name = "dispatched_date")
    private Date dispatchedDate;


    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="carrier_id")
    private Carrier carrier;

    @Column(name = "trailer_state")
    private TrailerState trailerState;

    // Yard Location
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
}
