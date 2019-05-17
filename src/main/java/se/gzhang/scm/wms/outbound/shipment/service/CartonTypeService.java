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
import se.gzhang.scm.wms.exception.Outbound.AllocationResultException;
import se.gzhang.scm.wms.framework.controls.model.UniversalIdentifier;
import se.gzhang.scm.wms.framework.controls.service.UniversalIdentifierService;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.outbound.shipment.model.AllocationResult;
import se.gzhang.scm.wms.outbound.shipment.model.CartonType;
import se.gzhang.scm.wms.outbound.shipment.model.Pick;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.repository.AllocationResultRepository;
import se.gzhang.scm.wms.outbound.shipment.repository.CartonTypeRepository;

import javax.persistence.Transient;
import javax.persistence.criteria.*;
import java.util.*;

@Service
public class CartonTypeService {

    @Autowired
    private CartonTypeRepository cartonTypeRepository;


    public List<CartonType> findAll(){

        return cartonTypeRepository.findAll();
    }

    public CartonType findByCartonTypeId(int id){
        return cartonTypeRepository.findById(id);
    }
    public CartonType findByCartonTypeName(String name){
        return cartonTypeRepository.findByName(name);
    }

    @Transactional
    public CartonType save(CartonType cartonType) {
        return cartonTypeRepository.save(cartonType);
    }

    public List<CartonType> findCartonTypes(Map<String, String> criteriaList) {

        return cartonTypeRepository.findAll(new Specification<CartonType>() {
            @Override
            public Predicate toPredicate(Root<CartonType> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }
                if(criteriaList.containsKey("enabled") && !criteriaList.get("enabled").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("enabled"), Boolean.valueOf(criteriaList.get("enabled"))));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    @Transactional
    public CartonType createCartonType(String name,
                                       String description,
                                       Double length,
                                       Double width,
                                       Double height,
                                       Double fillRate,
                                       Double weight,
                                       Double weightCapacity,
                                       Double cost) {
        CartonType cartonType = new CartonType();
        cartonType.setName(name);
        cartonType.setDescription(description);
        cartonType.setLength(length);
        cartonType.setWidth(width);
        cartonType.setHeight(height);
        cartonType.setFillRate(fillRate);
        cartonType.setWeight(weight);
        cartonType.setWeightCapacity(weightCapacity);
        cartonType.setCost(cost);
        return save(cartonType);
    }

    public List<CartonType> getEnabledCartonTypes() {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("enabled", "true");
        return findCartonTypes(criteriaList);
    }



    @Transactional
    public CartonType setCartonTypeEnable(CartonType cartonType, boolean enabled) {
        cartonType.setEnabled(enabled);
        return save(cartonType);
    }

    // The sortedCartonType will be a list of carton type that is sorted from
    // smallest volume to biggest volume
    public CartonType getMostSuitableCartonType(List<CartonType> sortedCartonType, double size) {
        for(CartonType cartonType: sortedCartonType) {
            if (cartonType.getVolume() * cartonType.getFillRate() > size) {
                return cartonType;
            }
        }
        return null;
    }
}
