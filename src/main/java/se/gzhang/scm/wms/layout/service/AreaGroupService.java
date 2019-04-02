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
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.AreaGroup;
import se.gzhang.scm.wms.layout.model.AreaType;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.repository.AreaGroupRepository;
import se.gzhang.scm.wms.layout.repository.AreaRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class AreaGroupService {
    @Autowired
    private AreaGroupRepository areaGroupRepository;

    public AreaGroup findByAreaGroupId(int id) {
        return areaGroupRepository.findById(id);
    }

    public AreaGroup findByAreaGroupName(String name) {
        return areaGroupRepository.findByName(name);

    }
    public List<AreaGroup> findAll() {

        return areaGroupRepository.findAll();
    }
    public void deleteAreaGroupByAreaGroupId(int id) {
        areaGroupRepository.deleteById(id);
    }


    public List<AreaGroup> findAreaGroup(Map<String, String> criteriaList) {
        return areaGroupRepository.findAll(new Specification<AreaGroup>() {
            @Override
            public Predicate toPredicate(Root<AreaGroup> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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

    public AreaGroup save(AreaGroup areaGroup) {
        AreaGroup newAreaGroup = areaGroupRepository.save(areaGroup);
        areaGroupRepository.flush();
        return newAreaGroup;
    }
}
