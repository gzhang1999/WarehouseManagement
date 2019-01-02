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

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "inventory_activity")
public class InventoryActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inventory_activity_id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "activity_date")
    private Date activityDate;

    @Column(name = "original_LPN")
    private String originalLPN;

    @Column(name = "new_LPN")
    private String newLPN;

    @Column(name = "original_quantity")
    private Integer originalQuantity;
    @Column(name = "new_quantity")
    private Integer newQuantity;

    @Column(name = "original_inventory_status")
    private String originalInventoryStatus;
    @Column(name = "new_inventory_status")
    private String newInventoryStatus;

    @Column(name = "inventory_activity_type")
    private String inventoryActivityType;

    @Column(name = "reason_code")
    private String reasonCode;
    @Column(name = "reason")
    private String reason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public String getOriginalLPN() {
        return originalLPN;
    }

    public void setOriginalLPN(String originalLPN) {
        this.originalLPN = originalLPN;
    }

    public String getNewLPN() {
        return newLPN;
    }

    public void setNewLPN(String newLPN) {
        this.newLPN = newLPN;
    }

    public Integer getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(Integer originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getOriginalInventoryStatus() {
        return originalInventoryStatus;
    }

    public void setOriginalInventoryStatus(String originalInventoryStatus) {
        this.originalInventoryStatus = originalInventoryStatus;
    }

    public String getNewInventoryStatus() {
        return newInventoryStatus;
    }

    public void setNewInventoryStatus(String newInventoryStatus) {
        this.newInventoryStatus = newInventoryStatus;
    }

    public String getInventoryActivityType() {
        return inventoryActivityType;
    }

    public void setInventoryActivityType(String inventoryActivityType) {
        this.inventoryActivityType = inventoryActivityType;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
