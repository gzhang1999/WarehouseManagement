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
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.exception.Outbound.CartonException;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.outbound.shipment.model.*;
import se.gzhang.scm.wms.outbound.shipment.repository.CartonAuditResultRepository;
import se.gzhang.scm.wms.outbound.shipment.repository.CartonTypeRepository;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class CartonAuditResultService {

    @Autowired
    private CartonAuditResultRepository cartonAuditResultRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CartonService cartonService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ItemService itemService;


    public List<CartonAuditResult> findAll(){

        return cartonAuditResultRepository.findAll();
    }

    public CartonAuditResult findByCartonAuditResultId(int id){
        return cartonAuditResultRepository.findById(id);
    }

    @Transactional
    public CartonAuditResult save(CartonAuditResult cartonAuditResult) {
        return cartonAuditResultRepository.save(cartonAuditResult);
    }

    public List<CartonAuditResult> findCartonAuditResults(Map<String, String> criteriaList) {

        return cartonAuditResultRepository.findAll(new Specification<CartonAuditResult>() {
            @Override
            public Predicate toPredicate(Root<CartonAuditResult> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("cartonAuditState") && !criteriaList.get("cartonAuditState").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("cartonAuditState"), CartonAuditState.valueOf(criteriaList.get("cartonAuditState"))));
                }

                if(criteriaList.containsKey("cartonNumber") && !criteriaList.get("cartonNumber").isEmpty()) {
                    Join<CartonAuditResult, Carton> joinCaron = root.join("carton",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinCaron.get("number"), criteriaList.get("cartonNumber")));
                }
                if(criteriaList.containsKey("auditUser") && !criteriaList.get("auditUser").isEmpty()) {
                    Join<CartonAuditResult, User> joinUser = root.join("auditUser",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinUser.get("username"), criteriaList.get("auditUser")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    @Transactional
    public CartonAuditResult initCartonAuditResult(String cartonNumber) {
        Carton carton = cartonService.findByCartonNumber(cartonNumber);
        if (carton == null) {
            throw CartonException.NO_SUCH_CARTON;
        }
        CartonAuditResult cartonAuditResult = new CartonAuditResult(carton, userService.getCurrentLoginUser());

        initCartonAuditResultLines(cartonAuditResult, cartonNumber);

        return save(cartonAuditResult);
    }

    public void initCartonAuditResultLines(CartonAuditResult cartonAuditResult, String cartonNumber) {

        // Initial lines based on the items picked for the carton
        List<Inventory> pickedInventoryList = inventoryService.findInventoryByLPN(cartonNumber);
        if (pickedInventoryList == null || pickedInventoryList.size() == 0) {
            throw CartonException.CARTON_EMPTY_NO_PICKED_INVENTORY;
        }

        Map<Item, Integer> expectedItemsWithQuantity = new HashMap<>();
        for(Inventory inventory : pickedInventoryList) {
            Item item = inventory.getItemFootprint().getItem();
            if (expectedItemsWithQuantity.containsKey(item)) {
                expectedItemsWithQuantity.put(item, (expectedItemsWithQuantity.get(item) + inventory.getQuantity()));
            }
            else {
                expectedItemsWithQuantity.put(item, inventory.getQuantity());
            }
        }

        for(Map.Entry<Item, Integer> expectedItemWithQuantity : expectedItemsWithQuantity.entrySet()) {
            CartonAuditResultLine cartonAuditResultLine = new CartonAuditResultLine();
            cartonAuditResultLine.setItem(expectedItemWithQuantity.getKey());
            cartonAuditResultLine.setExpectedQuantity(expectedItemWithQuantity.getValue());
            cartonAuditResultLine.setAuditQuantity(0);
            cartonAuditResultLine.setCartonAuditResult(cartonAuditResult);
            cartonAuditResult.addCartonAuditResultLine(cartonAuditResultLine);
        }
    }


}
