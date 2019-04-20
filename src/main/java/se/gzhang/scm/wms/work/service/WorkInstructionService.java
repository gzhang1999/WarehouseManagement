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

package se.gzhang.scm.wms.work.service;

import org.hibernate.jdbc.Work;
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
import se.gzhang.scm.wms.work.model.WorkInstructionStatus;
import se.gzhang.scm.wms.work.repository.WorkInstructionRepository;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class WorkInstructionService {

    @Autowired
    WorkInstructionRepository workInstructionRepository;

    @Autowired
    InventoryService inventoryService;

    public List<WorkInstruction> findAll(){

        return workInstructionRepository.findAll();
    }

    public WorkInstruction findByWorkInstructionId(int id){
        return workInstructionRepository.findById(id);
    }


    public WorkInstruction save(WorkInstruction workInstruction) {
        WorkInstruction newWorkInstruction = workInstructionRepository.save(workInstruction);
        workInstructionRepository.flush();
        return newWorkInstruction;
    }


    public void deleteByWorkInstructionID(int workInstructionID) {
        workInstructionRepository.deleteById(workInstructionID);
    }

    public WorkInstruction generatePutawayWorkInstruction(Inventory inventory, Location destinationLocation) {
        WorkInstruction workInstruction = new WorkInstruction();
        workInstruction.setInventory(inventory);
        workInstruction.setSourceLocation(inventory.getLocation());
        workInstruction.setDestinationLocation(destinationLocation);

        inventory.setDestinationLocation(destinationLocation);
        inventory.setSuggestedDestinationLocation(null);
        inventoryService.save(inventory);
        return save(workInstruction);
    }

    public WorkInstruction completeWorkInstruction(WorkInstruction workInstruction) {
        switch (workInstruction.getWorkInstructionType()) {
            case PUTAWAY:
                completePutaway(workInstruction);
                break;
            case PICK:
                completePick(workInstruction);
                break;
            case REPLENISHMENT:
                completeReplenishment(workInstruction);
                break;
            default:
                break;
        }
        return workInstruction;
    }

    // Finish the putaway work, move inventory to the destination
    private WorkInstruction completePutaway(WorkInstruction workInstruction) {
        inventoryService.moveInventory(workInstruction.getInventory(), workInstruction.getDestinationLocation());
        workInstruction.setWorkInstructionStatus(WorkInstructionStatus.COMPLETE);
        return workInstruction;


    }
    public WorkInstruction completePick(WorkInstruction workInstruction) {
        return workInstruction;

    }
    public WorkInstruction completeReplenishment(WorkInstruction workInstruction) {
        return  workInstruction;

    }

}
