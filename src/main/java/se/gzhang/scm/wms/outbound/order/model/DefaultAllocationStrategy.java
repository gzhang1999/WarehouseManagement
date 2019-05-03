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

import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.outbound.order.service.AllocationStrategyService;
import se.gzhang.scm.wms.outbound.shipment.model.AllocationResult;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.service.AllocationResultService;
import se.gzhang.scm.wms.outbound.shipment.service.PickService;
import se.gzhang.scm.wms.outbound.shipment.service.ShortAllocationService;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DefaultAllocationStrategy implements AllocationStrategy {

    InventoryService inventoryService;
    PickService pickService;
    ShortAllocationService shortAllocationService;
    AllocationResultService allocationResultService;

    public DefaultAllocationStrategy(InventoryService inventoryService,
                                     PickService pickService,
                                     ShortAllocationService shortAllocationService,
                                     AllocationResultService allocationResultService) {
        this.inventoryService = inventoryService;
        this.pickService = pickService;
        this.shortAllocationService = shortAllocationService;
        this.allocationResultService = allocationResultService;
    }

    @Transactional
    protected void allocateInventory(ShipmentLine shipmentLine, Location location, int allocatedQuantity) {
        allocationResultService.allocateInventoryFromLocation(shipmentLine, location, allocatedQuantity);
        // create a pick from the location
        pickService.generatePickFromShipmentLineAllocation(shipmentLine, location, allocatedQuantity);

    }

    // WE will allocate based on location. So we will get the
    // total available quantity based on the location
    protected Map<Location, Integer> getAvailableInventoryByLocation(ShipmentLine shipmentLine, List<Inventory> availableInventory) {
        // Let's check the inventory's status of the order line
        InventoryStatus orderInventoryStatus = shipmentLine.getSalesOrderLine().getInventoryStatus();

        // Load all the quantity that is already allocated
        Map<String, String> criteria = new HashMap<>();
        criteria.put("item", shipmentLine.getSalesOrderLine().getItem().getName());
        criteria.put("inventoryStatus", shipmentLine.getSalesOrderLine().getInventoryStatus().getName());
        List<AllocationResult> allocationResultList = allocationResultService.findAllocationResults(criteria);

        // Loop through all the inventory and generate the location structure
        Map<Location, Integer> availableInventorySummaryByLocation = new HashMap<>();
        for(Inventory inventory : availableInventory) {
            // Inventory status has to be match with order's inventory status
            if (!inventory.getInventoryStatus().equals(orderInventoryStatus)) {
                continue;
            }
            Location location = inventory.getLocation();
            int availableQuantity = 0;
            if (availableInventorySummaryByLocation.containsKey(location)) {
                availableQuantity = availableInventorySummaryByLocation.get(location);
            }
            availableQuantity += inventory.getQuantity();
            availableInventorySummaryByLocation.put(location, availableQuantity);
        }
        // after we add the inventory, minus the quantity that already allocated
        for(AllocationResult allocationResult : allocationResultList) {
            Location location = allocationResult.getLocation();
            if (availableInventorySummaryByLocation.containsKey(location)) {
                int availableQuantity = availableInventorySummaryByLocation.get(location);
                availableQuantity -= allocationResult.getAllocatedQuantity();
                availableInventorySummaryByLocation.put(location,availableQuantity);
            }
        }
        return availableInventorySummaryByLocation;
    }

    protected abstract List<Location> sortLocationList(Map<Location, Integer> locationList,
                                                               List<Inventory> availableInventory);
}
