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
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.repository.AreaRepository;
import se.gzhang.scm.wms.layout.repository.LocationRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("locationService")
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public Location findByLocationId(int id) {
        return locationRepository.findById(id);
    }

    public List<Location> findAll() {

        return locationRepository.findAll();
    }


    public List<Location> findLocation(Map<String, String> criteriaList) {
        System.out.println("Find location with following criteria");
        for(Map.Entry<String, String> entry : criteriaList.entrySet()) {
            System.out.println("name: " + entry.getKey() + " , value: " + entry.getValue());
        }
        return locationRepository.findAll(new Specification<Location>() {
            @Override
            public Predicate toPredicate(Root<Location> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(criteriaList.containsKey("buildingID") && !criteriaList.get("buildingID").isEmpty()) {
                    Join<Location, Area> joinArea = root.join("area",JoinType.INNER);
                    Join<Area, Building> joinBuilding = joinArea.join("building",JoinType.INNER);

                    predicates.add(criteriaBuilder.equal(joinBuilding.get("id"), criteriaList.get("buildingID")));

                    // if area id is passed in, we will include it here so we don't have to have
                    // join area table twice if both area id and building id is passed in
                    if(criteriaList.containsKey("areaID") && !criteriaList.get("areaID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinArea.get("id"), criteriaList.get("areaID")));
                    }
                }
                else if (criteriaList.containsKey("areaID") && !criteriaList.get("areaID").isEmpty()) {
                    Join<Location, Area> joinArea = root.join("area",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinArea.get("id"), criteriaList.get("areaID")));
                }

                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }
                if(criteriaList.containsKey("aisleID") && !criteriaList.get("aisleID").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("aisleID"), criteriaList.get("aisleID")));
                }
                if(criteriaList.containsKey("pickable") && !criteriaList.get("pickable").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("pickable"), criteriaList.get("pickable")));
                }
                if(criteriaList.containsKey("storable") && !criteriaList.get("storable").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("storable"), criteriaList.get("storable")));
                }
                if(criteriaList.containsKey("usable") && !criteriaList.get("usable").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("usable"), criteriaList.get("usable")));
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });

    }

    public Location save(Location location) {

        Location newLocation = locationRepository.save(location);
        locationRepository.flush();
        return newLocation;
    }
}
