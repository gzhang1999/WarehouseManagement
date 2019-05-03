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

package se.gzhang.scm.wms.outbound.order.model;

import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.outbound.order.service.AllocationStrategyService;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.service.AllocationResultService;
import se.gzhang.scm.wms.outbound.shipment.service.PickService;
import se.gzhang.scm.wms.outbound.shipment.service.ShortAllocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AreaListAllocationStrategy extends DefaultAllocationStrategy {

    public AreaListAllocationStrategy(InventoryService inventoryService,
                                      PickService pickService,
                                      ShortAllocationService shortAllocationService,
                                      AllocationResultService allocationResultService) {
        super(inventoryService, pickService, shortAllocationService, allocationResultService);
    }


    @Override
    public void allocate(ShipmentLine shipmentLine, List<Inventory> availableInventory){

    }

    protected List<Location> sortLocationList(Map<Location, Integer> locationList,
                                                       List<Inventory> availableInventory){
        return new ArrayList<>(locationList.keySet());
    }
}
