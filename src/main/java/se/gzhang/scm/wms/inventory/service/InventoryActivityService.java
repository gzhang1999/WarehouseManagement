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

package se.gzhang.scm.wms.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.InventoryActivity;
import se.gzhang.scm.wms.inventory.model.InventoryActivityType;
import se.gzhang.scm.wms.inventory.repository.InventoryActivityRepository;

import java.util.Date;
import java.util.List;

@Service
public class InventoryActivityService {
    @Autowired
    InventoryActivityRepository inventoryActivityRepository;

    public List<InventoryActivity> findAll() {
        return inventoryActivityRepository.findAll();
    }

    public InventoryActivity findByInventoryActivityID(int inventoryActivityID) {
        return inventoryActivityRepository.findById(inventoryActivityID);
    }

    @Transactional
    public InventoryActivity save(InventoryActivity inventoryActivity) {

        return inventoryActivityRepository.save(inventoryActivity);
    }

    @Transactional
    public InventoryActivity logInventoryActivity(String originalLPN,
                                                  String newLPN,
                                                  Integer originalQuantity,
                                                  Integer newQuantity,
                                                  String originalInventoryStatus,
                                                  String newInventoryStatus,
                                                  InventoryActivityType inventoryActivityType,
                                                  User user,
                                                  String reasonCode,
                                                  String reason) {
        InventoryActivity inventoryActivity = new InventoryActivity();
        inventoryActivity.setUsername(user.getUsername());
        Date activityDate = new Date();
        inventoryActivity.setActivityDate(activityDate);
        inventoryActivity.setOriginalLPN(originalLPN);
        inventoryActivity.setNewLPN(newLPN);
        inventoryActivity.setOriginalQuantity(originalQuantity);
        inventoryActivity.setNewQuantity(newQuantity);
        inventoryActivity.setOriginalInventoryStatus(originalInventoryStatus);
        inventoryActivity.setNewInventoryStatus(newInventoryStatus);
        inventoryActivity.setInventoryActivityType(inventoryActivityType.toString());
        inventoryActivity.setReasonCode(reasonCode);
        inventoryActivity.setReason(reason);


        return save(inventoryActivity);
    }

    @Transactional
    public InventoryActivity logInventoryAdjustActivity(Inventory inventory,
                                                        int newQuantity,
                                                        User user,
                                                        String reasonCode,
                                                        String reason){
        return logInventoryActivity(inventory.getLpn(),
                inventory.getLpn(),
                inventory.getQuantity(),
                newQuantity,
                inventory.getInventoryStatus().getName(),
                inventory.getInventoryStatus().getName(),
                InventoryActivityType.INVENTORY_QUANTITY_ADJUSTMENT,
                user,reasonCode,reason);
    }
    @Transactional
    public InventoryActivity logCreateInventoryActivity(Inventory inventory,
                                                        User user,
                                                        String reasonCode,
                                                        String reason){
        return logInventoryActivity(inventory.getLpn(),
                inventory.getLpn(),
                inventory.getQuantity(),
                inventory.getQuantity(),
                inventory.getInventoryStatus().getName(),
                inventory.getInventoryStatus().getName(),
                InventoryActivityType.CREATE_INVENTORY,
                user,reasonCode,reason);
    }

    @Transactional
    public InventoryActivity logDeleteInventoryActivity(Inventory inventory,
                                                        User user,
                                                        String reasonCode,
                                                        String reason){
        return logInventoryActivity(inventory.getLpn(),
                inventory.getLpn(),
                inventory.getQuantity(),
                inventory.getQuantity(),
                inventory.getInventoryStatus().getName(),
                inventory.getInventoryStatus().getName(),
                InventoryActivityType.DELETE_INVENTORY,
                user,reasonCode,reason);
    }

}
