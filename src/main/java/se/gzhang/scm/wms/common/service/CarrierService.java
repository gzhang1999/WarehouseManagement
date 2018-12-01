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

package se.gzhang.scm.wms.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.repository.CarrierRepository;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CarrierService {

    @Autowired
    CarrierRepository carrierRepository;

    public List<Carrier> findAll(){

        return carrierRepository.findAll();
    }

    public Carrier findByCarrierId(int id){
        return carrierRepository.findById(id);
    }

    public Carrier findByCarrierName(String name) {
        return carrierRepository.findByName(name);
    }



    public Carrier save(Carrier carrier) {
        Carrier newCarrier = carrierRepository.save(carrier);
        carrierRepository.flush();
        return newCarrier;
    }


    public List<Carrier> findCarriers(Map<String, String> criteriaList) {
        return carrierRepository.findAll(new Specification<Carrier>() {
            @Override
            public Predicate toPredicate(Root<Carrier> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public void deleteByCarrierID(int carrierID) {
        carrierRepository.deleteById(carrierID);
    }
}
