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
import se.gzhang.scm.wms.layout.model.AreaGroup;
import se.gzhang.scm.wms.layout.model.LocationGroup;
import se.gzhang.scm.wms.layout.repository.AreaGroupRepository;
import se.gzhang.scm.wms.layout.repository.LocationGroupRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LocationGroupService {
    @Autowired
    private LocationGroupRepository locationGroupRepository;

    public LocationGroup findByLocationGroupId(int id) {
        return locationGroupRepository.findById(id);
    }

    public LocationGroup findByLocationGroupName(String name) {
        return locationGroupRepository.findByName(name);

    }
    public List<LocationGroup> findAll() {

        return locationGroupRepository.findAll();
    }

    @Transactional
    public void deleteLocationGroupByLocationGroupId(int id) {
        locationGroupRepository.deleteById(id);
    }


    public List<LocationGroup> findLocationGroup(Map<String, String> criteriaList) {
        return locationGroupRepository.findAll(new Specification<LocationGroup>() {
            @Override
            public Predicate toPredicate(Root<LocationGroup> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {

                    predicates.add(criteriaBuilder.like(root.get("name"), criteriaList.get("name")));
                }

                if(criteriaList.containsKey("description") && !criteriaList.get("description").isEmpty()) {

                    predicates.add(criteriaBuilder.like(root.get("description"), criteriaList.get("description")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });

    }

    @Transactional
    public LocationGroup save(LocationGroup locationGroup) {
        return locationGroupRepository.save(locationGroup);
    }
}
