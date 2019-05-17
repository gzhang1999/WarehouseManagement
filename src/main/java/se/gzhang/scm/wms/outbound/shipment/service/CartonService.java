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
import se.gzhang.scm.wms.framework.controls.service.UniversalIdentifierService;
import se.gzhang.scm.wms.outbound.shipment.model.Carton;
import se.gzhang.scm.wms.outbound.shipment.model.CartonType;
import se.gzhang.scm.wms.outbound.shipment.model.Pick;
import se.gzhang.scm.wms.outbound.shipment.repository.CartonRepository;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class CartonService {

    @Autowired
    private CartonRepository cartonRepository;

    @Autowired
    private CartonTypeService cartonTypeService;

    @Autowired
    private PickService pickService;

    @Autowired
    private UniversalIdentifierService universalIdentifierService;

    public List<Carton> findAll(){

        return cartonRepository.findAll();
    }

    public Carton findByCartonId(int id){
        return cartonRepository.findById(id);
    }
    public Carton findByCartonNumber(String number){
        return cartonRepository.findByNumber(number);
    }

    @Transactional
    public Carton save(Carton carton) {
        return cartonRepository.save(carton);
    }

    public Carton createCarton(String number, CartonType cartonType) {
        Carton carton = new Carton();
        carton.setNumber(number);
        carton.setCartonType(cartonType);
        return carton;
    }

    public List<Carton> findCarton(Map<String, String> criteriaList) {

        return cartonRepository.findAll(new Specification<Carton>() {
            @Override
            public Predicate toPredicate(Root<Carton> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("number") && !criteriaList.get("number").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("number"), criteriaList.get("number")));
                }
                if(criteriaList.containsKey("cartonType") && !criteriaList.get("cartonType").isEmpty()) {
                    Join<Carton, CartonType> joinCaronType = root.join("cartonType",JoinType.INNER);

                    predicates.add(criteriaBuilder.equal(joinCaronType.get("name"), criteriaList.get("cartonType")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }


    // Find a list of carton type for the list of picks
    // The goal is to find a list of cartons that
    // 1. we won't split pick into 2 different cartons
    // 2. we should have as less cost as possible
    // TO-DO: 5/10/2019, now we only support volume based calculation
    @Transactional
    public void assignCarton(List<Pick> pickList){

        List<CartonType> enabledCartonTypes = cartonTypeService.getEnabledCartonTypes();

        // Sort the carton by size, ascending sequence
        // As of 5/10/2019, we will only support cartonization based
        // on volume
        Collections.sort(enabledCartonTypes, new Comparator<CartonType>() {
            @Override
            public int compare(CartonType cartonType1, CartonType cartonType2) {
                if (cartonType1.getVolume() * cartonType1.getFillRate() < cartonType2.getVolume() * cartonType2.getFillRate()) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
        });

        assignCartonByVolume(pickList, enabledCartonTypes);

    }

    @Transactional
    public void assignCartonByVolume(List<Pick> pickList, List<CartonType> cartonTypeList) {

        // At this point, we have a list of picks
        // and we have a list of carton types sort by volume on ascending sequence
        // We first will need to make sure the single pick is not too big for the
        // largest carton
        List<Pick> oversizePickList = new ArrayList<>();
        CartonType largestCarton = cartonTypeList.get(cartonTypeList.size()-1);
        for (Pick pick: pickList) {
            if (pickService.getEstimatedSize(pick) > largestCarton.getVolume() * largestCarton.getFillRate()) {
                oversizePickList.add(pick);
            }
        }
        if (oversizePickList.size() > 0) {
            // we have oversize pick, we will split the pick into 2 picks, the first will have
            // the quantity that can be fit into the largest carton and the balance goes into
            // the second pick
            for(Pick pick : oversizePickList) {
                double eachItemSize = pickService.getEstimatedSize(pick) / pick.getQuantity();
                int newPickQuantity = (int)((largestCarton.getVolume() * largestCarton.getFillRate()) / eachItemSize);
                Pick newPick = pickService.splitPick(pick, newPickQuantity);
                pickService.save(pick);
                newPick = pickService.save(newPick);
                pickList.add(newPick);
                // save both the current pick and the new pick.
            }
            assignCartonByVolume(pickList, cartonTypeList);
        }

        // at this point, we should not have any oversize picks

        // Sort the pick list by volume
        Collections.sort(pickList, new Comparator<Pick>() {
            @Override
            public int compare(Pick pick1, Pick pick2) {
                if (pickService.getEstimatedSize(pick1) > pickService.getEstimatedSize(pick2)) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
        });

        // we will start from the largest picks and give it a carton
        // then we will add one more pick to the same carton to see if we can fit into the same carton
        // or we will need to open a new carton. The new carton needs to be as small as possible
        CartonType cartonTypeInProcess = null;
        double totalSizeInProcess = 0.0d;
        List<Pick> picksInProcess = new ArrayList<>();
        for (Pick pick : pickList) {
            if (cartonTypeInProcess == null) {
                // ok we haven't start the carton process yet, let's get the most suitable
                // carton for our very first pick
                double pickSize = pickService.getEstimatedSize(pick);
                cartonTypeInProcess = cartonTypeService.getMostSuitableCartonType(cartonTypeList, pickSize);
                picksInProcess.add(pick);
                totalSizeInProcess += pickSize;
            }
            else {
                // we already have a carton in process and should have some picks
                // that already grouped into this carton. Let's see the size that is
                // already used by the picks
                double pickSize = pickService.getEstimatedSize(pick);
                CartonType cartonType = cartonTypeService.getMostSuitableCartonType(cartonTypeList, (totalSizeInProcess + pickSize));
                if (cartonType == null) {
                    // OK, it is time to start a new carton
                    // we will assign a new carton id for those picks in process and then
                    // start a new carton for current pick
                    String cartonNumber = universalIdentifierService.getNextNumber("carton_number");
                    Carton carton = createCarton(cartonNumber, cartonTypeInProcess);
                    save(carton);
                    for(Pick pickInProcess : picksInProcess) {
                        pickInProcess.setCarton(carton);
                        pickService.save(pickInProcess);
                    }
                    picksInProcess.clear();
                    totalSizeInProcess = 0.0d;

                    // start with a new carton
                    cartonTypeInProcess  = cartonTypeService.getMostSuitableCartonType(cartonTypeList, pickSize);
                    picksInProcess.add(pick);
                    totalSizeInProcess += pickSize;
                }
                else {
                    // OK, we can add current pick to the current carton
                    cartonTypeInProcess = cartonType;
                    picksInProcess.add(pick);
                    totalSizeInProcess += pickSize;
                }
            }
        }
        // after we loop through all the picks, for the last batch of picks, we still haven't assign any carton
        // to it yet
        String cartonNumber = universalIdentifierService.getNextNumber("carton_number");
        Carton carton = createCarton(cartonNumber, cartonTypeInProcess);
        save(carton);
        for(Pick pickInProcess : picksInProcess) {
            pickInProcess.setCarton(carton);
            pickService.save(pickInProcess);
        }
    }


}
