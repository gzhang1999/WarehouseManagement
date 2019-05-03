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
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.service.AllocationResultService;
import se.gzhang.scm.wms.outbound.shipment.service.PickService;
import se.gzhang.scm.wms.outbound.shipment.service.ShortAllocationService;


import java.util.*;

public class FIFOAllocationStrategy extends DefaultAllocationStrategy {

    public FIFOAllocationStrategy(InventoryService inventoryService,
                                     PickService pickService,
                                     ShortAllocationService shortAllocationService,
                                  AllocationResultService allocationResultService) {
        super(inventoryService, pickService, shortAllocationService, allocationResultService);
    }

    @Override
    @Transactional
    public void allocate(ShipmentLine shipmentLine, List<Inventory> availableInventory){
        // Only allocate the shipment has open quantity
        if (shipmentLine.getOrderQuantity() > (shipmentLine.getInprocessQuantity() + shipmentLine.getShippedQuantity())) {
            int openQuantity = shipmentLine.getOrderQuantity() - (shipmentLine.getInprocessQuantity() + shipmentLine.getShippedQuantity());

            Map<Location, Integer> availableInventoryByLocation = getAvailableInventoryByLocation(shipmentLine, availableInventory);

            List<Location> sortedLocationList = sortLocationList(availableInventoryByLocation, availableInventory);

            for(Location location : sortedLocationList) {
                 int availableQuantity = availableInventoryByLocation.get(location);
                 if (availableQuantity > 0) {
                     int allocatedQuantity = Math.min(availableQuantity, openQuantity);
                     allocateInventory(shipmentLine, location, allocatedQuantity);
                     openQuantity -= allocatedQuantity;
                 }

                 if (openQuantity <= 0) {
                     break;
                 }
            }

            // if we still have order quantity here, it means we have short allocation
            if (openQuantity > 0) {
                shortAllocationService.generateShortAllocationFromShipmentLineAllocation(shipmentLine, openQuantity);
            }

        }

    }

    @Override
    public List<Location> sortLocationList(Map<Location, Integer> locationList,
                                                   List<Inventory> availableInventory){

        // Sort the location by inventory's FIFO date
        // if the location has multiple inventory, we will consider the oldest date

        List<Location> sortedLocationList = new ArrayList<>(locationList.keySet());
        Collections.sort(sortedLocationList, new Comparator<Location>() {
            @Override
            public int compare(Location location1, Location location2) {
                return getFIFODate(location1).compareTo(getFIFODate(location2));
            }
            private Date getFIFODate(Location location) {
                Date fifoDate = new Date();
                for(Inventory inventory : availableInventory) {
                    if (inventory.getFIFODate() != null &&
                            inventory.getFIFODate().compareTo(fifoDate) < 0) {
                        fifoDate = inventory.getFIFODate();
                    }
                }
                return fifoDate;
            }
        });
        return sortedLocationList;
    }

}
