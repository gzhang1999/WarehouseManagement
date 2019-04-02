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
import se.gzhang.scm.wms.common.model.Velocity;
import se.gzhang.scm.wms.common.service.VelocityService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.*;
import se.gzhang.scm.wms.layout.repository.LocationRepository;
import se.gzhang.scm.wms.system.tools.service.FileUploadOptionService;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("locationService")
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AreaService areaService;

    @Autowired
    private VelocityService velocityService;

    @Autowired
    private FileUploadOptionService fileUploadOptionService;

    @Autowired
    private InventoryService inventoryService;

    public Location findByLocationId(int id) {
        return locationRepository.findById(id);
    }
    public Location findByLocationName(String name) {
        return locationRepository.findByName(name);
    }
    public void deleteByLocationId(int id) {
        locationRepository.deleteById(id);
    }

    public List<Location> findAll() {

        return locationRepository.findAll();
    }



    public List<Location> findLocation(Map<String, String> criteriaList) {
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
                    if(criteriaList.containsKey("areaName") && !criteriaList.get("areaName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinArea.get("name"), criteriaList.get("areaName")));
                    }
                    if(criteriaList.containsKey("areaType") && !criteriaList.get("areaType").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinArea.get("areaType"), AreaType.valueOf(criteriaList.get("areaType"))));
                    }
                }
                else if ((criteriaList.containsKey("areaID") && !criteriaList.get("areaID").isEmpty()) ||
                        (criteriaList.containsKey("areaName") && !criteriaList.get("areaName").isEmpty()) ||
                        (criteriaList.containsKey("areaType") && !criteriaList.get("areaType").isEmpty())) {
                    Join<Location, Area> joinArea = root.join("area",JoinType.INNER);

                    if(criteriaList.containsKey("areaID") && !criteriaList.get("areaID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinArea.get("id"), criteriaList.get("areaID")));
                    }
                    if(criteriaList.containsKey("areaName") && !criteriaList.get("areaName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinArea.get("name"), criteriaList.get("areaName")));
                    }
                    if(criteriaList.containsKey("areaType") && !criteriaList.get("areaType").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinArea.get("areaType"), AreaType.valueOf(criteriaList.get("areaType"))));
                    }
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

    public List<Location> loadFromFile(String[] columnNameList, List<String> locations, String processID) {


        List<Location> locationList = new ArrayList<>();

        // A local temporary map to store the area according to the area name
        // so that we don't have to get the area again and again for the
        // locations from the same area
        Map<String, Area> areaMap = new HashMap<>();

        for(String locationString : locations) {

            String[] locationAttributeList = locationString.split(",");
            if (columnNameList.length != locationAttributeList.length) {
                continue;
            }
            try {
                Location location = setupLocation(columnNameList, locationAttributeList, areaMap);

                locationList.add(save(location));

                fileUploadOptionService.increaseRecordNumberLoaded(processID, true);

            }
            catch (GenericException ex) {

                fileUploadOptionService.increaseRecordNumberLoaded(processID, false);
            }
        }
        return locationList;
    }

    private Area getAreaByName(String areaName) {
        return areaService.findByAreaName(areaName);
    }

    private Location setupLocation(String[] columnNameList, String[] locationAttributeList, Map<String, Area> areaMap)
        throws GenericException {

        Area area = null;
        String locationName="", aisle="";
        double length=0.0,width=0.0,height=0.0, volume=0.0;
        double coordinateX=0.0, coordinateY=0.0, coordinateZ=0.0;
        boolean pickable=false, storable=false, usable=false;
        Velocity velocity=null;


        for(int i = 0; i < columnNameList.length; i++) {
            String columnName = columnNameList[i];
            String locationAttribute = locationAttributeList[i];
            if (columnName.equalsIgnoreCase("area")){
                if (areaMap.containsKey(locationAttribute)) {
                    area = areaMap.get(locationAttribute);
                }
                else {
                    area = getAreaByName(locationAttribute);
                    if (area == null) {
                        throw new GenericException(10000, "can't find area with code: " + locationAttribute);
                    }
                    areaMap.put(locationAttribute, area);
                }
            }
            else if (columnName.equalsIgnoreCase("location")){
                locationName = locationAttribute;
            }
            else if (columnName.equalsIgnoreCase("length")){
                length = Double.parseDouble(locationAttribute);

            }
            else if (columnName.equalsIgnoreCase("width")){
                width = Double.parseDouble(locationAttribute);

            }
            else if (columnName.equalsIgnoreCase("height")){
                height = Double.parseDouble(locationAttribute);

            }
            else if (columnName.equalsIgnoreCase("x")){
                coordinateX = Double.parseDouble(locationAttribute);

            }
            else if (columnName.equalsIgnoreCase("y")){
                coordinateY = Double.parseDouble(locationAttribute);

            }
            else if (columnName.equalsIgnoreCase("z")){
                coordinateZ = Double.parseDouble(locationAttribute);

            }
            else if (columnName.equalsIgnoreCase("pickable")){
                pickable = locationAttribute.equalsIgnoreCase("1");
            }
            else if (columnName.equalsIgnoreCase("storable")){
                storable = locationAttribute.equalsIgnoreCase("1");

            }
            else if (columnName.equalsIgnoreCase("usable")){
                usable = locationAttribute.equalsIgnoreCase("1");

            }
            else if (columnName.equalsIgnoreCase("aisle")){
                aisle = locationAttribute;

            }
            else if (columnName.equalsIgnoreCase("volume")){

                volume = Double.parseDouble(locationAttribute);
            }
            else if (columnName.equalsIgnoreCase("velocity")){
                velocity = velocityService.findByVelocityName(locationAttribute);
                if (velocity == null) {
                    velocity = velocityService.findAll().get(0);
                }
            }
        }

        // If location already exists, let's change the existing location
        // otherwise, let's create a new location
        Location location = findByLocationName(locationName);
        if (location == null){
            location = new Location();
            location.setName(locationName);
        }
        location.setArea(area);
        location.setAisleID(aisle);
        location.setPickable(pickable);
        location.setStorable(storable);
        location.setUsable(usable);
        location.setLength(length);
        location.setWidth(width);
        location.setHeight(height);
        location.setVolume(volume);
        location.setVelocity(velocity);
        location.setCoordinateX(coordinateX);
        location.setCoordinateY(coordinateY);
        location.setCoordinateZ(coordinateZ);

        return location;
    }

    // Allocate the location to the inventory as a destination
    public void allocateLocation(Location location, Inventory inventory) {
        inventory.setDestinationLocation(location);
        inventoryService.save(inventory);
        if (location.getArea().getVolumeType().equals(VolumeType.EACH)) {
            location.setPendingVolumn(location.getPendingVolumn() + inventory.getQuantity());
            save(location);
        }
        else {
            // By default, we will use size(length * width * height) to calculate
            // the size of the inventory and location
            double size = inventoryService.getSize(inventory);
            location.setPendingVolumn(location.getPendingVolumn() + size);
            save(location);
        }
    }

    // de-allocate(release) the location from the inventory
    public void deallocateLocation(Location location, Inventory inventory) {
        inventory.setDestinationLocation(null);
        inventoryService.save(inventory);
        if (location.getArea().getVolumeType().equals(VolumeType.EACH)) {
            location.setPendingVolumn(location.getPendingVolumn() - inventory.getQuantity());
            save(location);
        }
        else {
            // By default, we will use size(length * width * height) to calculate
            // the size of the inventory and location
            double size = inventoryService.getSize(inventory);
            location.setPendingVolumn(location.getPendingVolumn() - size);
            save(location);
        }
    }
}
