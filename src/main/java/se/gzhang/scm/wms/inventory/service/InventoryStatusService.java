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
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.configuration.service.PolicyService;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.InventoryActivity;
import se.gzhang.scm.wms.inventory.model.InventoryActivityType;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.repository.InventoryActivityRepository;
import se.gzhang.scm.wms.inventory.repository.InventoryStatusRepository;

import java.util.Date;
import java.util.List;

@Service
public class InventoryStatusService {
    @Autowired
    InventoryStatusRepository inventoryStatusRepository;

    @Autowired
    PolicyService policyService;

    public List<InventoryStatus> findAll() {
        return inventoryStatusRepository.findAll();
    }

    public InventoryStatus findByInventoryStatusID(int inventoryStatusID) {
        return inventoryStatusRepository.findById(inventoryStatusID);
    }

    public InventoryStatus findByInventoryStatusName(String inventoryStatusName) {
        return inventoryStatusRepository.findByName(inventoryStatusName);
    }

    public InventoryStatus save(InventoryStatus inventoryStatus) {

        InventoryStatus newInventoryStatus = inventoryStatusRepository.save(inventoryStatus);
        inventoryStatusRepository.flush();
        return newInventoryStatus;
    }

   public InventoryStatus getDefaultInventoryStatus() {
        return findByInventoryStatusName(policyService.findByPolicyName("inventory.status.defaultStatus").getValue());
   }

}
