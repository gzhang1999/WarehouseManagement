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

package se.gzhang.scm.wms.outbound.shipment.service;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.common.model.Customer;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.exception.Outbound.PickException;
import se.gzhang.scm.wms.exception.StandProductException;
import se.gzhang.scm.wms.framework.controls.service.UniversalIdentifierService;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.outbound.order.model.SalesOrder;
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;
import se.gzhang.scm.wms.outbound.shipment.model.*;
import se.gzhang.scm.wms.outbound.shipment.repository.PickRepository;
import se.gzhang.scm.wms.outbound.shipment.repository.ShipmentLineRepository;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class PickService {
    @Autowired
    PickRepository pickRepository;
    @Autowired
    UniversalIdentifierService universalIdentifierService;
    @Autowired
    ShipmentLineService shipmentLineService;
    @Autowired
    AllocationResultService allocationResultService;
    @Autowired
    InventoryService inventoryService;

    public List<Pick> findAll(){

        return pickRepository.findAll();
    }

    public Pick findByPickId(int id){
        return pickRepository.findById(id);
    }

    public Pick findByPickNumber(String number) {
        return pickRepository.findByNumber(number);
    }

    public Pick save(Pick pick) {
        Pick newPick = pickRepository.save(pick);
        pickRepository.flush();
        return newPick;
    }

    public List<Pick> findPicks(Map<String, String> criteriaList) {

        return pickRepository.findAll(new Specification<Pick>() {
            @Override
            public Predicate toPredicate(Root<Pick> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }

                if(criteriaList.containsKey("number") && !criteriaList.get("number").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("number"), criteriaList.get("number")));
                }
                if(criteriaList.containsKey("pickState") && !criteriaList.get("pickState").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("pickState"), PickState.valueOf(criteriaList.get("pickState"))));
                }

                if((criteriaList.containsKey("sourceLocation") && !criteriaList.get("sourceLocation").isEmpty()) ||
                        (criteriaList.containsKey("sourceArea") && !criteriaList.get("sourceArea").isEmpty()) ) {
                    Join<Pick, Location> joinSourceLocation = root.join("sourceLocation",JoinType.INNER);
                    if (criteriaList.containsKey("sourceLocation") && !criteriaList.get("sourceLocation").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSourceLocation.get("name"), criteriaList.get("sourceLocation")));
                    }

                    if (criteriaList.containsKey("sourceArea") && !criteriaList.get("sourceArea").isEmpty()) {
                        Join<Location, Area> joinSourceArea = joinSourceLocation.join("area",JoinType.INNER);
                        predicates.add(criteriaBuilder.equal(joinSourceArea.get("name"), criteriaList.get("sourceArea")));
                    }
                }
                if((criteriaList.containsKey("destinationLocation") && !criteriaList.get("destinationLocation").isEmpty()) ||
                        (criteriaList.containsKey("destinationArea") && !criteriaList.get("destinationArea").isEmpty()) ) {
                    Join<Pick, Location> joinSourceLocation = root.join("destinationLocation",JoinType.INNER);
                    if (criteriaList.containsKey("destinationLocation") && !criteriaList.get("destinationLocation").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSourceLocation.get("name"), criteriaList.get("destinationLocation")));
                    }

                    if (criteriaList.containsKey("destinationArea") && !criteriaList.get("destinationArea").isEmpty()) {
                        Join<Location, Area> joinSourceArea = joinSourceLocation.join("area",JoinType.INNER);
                        predicates.add(criteriaBuilder.equal(joinSourceArea.get("name"), criteriaList.get("destinationArea")));
                    }
                }

                if((criteriaList.containsKey("salesOrderNumber") && !criteriaList.get("salesOrderNumber").isEmpty()) ||
                        (criteriaList.containsKey("shipmentNumber") && !criteriaList.get("shipmentNumber").isEmpty()) ) {
                    Join<Pick, ShipmentLine> joinShipmentLine = root.join("shipmentLine",JoinType.INNER);
                    Join<ShipmentLine, Shipment> joinShipment = joinShipmentLine.join("shipment",JoinType.INNER);
                    Join<ShipmentLine, SalesOrderLine> joinSalesOrderLine = joinShipmentLine.join("salesOrderLine",JoinType.INNER);
                    Join<SalesOrderLine, SalesOrder> joinSalesOrder = joinSalesOrderLine.join("salesOrder",JoinType.INNER);

                    if (criteriaList.containsKey("salesOrderNumber") && !criteriaList.get("salesOrderNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSalesOrder.get("number"), criteriaList.get("salesOrderNumber")));
                    }

                    if (criteriaList.containsKey("shipmentNumber") && !criteriaList.get("shipmentNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinShipment.get("number"), criteriaList.get("shipmentNumber")));
                    }

                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }


    @Transactional
    public Pick generatePickFromShipmentLineAllocation(ShipmentLine shipmentLine, Location location, int quantity) {
        Pick pick = new Pick();
        pick.setNumber(universalIdentifierService.getNextNumber("pick_number"));
        pick.setShipmentLine(shipmentLine);
        pick.setSourceLocation(location);
        pick.setQuantity(quantity);
        pick.setPickedQuantity(0);
        pick.setPickState(PickState.NEW);
        pick.setDestinationLocation(shipmentLine.getSalesOrderLine().getShippingStageLocation());
        return  save(pick);
    }

    @Transactional
    public void cancelPick(Pick pick) {
        if (pick.getPickState().equals(PickState.CANCELLED) ||
                pick.getPickState().equals(PickState.COMPLETED) ||
                pick.getPickState().equals(PickState.PICKED) ||
                pick.getPickState().equals(PickState.PICKING)) {
            throw PickException.NOT_VALID_STATE_FOR_CANCELLATION;
        }
        // Mark the pick as cancelled
        pick.setPickState(PickState.CANCELLED);
        pick.setCancelledDate(new Date());
        save(pick);

        // Return the quantity back to shipment
        ShipmentLine shipmentLine = pick.getShipmentLine();
        shipmentLine.setInprocessQuantity(Math.max(0, shipmentLine.getInprocessQuantity() - pick.getQuantity()));
        shipmentLineService.save(shipmentLine);

        // reset the allocated quantity of the inventory
        Map<String, String> criteria = new HashMap<>();
        criteria.put("location", pick.getSourceLocation().getName());
        criteria.put("area", pick.getSourceLocation().getArea().getName());
        criteria.put("item", pick.getShipmentLine().getSalesOrderLine().getItem().getName());
        criteria.put("inventoryStatus", pick.getShipmentLine().getSalesOrderLine().getInventoryStatus().getName());
        List<AllocationResult> allocationResultList = allocationResultService.findAllocationResults(criteria);
        // We should only find one result and deduct the quantity from it
        if (allocationResultList.size() > 0) {
            AllocationResult allocationResult = allocationResultList.get(0);
            allocationResultService.deallocateInventoryFromLocation(allocationResult, pick.getQuantity());
        }

    }

    @Transactional
    public void confirmPick(Pick pick, int confirmedQuantity) {
        // make sure the pick is not cancelled nor completed
        if (pick.getPickState() != PickState.CANCELLED &&
                pick.getPickState() != PickState.COMPLETED) {
            pick.setPickedQuantity(pick.getPickedQuantity() + confirmedQuantity);
            if (pick.getPickedQuantity() == pick.getQuantity()) {
                pick.setPickState(PickState.PICKED);
            }
            else {
                pick.setPickState(PickState.PICKING);
            }
            save(pick);

            // move inventory to the pick's destination

        }
    }
}
