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

package se.gzhang.scm.wms.outbound.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.outbound.order.model.*;
import se.gzhang.scm.wms.outbound.shipment.service.AllocationResultService;
import se.gzhang.scm.wms.outbound.shipment.service.PickService;
import se.gzhang.scm.wms.outbound.shipment.service.ShortAllocationService;

@Service
public class AllocationStrategyService {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    PickService pickService;
    @Autowired
    ShortAllocationService shortAllocationService;
    @Autowired
    AllocationResultService allocationResultService;

    public AllocationStrategy getAllocationStrategy(AllocationStrategyType allocationStrategyType) {
        switch (allocationStrategyType){
            case FIFO:
                return new FIFOAllocationStrategy(inventoryService, pickService, shortAllocationService, allocationResultService);
            case FEFO:
                return new FEFOAllocationStrategy(inventoryService, pickService, shortAllocationService, allocationResultService);
            case AREA_LIST:
                return new AreaListAllocationStrategy(inventoryService, pickService, shortAllocationService, allocationResultService);
            case EMPTY_LOCATION:
                return new EmptyLocationAllocationStrategy(inventoryService, pickService, shortAllocationService, allocationResultService);
            default:
                return new FIFOAllocationStrategy(inventoryService, pickService, shortAllocationService, allocationResultService);
        }
    }




}
