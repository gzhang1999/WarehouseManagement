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
import org.springframework.webflow.core.collection.LocalParameterMap;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.common.service.TrailerService;
import se.gzhang.scm.wms.inbound.model.AddPutawayPolicyFlowModel;
import se.gzhang.scm.wms.inbound.model.PutawayPolicy;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.inventory.service.ItemFamilyService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.AreaGroupService;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.layout.service.LocationGroupService;
import se.gzhang.scm.wms.layout.service.LocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AddPutawayPolicyFlowHandler {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemFamilyService itemFamilyService;
    @Autowired
    SupplierService supplierService;
    @Autowired
    TrailerService trailerService;
    @Autowired
    ReceiptService receiptService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaGroupService areaGroupService;
    @Autowired
    LocationService locationService;
    @Autowired
    LocationGroupService locationGroupService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    PutawayPolicyService putawayPolicyService;

    public AddPutawayPolicyFlowModel init() {
        return new AddPutawayPolicyFlowModel();
    }
    public void saveCriteria(AddPutawayPolicyFlowModel addPutawayPolicyFlowModel, LocalParameterMap parameters){
        if (parameters.contains("sequence")) {

            addPutawayPolicyFlowModel.getPutawayPolicy().setSequence(Integer.parseInt(parameters.get("sequence")));
        }
        setupCriteria(addPutawayPolicyFlowModel.getPutawayPolicy(), parameters);
    }

    private void setupCriteria(PutawayPolicy putawayPolicy, LocalParameterMap parameters) {
        if (parameters.contains("item") && !parameters.get("item").isEmpty()) {
            putawayPolicy.setItem(itemService.findByItemName(parameters.get("item")));
        }

        if (parameters.contains("itemFamily") && !parameters.get("itemFamily").isEmpty()) {
            putawayPolicy.setItemFamily(itemFamilyService.findByItemFamilyName(parameters.get("itemFamily")));
        }
        if (parameters.contains("supplier") && !parameters.get("supplier").isEmpty()) {
            putawayPolicy.setSupplier(supplierService.findBySupplierName(parameters.get("supplier")));
        }
        if (parameters.contains("trailer") && !parameters.get("trailer").isEmpty()) {
            putawayPolicy.setTrailer(trailerService.findByTrailerId(Integer.parseInt(parameters.get("trailer"))));
        }
        if (parameters.contains("receipt") && !parameters.get("receipt").isEmpty()) {
            putawayPolicy.setReceipt(receiptService.findByReceiptNumber(parameters.get("receipt")));
        }
        if (parameters.contains("minSize") && !parameters.get("minSize").isEmpty()) {
            putawayPolicy.setMinimumSize(Double.parseDouble(parameters.get("minSize")));
        }

        if (parameters.contains("maxSize") && !parameters.get("maxSize").isEmpty()) {
            putawayPolicy.setMaximumSize(Double.parseDouble(parameters.get("maxSize")));
        }
        if (parameters.contains("minWeight") && !parameters.get("minWeight").isEmpty()) {
            putawayPolicy.setMinimumWeight(Double.parseDouble(parameters.get("minWeight")));
        }
        if (parameters.contains("maxWeight") && !parameters.get("maxWeight").isEmpty()) {
            putawayPolicy.setMaximumSize(Double.parseDouble(parameters.get("maxWeight")));
        }
    }

    public void savePutawayPolicy(AddPutawayPolicyFlowModel addPutawayPolicyFlowModel, LocalParameterMap parameters){
        setupDestination(addPutawayPolicyFlowModel.getPutawayPolicy(), parameters);
        loadMatchedInventory(addPutawayPolicyFlowModel);
        putawayPolicyService.save(addPutawayPolicyFlowModel.getPutawayPolicy());
    }

    private void setupDestination(PutawayPolicy putawayPolicy, LocalParameterMap parameters) {

        if (parameters.contains("area_id") && !parameters.get("area_id").isEmpty()) {
            putawayPolicy.setArea(areaService.findByAreaId(Integer.parseInt(parameters.get("area_id"))));
        }
        if (parameters.contains("areaGroup") && !parameters.get("areaGroup").isEmpty()) {
            putawayPolicy.setAreaGroup(areaGroupService.findByAreaGroupName(parameters.get("areaGroup")));
        }
        if (parameters.contains("location") && !parameters.get("location").isEmpty()) {
            putawayPolicy.setLocation(locationService.findByLocationName(parameters.get("location")));
        }
        if (parameters.contains("locationGroup") && !parameters.get("locationGroup").isEmpty()) {
            putawayPolicy.setLocationGroup(locationGroupService.findByLocationGroupName(parameters.get("locationGroup")));
        }
        if (parameters.contains("locationLevel") && !parameters.get("locationLevel").isEmpty()) {
            putawayPolicy.setLocationLevel(parameters.get("locationLevel"));
        }
        if (parameters.contains("locationAisle") && !parameters.get("locationAisle").isEmpty()) {
            putawayPolicy.setLocationAisleID(parameters.get("locationAisle"));
        }

    }

    private void loadMatchedInventory(AddPutawayPolicyFlowModel addPutawayPolicyFlowModel) {

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
        PutawayPolicy putawayPolicy = addPutawayPolicyFlowModel.getPutawayPolicy();
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
        addPutawayPolicyFlowModel.setInventoryList(receivedInventory);

    }
}
