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

package se.gzhang.scm.wms.inbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.gzhang.scm.wms.inbound.model.*;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.AreaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PutawayFlowHandler {

    @Autowired
    PutawayPolicyService putawayPolicyService;
    @Autowired
    AreaService areaService;
    @Autowired
    InventoryService inventoryService;

    public PutawayFlowModel init(int putawayPolicyId) {

        PutawayPolicy putawayPolicy = putawayPolicyService.findByPutawayPolicyId(putawayPolicyId);
        PutawayFlowModel putawayFlowModel = new PutawayFlowModel(putawayPolicy);
        loadMatchedInventory(putawayFlowModel);

        return putawayFlowModel;
    }

    private void loadMatchedInventory(PutawayFlowModel putawayFlowModel) {

        // Get all the inventory that is still in receiving stage
        List<Area> receivingStageAreaList = areaService.findReceingStageAreas();
        List<Inventory> receivedInventory = new ArrayList<>();
        for(Area area : receivingStageAreaList) {
            Map<String, String> inventoryCriteriaMap = new HashMap<>();
            inventoryCriteriaMap.put("areaID", String.valueOf(area.getId()));
            List<Inventory> inventoryList = inventoryService.findInventory(inventoryCriteriaMap);
            receivedInventory.addAll(inventoryList);
        }

        // after we get all the inventory, let's run the putaway policy for inventory that
        // 1. has a LPN
        // 2. doesn't have the destination location yet
        PutawayPolicy putawayPolicy = putawayFlowModel.getPutawayPolicy();
        for(Inventory inventory : receivedInventory) {
            if(inventory.getLpn() != "" && inventory.getDestinationLocation() == null) {
                Location destination = putawayPolicyService.getPutawayDestination(putawayPolicy, inventory);
                if (destination != null) {
                    inventory.setSuggestedDestinationLocation(destination);
                    // We will only reserve the location after the user click 'putaway' button
                    // locationService.reserveLocation(destination, inventoryService.getSize(inventory));
                }
            }
        }
        putawayFlowModel.setInventoryList(receivedInventory);

    }

}
