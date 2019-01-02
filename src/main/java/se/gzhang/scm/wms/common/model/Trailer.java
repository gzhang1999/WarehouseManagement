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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "trailer")
@JsonSerialize(using = TrailerSerializer.class)
public class Trailer implements Serializable {

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


    @ManyToOne(fetch = FetchType.EAGER)
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    public TrailerType getTrailerType() {
        return trailerType;
    }

    public void setTrailerType(TrailerType trailerType) {
        this.trailerType = trailerType;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public void setTrailerNumber(String trailerNumber) {
        this.trailerNumber = trailerNumber;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriverTelephone() {
        return driverTelephone;
    }

    public void setDriverTelephone(String driverTelephone) {
        this.driverTelephone = driverTelephone;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Date getCheckedInDate() {
        return checkedInDate;
    }

    public void setCheckedInDate(Date checkedInDate) {
        this.checkedInDate = checkedInDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public Date getDispatchedDate() {
        return dispatchedDate;
    }

    public void setDispatchedDate(Date dispatchedDate) {
        this.dispatchedDate = dispatchedDate;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public TrailerState getTrailerState() {
        return trailerState;
    }

    public void setTrailerState(TrailerState trailerState) {
        this.trailerState = trailerState;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
