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
import se.gzhang.scm.wms.inventory.model.Item;

import javax.persistence.*;

@Entity
@Table(name = "carton_audit_result_line")
@JsonSerialize(using = CartonAuditResultLineSerializer.class)
public class CartonAuditResultLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carton_audit_result_line_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carton_audit_result_id")
    private CartonAuditResult cartonAuditResult;

    @Column(name = "expected_quantity")
    private Integer expectedQuantity;

    @Column(name = "audit_quantity")
    private Integer auditQuantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_id")
    private Item item;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CartonAuditResult getCartonAuditResult() {
        return cartonAuditResult;
    }

    public void setCartonAuditResult(CartonAuditResult cartonAuditResult) {
        this.cartonAuditResult = cartonAuditResult;
    }

    public Integer getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(Integer expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public Integer getAuditQuantity() {
        return auditQuantity;
    }

    public void setAuditQuantity(Integer auditQuantity) {
        this.auditQuantity = auditQuantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
