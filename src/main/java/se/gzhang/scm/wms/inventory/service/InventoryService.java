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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.configuration.service.PolicyService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.exception.Outbound.PickException;
import se.gzhang.scm.wms.exception.StandProductException;
import se.gzhang.scm.wms.exception.inventory.InventoryException;
import se.gzhang.scm.wms.exception.inventory.ItemException;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inventory.model.*;
import se.gzhang.scm.wms.inventory.repository.InventoryRepository;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.AreaType;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;
import se.gzhang.scm.wms.outbound.shipment.model.Carton;
import se.gzhang.scm.wms.outbound.shipment.model.Pick;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class InventoryService {
    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ItemService itemService;


    @Autowired
    ItemFootprintService itemFootprintService;

    @Autowired
    LocationService locationService;

    @Autowired
    AreaService areaService;

    @Autowired
    InventoryActivityService inventoryActivityService;

    @Autowired
    UserService userService;

    @Autowired
    InventoryStatusService inventoryStatusService;

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Inventory findByInventoryID(int inventoryID) {
        return inventoryRepository.findById(inventoryID);
    }

    @Transactional
    public Inventory adjustInventory(Inventory inventory, int newQuantity, String reasonCode, String reason) {
        // Save the record for adjusting inventory
        inventoryActivityService.logInventoryAdjustActivity(inventory,newQuantity, userService.getCurrentLoginUser(), reasonCode, reason);
        inventory.setQuantity(newQuantity);
        return save(inventory);
    }

    @Transactional
    public Inventory addInventory(Location location, ItemFootprint itemFootprint,
                                  int quantity, String itemFootprintUOM, String lpn,
                                  String reasonCode, String reason){

        int unitQuantity = quantity;
        for(ItemFootprintUOM itemFootprintUOMIterator : itemFootprint.getItemFootprintUOMs()) {
            if (itemFootprintUOMIterator.getUnitOfMeasure().getName().equals(itemFootprintUOM)) {
                unitQuantity = quantity * itemFootprintUOMIterator.getQuantity();
            }
        }

        Inventory inventory = new Inventory();
        inventory.setItemFootprint(itemFootprint);
        inventory.setLocation(location);
        inventory.setQuantity(unitQuantity);
        inventory.setLpn(lpn);
        inventory.setFIFODate(new Date());
        // TO-DO: Setup the inventory with default inventory status
        inventory.setInventoryStatus(inventoryStatusService.getDefaultInventoryStatus());

        Inventory newInventory = createInventory(inventory, reasonCode, reason);
        return newInventory;
    }

    @Transactional
    public Inventory createInventory(Inventory inventory, String reasonCode, String reason) {
        // Save the record for adjusting inventory
        inventoryActivityService.logCreateInventoryActivity(inventory, userService.getCurrentLoginUser(), reasonCode, reason);

        return save(inventory);

    }

    @Transactional
    public Inventory save(Inventory inventory) {

        return inventoryRepository.save(inventory);
    }

    @Transactional
    public void deleteInventory(int inventoryID, String reasonCode, String reason) throws GenericException{
        deleteInventory(findByInventoryID(inventoryID), reasonCode, reason);
    }

    @Transactional
    public void deleteInventory(Inventory inventory, String reasonCode, String reason) throws GenericException{
        // Save the record for adjusting inventory
        inventoryActivityService.logDeleteInventoryActivity(inventory, userService.getCurrentLoginUser(), reasonCode, reason);

        inventoryRepository.delete(inventory);
    }

    @Transactional
    public Inventory addInventory(String itemNumber, String footprintName, String lpn, int quantity, Location location) {

        Item item = itemService.findByItemName(itemNumber);
        if (item == null) {
            throw ItemException.NO_SUCH_ITEM;
        }

        ItemFootprint itemFootprint = itemFootprintService.findByItemFootprintName(footprintName);
        if (itemFootprint == null) {
            throw ItemException.NO_SUCH_ITEM;
        }

        Inventory inventory = new Inventory();
        inventory.setItemFootprint(itemFootprint);
        inventory.setLpn(lpn);
        inventory.setLocation(location);

        return save(inventory);

    }

    @Transactional
    public Inventory moveInventory(Inventory inventory, Location destinationLocation) {
        // check if the movement is allowed
        validateMovement(inventory, destinationLocation);

        Location sourceLocation = inventory.getLocation();
        inventory.setLocation(destinationLocation);

        // clear the supposed destination location after we actually move
        // the inventory into this location
        if (destinationLocation.equals(inventory.getDestinationLocation())) {
            locationService.deallocateLocation(destinationLocation, inventory);

        }
        // refresh the locaiton volume after the inventory movement.
        locationService.refreshLocationUsedVolume(sourceLocation);
        locationService.refreshLocationUsedVolume(destinationLocation);
        return save(inventory);
    }

    @Transactional
    // move inventory to the destination based on the pick
    public Inventory moveInventory(Inventory inventory, Pick pick) {

        // we will need to update the volume of the source location
        // and destination location
        Location sourceLocation = inventory.getLocation();
        Location destinationLocation = pick.getDestinationLocation();

        // move the inventory for this pick
        inventory.setLocation(destinationLocation);
        inventory.setPick(pick);

        // refresh the locaiton volume after the inventory movement.
        locationService.refreshLocationUsedVolume(sourceLocation);
        locationService.refreshLocationUsedVolume(destinationLocation);
        return save(inventory);
    }

    // Check if we can move the inventory to the destination
    private void validateMovement(Inventory inventory, Location destinationLocation) {
        // return true if we have no suggested destination
        // or the actual destination is the same as the suggested destination
        if (inventory.getDestinationLocation() == null ||
                inventory.getDestinationLocation().equals(destinationLocation)) {
            return;
        }
        // now we know we have a suggested destination but it is not the same as
        // the actual destination location. return true if the actual destination is
        // in a Pickup and Dopisit area
        if (destinationLocation.getArea().getAreaType().equals(AreaType.PICKUP_AND_DEPOSIT)) {
            return;
        }
        throw InventoryException.NOT_VALID_MOVEMENT;
    }

    public List<Inventory> findInventoryByLPN(String lpn) {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("lpn", lpn);
        return findInventory(criteria);
    }


    public List<Inventory> findInventory(Map<String, String> criteriaList) {
        if (criteriaList.containsKey("area_id")) {
            criteriaList.put("areaID", criteriaList.get("area_id"));
        }
        return inventoryRepository.findAll(new Specification<Inventory>() {
            @Override
            public Predicate toPredicate(Root<Inventory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(criteriaList.containsKey("buildingID") && !criteriaList.get("buildingID").isEmpty()) {
                    Join<Inventory, Location> joinLocation = root.join("location",JoinType.INNER);
                    Join<Location, Area> joinArea = joinLocation.join("area",JoinType.INNER);
                    Join<Area, Building> joinBuilding = joinArea.join("building",JoinType.INNER);

                    predicates.add(criteriaBuilder.equal(joinBuilding.get("id"), criteriaList.get("buildingID")));

                    // if area id is passed in, we will include it here so we don't have to have
                    // join area table twice if both area id and building id is passed in
                    if(criteriaList.containsKey("areaID") && !criteriaList.get("areaID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinArea.get("id"), criteriaList.get("areaID")));
                    }
                    if(criteriaList.containsKey("locationName") && !criteriaList.get("locationName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("name"), criteriaList.get("locationName")));
                    }
                    if(criteriaList.containsKey("aisleID") && !criteriaList.get("aisleID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("aisleID"), criteriaList.get("aisleID")));
                    }
                }
                else if (criteriaList.containsKey("areaID") && !criteriaList.get("areaID").isEmpty()) {
                    Join<Inventory, Location> joinLocation = root.join("location",JoinType.INNER);
                    Join<Location, Area> joinArea = joinLocation.join("area",JoinType.INNER);

                    predicates.add(criteriaBuilder.equal(joinArea.get("id"), criteriaList.get("areaID")));

                    if(criteriaList.containsKey("locationName") && !criteriaList.get("locationName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("name"), criteriaList.get("locationName")));
                    }
                    if(criteriaList.containsKey("aisleID") && !criteriaList.get("aisleID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("aisleID"), criteriaList.get("aisleID")));
                    }
                }
                else if ((criteriaList.containsKey("locationName") && !criteriaList.get("locationName").isEmpty()) ||
                        (criteriaList.containsKey("aisleID") && !criteriaList.get("aisleID").isEmpty())) {
                    Join<Inventory, Location> joinLocation = root.join("location",JoinType.INNER);


                    if(criteriaList.containsKey("aisleID") && !criteriaList.get("aisleID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("aisleID"), criteriaList.get("aisleID")));
                    }
                    if(criteriaList.containsKey("locationName") && !criteriaList.get("locationName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("name"), criteriaList.get("locationName")));
                    }
                }

                if(criteriaList.containsKey("lpn") && !criteriaList.get("lpn").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("lpn"), criteriaList.get("lpn")));
                }

                if((criteriaList.containsKey("pick_id") && !criteriaList.get("pick_id").isEmpty()) ||
                        (criteriaList.containsKey("pick_number") && !criteriaList.get("pick_number").isEmpty()) ) {
                    Join<Inventory, Pick> joinPick = root.join("pick",JoinType.INNER);
                    if (criteriaList.containsKey("pick_id") && !criteriaList.get("pick_id").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinPick.get("id"), criteriaList.get("pick_id")));
                    }
                    if (criteriaList.containsKey("pick_number") && !criteriaList.get("pick_number").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinPick.get("number"), criteriaList.get("pick_number")));
                    }
                }


                if(criteriaList.containsKey("inventoryStatus") && !criteriaList.get("inventoryStatus").isEmpty()) {
                    Join<Inventory, InventoryStatus> joinInventoryStatus = root.join("inventoryStatus",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinInventoryStatus.get("name"), criteriaList.get("inventoryStatus")));
                }

                if(criteriaList.containsKey("receiptLineID") && !criteriaList.get("receiptLineID").isEmpty()) {
                    Join<Inventory, ReceiptLine> joinReceiptLine = root.join("receiptLine",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinReceiptLine.get("id"), criteriaList.get("receiptLineID")));

                }

                if ((criteriaList.containsKey("itemName") && !criteriaList.get("itemName").isEmpty()) ||
                        (criteriaList.containsKey("itemDescription") && !criteriaList.get("itemDescription").isEmpty())) {
                    Join<Inventory, ItemFootprint> joinItemFootprint = root.join("itemFootprint",JoinType.INNER);
                    Join<ItemFootprint, Item> joinItem = joinItemFootprint.join("item",JoinType.INNER);


                    if(criteriaList.containsKey("itemName") && !criteriaList.get("itemName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinItem.get("name"), criteriaList.get("itemName")));
                    }
                    if(criteriaList.containsKey("itemDescription") && !criteriaList.get("itemDescription").isEmpty()) {
                        predicates.add(criteriaBuilder.like(joinItem.get("description"), criteriaList.get("itemDescription")));
                    }
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }



    public double getSize(Inventory inventory) {
        if (inventory.getItemFootprint() == null) {
            return 0.0;
        }

        ItemFootprintUOM stockUOM = getStockUOM(inventory.getItemFootprint());
        if (stockUOM == null) {
            return 0.0;
        }

        // both size and weight are calcuated based upon the stock uom
        return (inventory.getQuantity() / stockUOM.getQuantity()) * stockUOM.getHeight()
                * stockUOM.getWidth() * stockUOM.getLength();
    }

    public double getWeight(Inventory inventory) {
        if (inventory.getItemFootprint() == null) {
            return 0.0;
        }

        ItemFootprintUOM stockUOM = getStockUOM(inventory.getItemFootprint());
        if (stockUOM == null) {
            return 0.0;
        }

        // both size and weight are calcuated based upon the stock uom
        return (inventory.getQuantity() / stockUOM.getQuantity()) * stockUOM.getWeight();
    }

    private ItemFootprintUOM getStockUOM(ItemFootprint itemFootprint) {

        List<ItemFootprintUOM> itemFootprintUOMList = itemFootprint.getItemFootprintUOMs();

        // return the UOM that has stock flag checked. If we don't have this kind of UOM
        // return the smallest UOM
        ItemFootprintUOM stockUOM = null;
        for(ItemFootprintUOM uom : itemFootprintUOMList) {
            if (uom.isStockUOM()) {
                return uom;
            }
            if (stockUOM == null) {
                stockUOM = uom;
            }
            // return if current UOM is smallest(has less quantity or small size)
            // than the previous UOM
            else if (uom.getQuantity() < stockUOM.getQuantity() ||
                        (uom.getQuantity() == stockUOM.getQuantity() &&
                         uom.getWeight() < stockUOM.getWeight() &&
                         uom.getWidth() * uom.getHeight() * uom.getLength() <
                         stockUOM.getWidth() * stockUOM.getHeight() * stockUOM.getLength())) {
                stockUOM = uom;
            }

        }
        return stockUOM;

    }

    // Find available inventory for sales order line allocation process
    public List<Inventory> getAvailableInventoryForSalesOrderLine(SalesOrderLine salesOrderLine) {
        Map<String, String> inventoryCriteria = new HashMap<>();
        inventoryCriteria.put("itemName", salesOrderLine.getItem().getName());
        inventoryCriteria.put("inventoryStatus", salesOrderLine.getInventoryStatus().getName());
        return findInventory(inventoryCriteria);
    }

    // Find available inventory that matches certain pick
    public List<Inventory> getAvailableInventoryForPick(Pick pick) {
        Map<String, String> inventoryCriteria = new HashMap<>();
        inventoryCriteria.put("itemName", pick.getItem().getName());
        inventoryCriteria.put("inventoryStatus", pick.getInventoryStatus().getName());
        inventoryCriteria.put("areaID", String.valueOf(pick.getSourceLocation().getArea().getId()));
        inventoryCriteria.put("locationName", pick.getSourceLocation().getName());
        return findInventory(inventoryCriteria);

    }

    @Transactional
    public List<Inventory> pickInventory(Pick pick, int pickQuantity, String lpn){

        List<Inventory> inventoryList = getAvailableInventoryForPick(pick);
        // Let's make sure we still have enough inventory for the pickQuantity.
        // We will consider the whole available inventory list, but not other picks
        // against the same inventory. So in case there's not enough quantity left
        // for all the picks, the picks that done later may not have enough quantity
        validateInventoryForPick(inventoryList, pickQuantity);

        // Deduct the quantity from inventory on FIFO
        Collections.sort(inventoryList, new Comparator<Inventory>() {
            @Override
            public int compare(Inventory inventory1, Inventory inventory2) {
                return inventory1.getFIFODate().compareTo(inventory2.getFIFODate());
            }
        });

        List<Inventory> pickedInventoryList = new ArrayList<>();
        for(Inventory inventory : inventoryList) {
            if (inventory.getQuantity() <= pickQuantity) {
                // the whole inventory will be picked, we won't create a new inventory
                // structure, instead, we will just move the inventory to the destination
                // and mark the inventory as picked
                inventory.setLpn(lpn);
                moveInventory(inventory, pick);
                pickedInventoryList.add(inventory);
            }
            else {
                // we only need to pick partial of the inventory
                Inventory newInventory = splitInventory(inventory, pickQuantity);
                newInventory.setLpn(lpn);
                moveInventory(newInventory, pick);
                pickedInventoryList.add(newInventory);
            }
        }
        return pickedInventoryList;

    }
    @Transactional
    public List<Inventory> pickInventory(Pick pick, int pickQuantity) {
        // If we use carton to pick, then we will use the carton number
        // as the LPN
        if (pick.getCarton() != null) {
            return pickInventory(pick, pickQuantity, pick.getCarton().getNumber());
        }
        else {
            return pickInventory(pick, pickQuantity, "");

        }
    }

    @Transactional
    public List<Inventory> pickInventory(Pick pick) {
        // If we use carton to pick, then we will use the carton number
        // as the LPN
        if (pick.getCarton() != null) {
            return pickInventory(pick, pick.getQuantity() - pick.getPickedQuantity(), pick.getCarton().getNumber());
        }
        else {
            return pickInventory(pick, pick.getQuantity() - pick.getPickedQuantity(), "");

        }
    }

    @Transactional
    public List<Inventory> pickInventory(Pick pick, String lpn) {
        return pickInventory(pick, pick.getQuantity() - pick.getPickedQuantity(), lpn);
    }

    private void validateInventoryForPick(List<Inventory> inventoryList, int pickQuantity) {
        int totalInventoryQuantity = 0;
        for(Inventory inventory : inventoryList) {
            totalInventoryQuantity += inventory.getQuantity();
        }
        if (totalInventoryQuantity < pickQuantity) {
            throw PickException.NOT_SUFFICIENT_QUANTITY;

        }
    }

    // Split the inventory into 2 and return the new inventory
    @Transactional
    private Inventory splitInventory(Inventory inventory, int splitQuantity)
            {
        if (inventory.getQuantity() <= splitQuantity) {
            throw InventoryException.SPLIT_ERROR_NOT_SUFFICIENT_QUANTITY;
        }
        try {
            Inventory splitInventory = inventory.clone();
            splitInventory.setQuantity(splitQuantity);
            inventory.setQuantity(inventory.getQuantity() - splitQuantity);
            save(inventory);
            return save(splitInventory);
        }
        catch (CloneNotSupportedException ex) {
            throw InventoryException.PARTIAL_PICK_NOT_ALLOWED;
        }

    }

    @Transactional
    public void shipCarton(Carton carton) {
        List<Inventory> pickedInventoryList = new ArrayList<>();
        for(Pick pick : carton.getPickList()) {
            for(Inventory pickedInventory : pick.getPickedInventory()) {
                pickedInventoryList.add(pickedInventory);
            }
        }
        if (pickedInventoryList.size() > 0) {
            // ok we get some picked inventory, let's create a fake location
            // in the SHIPPING area first

            Location location = locationService.createFakeLocationForShippedInventory(carton.getNumber());
            // move inventory to this location
            for(Inventory inventory : pickedInventoryList) {
                moveInventory(inventory, location);
            }

        }
    }

}
