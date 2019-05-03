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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.common.model.Customer;
import se.gzhang.scm.wms.framework.controls.service.UniversalIdentifierService;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.outbound.order.model.*;
import se.gzhang.scm.wms.outbound.order.service.AllocationStrategyService;
import se.gzhang.scm.wms.outbound.shipment.model.Shipment;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLineState;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentState;
import se.gzhang.scm.wms.outbound.shipment.repository.ShipmentRepository;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class ShipmentService {
    @Autowired
    UniversalIdentifierService universalIdentifierService;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    AllocationStrategyService allocationStrategyService;
    @Autowired
    ShipmentLineService shipmentLineService;
    @Autowired
    ShipmentRepository shipmentRepository;

    public List<Shipment> findAll(){

        return shipmentRepository.findAll();
    }

    public Shipment findByShipmentId(int id){
        return shipmentRepository.findById(id);
    }

    public Shipment findByShipmentNumber(String number) {
        return shipmentRepository.findByNumber(number);
    }

    public Shipment save(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    public List<Shipment> findShipments(Map<String, String> criteriaList) {

        return shipmentRepository.findAll(new Specification<Shipment>() {
            @Override
            public Predicate toPredicate(Root<Shipment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }

                if(criteriaList.containsKey("number") && !criteriaList.get("number").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("number"), criteriaList.get("number")));
                }

                if((criteriaList.containsKey("salesOrderNumber") && !criteriaList.get("salesOrderNumber").isEmpty()) ||
                        (criteriaList.containsKey("salesOrderLineNumber") && !criteriaList.get("salesOrderLineNumber").isEmpty()) ||
                        (criteriaList.containsKey("salesOrderExternalID") && !criteriaList.get("salesOrderExternalID").isEmpty()) ||
                        (criteriaList.containsKey("shipToCustomerFirstName") && !criteriaList.get("shipToCustomerFirstName").isEmpty()) ||
                        (criteriaList.containsKey("shipToCustomerLastName") && !criteriaList.get("shipToCustomerLastName").isEmpty()) ||
                        (criteriaList.containsKey("billToCustomerFirstName") && !criteriaList.get("billToCustomerFirstName").isEmpty()) ||
                        (criteriaList.containsKey("billToCustomerLastName") && !criteriaList.get("billToCustomerLastName").isEmpty())) {
                    Join<Shipment, ShipmentLine> joinShipmentLine = root.join("shipmentLines",JoinType.INNER);
                    Join<ShipmentLine, SalesOrderLine> joinSalesOrderLine = joinShipmentLine.join("salesOrderLine",JoinType.INNER);
                    Join<SalesOrderLine, SalesOrder> joinSalesOrder = joinSalesOrderLine.join("salesOrder",JoinType.INNER);

                    if (criteriaList.containsKey("salesOrderLineNumber") && !criteriaList.get("salesOrderLineNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSalesOrderLine.get("number"), criteriaList.get("salesOrderLineNumber")));
                    }
                    if (criteriaList.containsKey("salesOrderNumber") && !criteriaList.get("salesOrderNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSalesOrder.get("number"), criteriaList.get("salesOrderNumber")));
                    }
                    if (criteriaList.containsKey("salesOrderExternalID") && !criteriaList.get("salesOrderExternalID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSalesOrder.get("externalID"), criteriaList.get("salesOrderExternalID")));
                    }
                    if((criteriaList.containsKey("shipToCustomerFirstName") && !criteriaList.get("shipToCustomerFirstName").isEmpty()) ||
                            (criteriaList.containsKey("shipToCustomerLastName") && !criteriaList.get("shipToCustomerLastName").isEmpty())) {
                        Join<SalesOrder, Customer> joinShipToCustomer = joinSalesOrder.join("shipToCustomer",JoinType.INNER);

                        if(criteriaList.containsKey("shipToCustomerFirstName") && !criteriaList.get("shipToCustomerFirstName").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(joinShipToCustomer.get("firstName"), criteriaList.get("shipToCustomerFirstName")));
                        }
                        if(criteriaList.containsKey("shipToCustomerLastName") && !criteriaList.get("shipToCustomerLastName").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(joinShipToCustomer.get("lastName"), criteriaList.get("shipToCustomerLastName")));
                        }
                    }
                    if((criteriaList.containsKey("billToCustomerFirstName") && !criteriaList.get("billToCustomerFirstName").isEmpty()) ||
                            (criteriaList.containsKey("billToCustomerLastName") && !criteriaList.get("billToCustomerLastName").isEmpty())) {
                        Join<Order, Customer> joinBillToCustomer = joinSalesOrder.join("billToCustomer",JoinType.INNER);

                        if(criteriaList.containsKey("billToCustomerFirstName") && !criteriaList.get("billToCustomerFirstName").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(joinBillToCustomer.get("firstName"), criteriaList.get("billToCustomerFirstName")));
                        }
                        if(criteriaList.containsKey("billToCustomerLastName") && !criteriaList.get("billToCustomerLastName").isEmpty()) {
                            predicates.add(criteriaBuilder.equal(joinBillToCustomer.get("lastName"), criteriaList.get("billToCustomerLastName")));
                        }
                    }

                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    @Transactional
    public Shipment createShipment(SalesOrder salesOrder) {

        List<Shipment> shipmentList =  createShipment(salesOrder.getSalesOrderLines());
        if (shipmentList.size() == 0) {
            return null;
        }
        else {
            return shipmentList.get(0);
        }

    }

    // create a shipment for a list of sales order line
    // we will stick to one shipment - one sales order restriction
    // for simplicity
    @Transactional
    public List<Shipment> createShipment(List<SalesOrderLine> salesOrderLineList) {

        List<Shipment> shipmentList = new ArrayList<>();

        if (salesOrderLineList.size() == 0) {
            return shipmentList;
        }

        SalesOrder currentSalesOrder = salesOrderLineList.get(0).getSalesOrder();
        // Get the shipment head structure from the sales order
        Shipment currentShipment = createShipmentHeader(currentSalesOrder);
        shipmentList.add(currentShipment);
        for(SalesOrderLine salesOrderLine : salesOrderLineList) {
            // If we have a new sales order, let's start with a new shipment
            if (!salesOrderLine.getSalesOrder().equals(currentSalesOrder)) {
                currentSalesOrder = salesOrderLine.getSalesOrder();
                currentShipment = createShipmentHeader(currentSalesOrder);
                shipmentList.add(currentShipment);
            }
            // Attach the sales order line to current shipment
            // and create a shipment line for it
            attachSalesOrderLine(currentShipment, salesOrderLine);
        }
        return shipmentList;
    }

    // create a shipment for one sales order line
    @Transactional
    public Shipment createShipment(SalesOrderLine salesOrderLine) {
        List<SalesOrderLine> salesOrderLineList = new ArrayList<>();
        salesOrderLineList.add(salesOrderLine);
        List<Shipment> shipmentList = createShipment(salesOrderLineList);
        if (shipmentList.size() > 0) {
            return shipmentList.get(0);
        }
        return null;
    }

    @Transactional
    private Shipment createShipmentHeader(SalesOrder salesOrder) {
        // Check if this sales order already have an shipment assigned
        Shipment shipment = getOpenShipment(salesOrder);
        if (shipment == null) {
            shipment = new Shipment();
            shipment.setNumber(universalIdentifierService.getNextNumber("shipment_number"));
            shipment.setWarehouse(salesOrder.getWarehouse());
            shipment.setCustomer(salesOrder.getShipToCustomer());
            shipment.setShipmentState(ShipmentState.NEW);
        }
        save(shipment);
        return shipment;
    }

    // Attach a sales order line to the existing shipment
    @Transactional
    private void attachSalesOrderLine(Shipment shipment, SalesOrderLine salesOrderLine) {

        // Check if we already have a shipment line assigned for this sales order line
        if (shipmentLineService.getOpenShipmentLine(salesOrderLine) == null) {
            shipment.addShipmentLine(createShipmentLine(shipment, salesOrderLine));
        }

    }

    // create a new shipment line from sales order line
    @Transactional
    private ShipmentLine createShipmentLine(Shipment shipment, SalesOrderLine salesOrderLine) {

        ShipmentLine shipmentLine = new ShipmentLine();
        shipmentLine.setNumber(universalIdentifierService.getNextNumber("shipment_line_number"));
        shipmentLine.setOrderQuantity(salesOrderLine.getQuantity());
        shipmentLine.setSalesOrderLine(salesOrderLine);
        shipmentLine.setShipment(shipment);
        shipmentLine.setShippedQuantity(0);
        shipmentLine.setShipmentLineState(ShipmentLineState.NEW);
        shipmentLine.setInprocessQuantity(0);
        shipmentLineService.save(shipmentLine);
        return shipmentLine;
    }

    public List<Shipment> getShipments(SalesOrder salesOrder) {
        Set<Shipment> shipmentSet = new HashSet<>();
        for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
            List<ShipmentLine> shipmentLineList = shipmentLineService.getShipmentLine(salesOrderLine);
            for(ShipmentLine shipmentLine : shipmentLineList) {
                shipmentSet.add(shipmentLine.getShipment());
            }
        }
        return new ArrayList<>(shipmentSet);

    }

    public Shipment getOpenShipment(SalesOrder salesOrder) {
        List<Shipment> shipmentList = getShipments(salesOrder);
        Iterator<Shipment> shipmentIterator = shipmentList.iterator();
        while(shipmentIterator.hasNext()) {
            Shipment shipment = shipmentIterator.next();
            if (shipment.getShipmentState().equals(ShipmentState.CANCELLED)) {
                shipmentIterator.remove();
            }
        }
        if (shipmentList.size() > 0) {
            return shipmentList.get(0);
        }
        else {
            return null;
        }
    }

    @Transactional
    public void allocateShipment(Shipment shipment) {
        List<Inventory> inventoryList = new ArrayList<>();
        for(ShipmentLine shipmentLine : shipment.getShipmentLines()) {
            // only allocate the new shipment line
            if (shipmentLine.getOrderQuantity() > (shipmentLine.getInprocessQuantity() + shipmentLine.getShippedQuantity())) {
                allocateShipmentLine(shipmentLine);
                // Change the state of the shipment line to ALLOCATED
                if (shipmentLine.getShipmentLineState().equals(ShipmentLineState.NEW)) {
                    shipmentLine.setShipmentLineState(ShipmentLineState.ALLOCATED);
                }
                // We will assume that the 'allocateShipmentLine' will always
                // allocate the whole open quantity to either get a pick or
                // a short allocation, so here we will change the inprocess quantity
                // to match with the open quantity
                shipmentLine.setInprocessQuantity(shipmentLine.getOrderQuantity() - shipmentLine.getShippedQuantity());
                shipmentLineService.save(shipmentLine);
            }
        }
        // Change the state of the shipment to ALLOCATED
        shipment.setShipmentState(ShipmentState.ALLOCATED);
        save(shipment);
    }

    @Transactional
    public void allocateShipmentLine(ShipmentLine shipmentLine) {

        List<SalesOrderLineAllocationStrategy> salesOrderLineAllocationStrategyList = shipmentLine.getSalesOrderLine().getSalesOrderLineAllocationStrategyArrayList();

        // Default the allocation strategy to FIFO
        if (salesOrderLineAllocationStrategyList.size() == 0) {
            SalesOrderLineAllocationStrategy defaultSalesOrderLineAllocationStrategy = new SalesOrderLineAllocationStrategy();
            defaultSalesOrderLineAllocationStrategy.setAllocationStrategyType(AllocationStrategyType.FIFO);
            defaultSalesOrderLineAllocationStrategy.setSequence(1);
            defaultSalesOrderLineAllocationStrategy.setSalesOrderLine(shipmentLine.getSalesOrderLine());
            salesOrderLineAllocationStrategyList.add(defaultSalesOrderLineAllocationStrategy);
        }
        // Get all the available inventory for this sales order line
        List<Inventory> availableInventory = inventoryService.getAvailableInventoryForSalesOrderLine(shipmentLine.getSalesOrderLine());
        allocateShipmentLine(shipmentLine, salesOrderLineAllocationStrategyList, availableInventory);
    }

    @Transactional
    private void allocateShipmentLine(ShipmentLine shipmentLine,
                                                 List<SalesOrderLineAllocationStrategy> salesOrderLineAllocationStrategyList,
                                                 List<Inventory> availableInventory) {

        // Sort the allocation strategy based on the sequence and applied one by one
        // to find the final result
        // TO-DO: 4/29/2019, Now we only support one allocation strategy for each
        // sales order line. WE Will support the combination later on
        Collections.sort(salesOrderLineAllocationStrategyList, new Comparator<SalesOrderLineAllocationStrategy>() {
                    @Override
                    public int compare(SalesOrderLineAllocationStrategy salesOrderLineAllocationStrategy1,
                                       SalesOrderLineAllocationStrategy salesOrderLineAllocationStrategy2) {
                        return salesOrderLineAllocationStrategy1.getSequence() - salesOrderLineAllocationStrategy2.getSequence();
                    }
                }
        );
        SalesOrderLineAllocationStrategy salesOrderLineAllocationStrategy = salesOrderLineAllocationStrategyList.get(0);

        AllocationStrategy allocationStrategy = allocationStrategyService.getAllocationStrategy(salesOrderLineAllocationStrategy.getAllocationStrategyType());
        allocationStrategy.allocate(shipmentLine, availableInventory);
    }


}
