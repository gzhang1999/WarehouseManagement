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
import se.gzhang.scm.wms.layout.model.AreaType;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.repository.AreaRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("areaService")
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;

    public Area findByAreaId(int id) {
        return areaRepository.findById(id);
    }

    public Area findByAreaName(String name) {
        return areaRepository.findByName(name);

    }
    public List<Area> findAll() {

        return areaRepository.findAll();
    }
    public void deleteAreaByAreaId(int id) {
        areaRepository.deleteById(id);
    }


    public List<Area> findArea(Map<String, String> criteriaList) {
        return areaRepository.findAll(new Specification<Area>() {
            @Override
            public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(criteriaList.containsKey("buildingID") && !criteriaList.get("buildingID").isEmpty()) {
                    Join<Area, Building> joinBuilding = root.join("building",JoinType.INNER);

                    predicates.add(criteriaBuilder.equal(joinBuilding.get("id"), criteriaList.get("buildingID")));
                }

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }

                if(criteriaList.containsKey("areaType") && !criteriaList.get("areaType").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("areaType"), AreaType.valueOf(criteriaList.get("areaType"))));
                }


                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });

    }

    public List<Area> findReceingStageAreas() {
        Map<String, String> receivingStageAreaCriteria = new HashMap<>();
        receivingStageAreaCriteria.put("areaType", AreaType.INBOUND_STAGE.name());
        return findArea(receivingStageAreaCriteria);
    }

    public Area save(Area area) {
        Area newArea = areaRepository.save(area);
        areaRepository.flush();
        return newArea;
    }

    public List<Area> getShipppingStageAreas() {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("areaType", AreaType.OUTBOUND_STAGE.name());
        return findArea(criteriaList);
    }

}
