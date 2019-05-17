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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.common.model.Customer;
import se.gzhang.scm.wms.common.service.CustomerService;
import se.gzhang.scm.wms.inventory.model.ItemFootprintUOM;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.VolumeType;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.outbound.order.model.SalesOrder;
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;
import se.gzhang.scm.wms.outbound.order.model.ShippingStageLocationReserveStrategy;
import se.gzhang.scm.wms.outbound.order.repository.SalesOrderRepository;
import se.gzhang.scm.wms.outbound.shipment.model.Shipment;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentLineService;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentService;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalesOrderService {

    @Autowired
    SalesOrderRepository salesOrderRepository;
    @Autowired
    CustomerService customerService;
    @Autowired
    ShipmentLineService shipmentLineService;
    @Autowired
    ShipmentService shipmentService;
    @Autowired
    AreaService areaService;
    @Autowired
    ShippingStrageLocationReserveStrategyService shippingStrageLocationReserveStrategyService;
    @Autowired
    SalesOrderLineService salesOrderLineService;

    public List<SalesOrder> findAll(){

        return salesOrderRepository.findAll();
    }

    public SalesOrder findBySalesOrderId(int id){
        return salesOrderRepository.findById(id);
    }

    public SalesOrder findBySalesOrderNumber(String number) {
        return salesOrderRepository.findByNumber(number);
    }

    public SalesOrder findBySalesOrderExternalID(String externalID) {
        return salesOrderRepository.findByExternalID(externalID);
    }

    @Transactional
    public SalesOrder save(SalesOrder salesOrder) {
        // If the external ID is empty, we will use order number
        // as the external ID
        if (salesOrder.getExternalID().isEmpty()) {
            salesOrder.setExternalID(salesOrder.getNumber());
            for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
                if (salesOrderLine.getExternalID().isEmpty()) {
                    salesOrderLine.setExternalID(salesOrder.getNumber() + "-" + salesOrderLine.getLineNumber());
                }
            }
        }
        return salesOrderRepository.save(salesOrder);
    }


    public List<SalesOrder> findSalesOrders(Map<String, String> criteriaList) {
        if (criteriaList.containsKey("orderID") && !criteriaList.get("orderID").isEmpty()) {
            criteriaList.put("id", criteriaList.get("orderID"));
        }
        return salesOrderRepository.findAll(new Specification<SalesOrder>() {
            @Override
            public Predicate toPredicate(Root<SalesOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("externalID") && !criteriaList.get("externalID").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("externalID"), criteriaList.get("externalID")));
                }

                if(criteriaList.containsKey("number") && !criteriaList.get("number").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("number"), criteriaList.get("number")));
                }


                if((criteriaList.containsKey("shipToCustomerFirstName") && !criteriaList.get("shipToCustomerFirstName").isEmpty()) ||
                        (criteriaList.containsKey("shipToCustomerLastName") && !criteriaList.get("shipToCustomerLastName").isEmpty())) {
                    Join<SalesOrder, Customer> joinShipToCustomer = root.join("shipToCustomer",JoinType.INNER);

                    if(criteriaList.containsKey("shipToCustomerFirstName") && !criteriaList.get("shipToCustomerFirstName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinShipToCustomer.get("firstName"), criteriaList.get("shipToCustomerFirstName")));
                    }
                    if(criteriaList.containsKey("shipToCustomerLastName") && !criteriaList.get("shipToCustomerLastName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinShipToCustomer.get("lastName"), criteriaList.get("shipToCustomerLastName")));
                    }
                }

                if((criteriaList.containsKey("billToCustomerFirstName") && !criteriaList.get("billToCustomerFirstName").isEmpty()) ||
                        (criteriaList.containsKey("billToCustomerLastName") && !criteriaList.get("billToCustomerLastName").isEmpty())) {
                    Join<SalesOrder, Customer> joinBillToCustomer = root.join("billToCustomer",JoinType.INNER);

                    if(criteriaList.containsKey("billToCustomerFirstName") && !criteriaList.get("billToCustomerFirstName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinBillToCustomer.get("firstName"), criteriaList.get("billToCustomerFirstName")));
                    }
                    if(criteriaList.containsKey("billToCustomerLastName") && !criteriaList.get("billToCustomerLastName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinBillToCustomer.get("lastName"), criteriaList.get("billToCustomerLastName")));
                    }
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    @Transactional
    public void deleteBySalesOrderID(int salesOrderID) {
        SalesOrder salesOrder = findBySalesOrderId(salesOrderID);

        salesOrderRepository.deleteById(salesOrderID);
    }

    @Transactional
    public SalesOrder createSalesOrder(String externalID,
                                       String number,
                                       Integer shipToCustomerID,
                                       Integer billToCustomerID){
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setExternalID(externalID);
        salesOrder.setNumber(number);
        salesOrder.setShipToCustomer(customerService.findByCustomerId(shipToCustomerID));
        salesOrder.setBillToCustomer(customerService.findByCustomerId(billToCustomerID));

        return save(salesOrder);
    }

    @Transactional
    public SalesOrder changeSalesOrder(int salesOrderID,
                                       String number,
                                       String externalID,
                                       Integer shipToCustomerID,
                                       Integer billToCustomerID){
        SalesOrder salesOrder = findBySalesOrderId(salesOrderID);
        salesOrder.setExternalID(externalID);
        salesOrder.setNumber(number);
        salesOrder.setShipToCustomer(customerService.findByCustomerId(shipToCustomerID));
        salesOrder.setBillToCustomer(customerService.findByCustomerId(billToCustomerID));

        return save(salesOrder);
    }

    @Transactional
    public void allocateInventory(SalesOrder salesOrder) {

        Shipment shipment = shipmentService.createShipment(salesOrder);

        shipmentService.allocateShipment(shipment);
    }

    public void loadShipmentInformation(SalesOrder salesOrder) {
        // load the shipment information to the sales order
        for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
            Map<String, String> criteria = new HashMap<>();
            criteria.put("sales_order_line_id", String.valueOf(salesOrderLine.getId()));
            List<ShipmentLine> shipmentLineList = shipmentLineService.findShipmentLines(criteria);
            salesOrderLine.setShipmentLineList(shipmentLineList);
        }
    }

    @Transactional
    public Location reserveShippingStageLocation(SalesOrder salesOrder) {

        // only process when the order doesn't have any location reserved yet
        Location reservedLocation = null;
        for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
            if (salesOrderLine.getShippingStageLocation() != null) {
                reservedLocation = salesOrderLine.getShippingStageLocation();
            }
        }
        if (reservedLocation != null) {
            // We already reserved a location for this sales order
            // currently we only support one location for the entire sales order
            for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
                if (!reservedLocation.equals(salesOrderLine.getShippingStageLocation())) {
                    salesOrderLine.setShippingStageLocation(reservedLocation);
                    salesOrderLineService.save(salesOrderLine);
                }
            }
            return reservedLocation;
        }
        // 1. get all available shipping stage area
        List<Area> shippingStageArea = areaService.getShipppingStageAreas();
        // Loop through each area until we get the right location
        for(Area area : shippingStageArea) {
            reservedLocation = reserveShippingStageLocation(salesOrder, area);
            if (reservedLocation != null) {
                ShippingStageLocationReserveStrategy shippingStageLocationReserveStrategy =
                        shippingStrageLocationReserveStrategyService.getShippingStrageLocationReserveStrategy(area.getLocationReserveStrategyType());
                shippingStageLocationReserveStrategy.reserve(reservedLocation, salesOrder);
                for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
                    if (salesOrderLine.getShippingStageLocation() == null) {
                        salesOrderLine.setShippingStageLocation(reservedLocation);
                        salesOrderLineService.save(salesOrderLine);
                    }
                }

            }
        }
        return reservedLocation;
    }

    public Location reserveShippingStageLocation(SalesOrder salesOrder, Area area) {
        double salesOrderVolume = getSalesOrderVolume(salesOrder, area.getVolumeType());

        for(Location location : area.getLocations()) {
            if (location.getVolume() - location.getUsedVolume() > salesOrderVolume) {
                return location;
            }
        }
        return null;
    }

    public double getSalesOrderVolume(SalesOrder salesOrder, VolumeType volumeType) {
        switch (volumeType) {
            case EACH:
                return getSalesOrderVolumeByEach(salesOrder);
            case SIZE:
                return getSalesOrderVolumeBySize(salesOrder);
            default:
                // by default it is SIZE
                return getSalesOrderVolumeBySize(salesOrder);
        }

    }

    public double getSalesOrderVolumeBySize(SalesOrder salesOrder) {
        double size = 0.0d;
        for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
            int orderQuantity = salesOrderLine.getQuantity();
            // calculate from default item footprint
            ItemFootprintUOM stockItemFootprintUOM = salesOrderLine.getItem().getDefaultItemFootprint().getStockItemFootprintUOM();
            size += (orderQuantity / stockItemFootprintUOM.getQuantity()) * stockItemFootprintUOM.getLength() *
                    stockItemFootprintUOM.getWidth() * stockItemFootprintUOM.getHeight();

        }
        return size;
    }
    public double getSalesOrderVolumeByEach(SalesOrder salesOrder) {
        double quantity = 0.0d;
        for(SalesOrderLine salesOrderLine : salesOrder.getSalesOrderLines()) {
            quantity += salesOrderLine.getQuantity();

        }
        return quantity;
    }

}
