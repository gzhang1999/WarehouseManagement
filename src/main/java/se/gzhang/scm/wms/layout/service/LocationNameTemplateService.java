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

package se.gzhang.scm.wms.layout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.layout.model.LocationNameTemplate;
import se.gzhang.scm.wms.layout.repository.LocationNameTemplateRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("locationNameTemplateService")
public class LocationNameTemplateService {
    @Autowired
    LocationNameTemplateRepository locationNameTemplateRepository;

    public LocationNameTemplate findByLocationNameTemplateID(int id) {
        return locationNameTemplateRepository.findById(id);

    }

    @Transactional
    public void deleteByLocationNameTemplateID(int id){
        locationNameTemplateRepository.deleteById(id);
    }

    public List<LocationNameTemplate> findAll() {
        return locationNameTemplateRepository.findAll();
    }


    public List<LocationNameTemplate> findLocationNameTemplate(Map<String, String> criteriaList) {
        return locationNameTemplateRepository.findAll(new Specification<LocationNameTemplate>() {
            @Override
            public Predicate toPredicate(Root<LocationNameTemplate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });

    }

    @Transactional
    public LocationNameTemplate save(LocationNameTemplate locationNameTemplate) {
        return locationNameTemplateRepository.save(locationNameTemplate);
    }
}
