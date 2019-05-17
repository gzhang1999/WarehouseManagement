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
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;
import se.gzhang.scm.wms.outbound.shipment.model.*;
import se.gzhang.scm.wms.outbound.shipment.repository.ShipmentLineRepository;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class ShipmentLineService {
    @Autowired
    ShipmentLineRepository shipmentLineRepository;
    @Autowired
    PickService pickService;
    @Autowired
    ShortAllocationService shortAllocationService;

    public List<ShipmentLine> findAll(){

        return shipmentLineRepository.findAll();
    }

    public ShipmentLine findByShipmentLineId(int id){
        return shipmentLineRepository.findById(id);
    }

    public List<ShipmentLine> findShipmentLines(Map<String, String> criteriaList) {

        return shipmentLineRepository.findAll(new Specification<ShipmentLine>() {
            @Override
            public Predicate toPredicate(Root<ShipmentLine> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }

                if(criteriaList.containsKey("number") && !criteriaList.get("number").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("number"), criteriaList.get("number")));
                }


                if((criteriaList.containsKey("shipment_id") && !criteriaList.get("shipment_id").isEmpty())) {
                    Join<ShipmentLine, Shipment> joinShipment = root.join("shipment",JoinType.INNER);

                    predicates.add(criteriaBuilder.equal(joinShipment.get("id"), criteriaList.get("shipment_id")));

                }

                if((criteriaList.containsKey("sales_order_line_id") && !criteriaList.get("sales_order_line_id").isEmpty())) {
                    Join<ShipmentLine, SalesOrderLine> joinSalesOrderLine = root.join("salesOrderLine",JoinType.INNER);

                    predicates.add(criteriaBuilder.equal(joinSalesOrderLine.get("id"), criteriaList.get("sales_order_line_id")));

                }


                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public List<ShipmentLine> getShipmentLine(SalesOrderLine salesOrderLine) {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("sales_order_line_id", String.valueOf(salesOrderLine.getId()));
        return findShipmentLines(criteriaList);
    }

    // Check if there's any open shipment line so that we can assign the order line to
    // Open means the shipment line's state is NEW and not allocated
    public ShipmentLine getOpenShipmentLine(SalesOrderLine salesOrderLine) {

        List<ShipmentLine> shipmentLineList = getShipmentLine(salesOrderLine);
        // We will assume that there will be only one open shipment line for
        // each sales order line
        Iterator<ShipmentLine> shipmentLineIterator = shipmentLineList.iterator();
        while(shipmentLineIterator.hasNext()) {
            ShipmentLine shipmentLine = shipmentLineIterator.next();
            if (shipmentLine.getShipmentLineState() == ShipmentLineState.CANCELLED) {
                shipmentLineIterator.remove();
            }
        }
        if (shipmentLineList.size() > 0) {
            return shipmentLineList.get(0);
        }
        else {
            return null;
        }
    }

    @Transactional
    public ShipmentLine save(ShipmentLine shipmentLine) {
        return shipmentLineRepository.save(shipmentLine);
    }
    @Transactional
    public void removeShipmentLine(ShipmentLine shipmentLine) {
        shipmentLineRepository.delete(shipmentLine);
    }

    @Transactional
    public void cancelShipmentLine(ShipmentLine shipmentLine) {

        // Let's cancel all the pick and short allocation first
        List<Pick> pickList = shipmentLine.getPicks();
        for(Pick pick : pickList) {

            if (pick.getPickState().equals(PickState.CANCELLED)) {
                continue;
            }
            pickService.cancelPick(pick);
        }
        List<ShortAllocation> shortAllocationList = shipmentLine.getShortAllocation();
        //
        for(ShortAllocation shortAllocation : shortAllocationList) {

            if (shortAllocation.getShortAllocationState().equals(ShortAllocationState.CANCELLED)) {
                continue;
            }
            shortAllocationService.cancelShortAllocation(shortAllocation);
        }

        shipmentLine.setShipmentLineState(ShipmentLineState.CANCELLED);
        save(shipmentLine);

    }
}
