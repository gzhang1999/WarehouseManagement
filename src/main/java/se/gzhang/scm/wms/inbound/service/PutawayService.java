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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.inbound.model.PutawayPolicy;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.repository.PutawayPolicyRepository;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemFamily;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.work.model.WorkInstruction;
import se.gzhang.scm.wms.work.service.WorkInstructionService;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class PutawayService {

    @Autowired
    PutawayPolicyService putawayPolicyService;

    @Autowired
    InventoryService inventoryService;
    @Autowired
    LocationService locationService;
    @Autowired
    WorkInstructionService workInstructionService;

    public Inventory generatePutawayWork(Inventory inventory) {
        List<PutawayPolicy> putawayPolicyList = putawayPolicyService.getMatchedPutawayPolicy(inventory);
        // go through each putaway policy until we can successfully
        // allocate the inventory to a location
        for(PutawayPolicy putawayPolicy : putawayPolicyList) {
            Location destinationLocation = putawayPolicyService.getPutawayDestination(putawayPolicy, inventory);
            if (destinationLocation != null) {
                // reserve the location and generate the work instruction
                locationService.allocateLocation(destinationLocation, inventory);
                workInstructionService.generatePutawayWorkInstruction(inventory, destinationLocation);
                break;
            }
        }
        return inventoryService.findByInventoryID(inventory.getId());
    }



}
