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
import se.gzhang.scm.wms.authorization.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "carton_audit_result")
@JsonSerialize(using = CartonAuditResultSerializer.class)
public class CartonAuditResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carton_audit_result_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="carton_id")
    private Carton carton;

    @OneToMany(
            mappedBy = "cartonAuditResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartonAuditResultLine> cartonAuditResultLines = new ArrayList<>();

    @Column(name = "audit_date")
    private Date auditDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User auditUser;

    @Column(name = "carton_audit_state")
    private CartonAuditState cartonAuditState;

    public CartonAuditResult(Carton carton) {
        auditDate = new Date();
        cartonAuditState = CartonAuditState.NEW;
        this.carton = carton;
    }
    public CartonAuditResult(Carton carton, User auditUser) {
        this(carton);
        this.auditUser = auditUser;
    }

    public void addCartonAuditResultLine(CartonAuditResultLine cartonAuditResultLine) {
        if (cartonAuditResultLines == null ) {
            cartonAuditResultLines = new ArrayList<>();
        }
        cartonAuditResultLines.add(cartonAuditResultLine);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Carton getCarton() {
        return carton;
    }

    public void setCarton(Carton carton) {
        this.carton = carton;
    }

    public List<CartonAuditResultLine> getCartonAuditResultLines() {
        return cartonAuditResultLines;
    }

    public void setCartonAuditResultLines(List<CartonAuditResultLine> cartonAuditResultLines) {
        this.cartonAuditResultLines = cartonAuditResultLines;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public User getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(User auditUser) {
        this.auditUser = auditUser;
    }

    public CartonAuditState getCartonAuditState() {
        return cartonAuditState;
    }

    public void setCartonAuditState(CartonAuditState cartonAuditState) {
        this.cartonAuditState = cartonAuditState;
    }
}
