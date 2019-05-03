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
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.outbound.shipment.model.*;
import se.gzhang.scm.wms.outbound.shipment.repository.AllocationResultRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AllocationResultService {

    @Autowired
    private AllocationResultRepository allocationResultRepository;

    public List<AllocationResult> findAll(){

        return allocationResultRepository.findAll();
    }

    public AllocationResult findByAllocationResultId(int id){
        return allocationResultRepository.findById(id);
    }

    @Transactional
    public AllocationResult save(AllocationResult allocationResult) {
        AllocationResult newAllocationResult = allocationResultRepository.save(allocationResult);
        allocationResultRepository.flush();
        return newAllocationResult;
    }

    public List<AllocationResult> findAllocationResults(Map<String, String> criteriaList) {

        return allocationResultRepository.findAll(new Specification<AllocationResult>() {
            @Override
            public Predicate toPredicate(Root<AllocationResult> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }

                if((criteriaList.containsKey("location") && !criteriaList.get("location").isEmpty()) ||
                        (criteriaList.containsKey("area") && !criteriaList.get("area").isEmpty()) ) {
                    Join<AllocationResult, Location> joinLocation = root.join("location",JoinType.INNER);
                    if (criteriaList.containsKey("location") && !criteriaList.get("location").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("name"), criteriaList.get("location")));
                    }

                    if (criteriaList.containsKey("area") && !criteriaList.get("area").isEmpty()) {
                        Join<Location, Area> joinArea = joinLocation.join("area",JoinType.INNER);
                        predicates.add(criteriaBuilder.equal(joinArea.get("name"), criteriaList.get("area")));
                    }
                }
                if((criteriaList.containsKey("item") && !criteriaList.get("item").isEmpty())) {
                    Join<AllocationResult, Item> joinItem = root.join("item",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinItem.get("name"), criteriaList.get("item")));
                }
                if((criteriaList.containsKey("inventoryStatus") && !criteriaList.get("inventoryStatus").isEmpty())) {
                    Join<AllocationResult, InventoryStatus> joinInventoryStatus = root.join("inventoryStatus",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinInventoryStatus.get("name"), criteriaList.get("inventoryStatus")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    @Transactional
    public AllocationResult allocateInventoryFromLocation(ShipmentLine shipmentLine, Location location, int allocatedQuantity) {
        AllocationResult allocationResult = new AllocationResult();
        allocationResult.setAllocatedQuantity(allocatedQuantity);
        allocationResult.setInventoryStatus(shipmentLine.getSalesOrderLine().getInventoryStatus());
        allocationResult.setItem(shipmentLine.getSalesOrderLine().getItem());
        allocationResult.setLocation(location);
        return save(allocationResult);
    }


}
