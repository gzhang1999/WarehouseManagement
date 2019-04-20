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

package se.gzhang.scm.wms.inbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.model.TrailerState;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inbound.model.PutawayPolicy;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inbound.repository.PutawayPolicyRepository;
import se.gzhang.scm.wms.inbound.repository.ReceiptRepository;
import se.gzhang.scm.wms.inventory.model.*;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.inventory.service.InventoryStatusService;
import se.gzhang.scm.wms.inventory.service.ItemFootprintService;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.LocationStatus;
import se.gzhang.scm.wms.layout.service.LocationService;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class PutawayPolicyService {

    @Autowired
    PutawayPolicyRepository putawayPolicyRepository;

    @Autowired
    InventoryService inventoryService;
    @Autowired
    LocationService locationService;

    public List<PutawayPolicy> findAll(){

        return putawayPolicyRepository.findAllByOrderBySequenceAsc();
    }

    public PutawayPolicy findByPutawayPolicyId(int id){
        return putawayPolicyRepository.findById(id);
    }


    public PutawayPolicy save(PutawayPolicy putawayPolicy) {
        PutawayPolicy newPutawayPolicy = putawayPolicyRepository.save(putawayPolicy);
        putawayPolicyRepository.flush();
        return newPutawayPolicy;
    }


    public List<PutawayPolicy> findPutawayPolicys(Map<String, String> criteriaList) {
        List<PutawayPolicy> putawayPolicyList =
         putawayPolicyRepository.findAll(new Specification<PutawayPolicy>() {
            @Override
            public Predicate toPredicate(Root<PutawayPolicy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("sequence") && !criteriaList.get("sequence").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("sequence"), criteriaList.get("sequencew")));
                }
                if(criteriaList.containsKey("item") && !criteriaList.get("item").isEmpty()) {
                    Join<PutawayPolicy, Item> joinItem = root.join("item",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinItem.get("name"), criteriaList.get("item")));
                }
                if(criteriaList.containsKey("itemFamily") && !criteriaList.get("itemFamily").isEmpty()) {
                    Join<PutawayPolicy, ItemFamily> joinItemFamily = root.join("itemFamily",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinItemFamily.get("name"), criteriaList.get("itemFamily")));
                }
                if(criteriaList.containsKey("supplier") && !criteriaList.get("supplier").isEmpty()) {
                    Join<PutawayPolicy, Supplier> joinSupplier = root.join("supplier",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinSupplier.get("name"), criteriaList.get("supplier")));
                }
                if(criteriaList.containsKey("trailerNumber") && !criteriaList.get("trailerNumber").isEmpty()) {
                    Join<PutawayPolicy, Trailer> joinTrailer = root.join("trailer",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinTrailer.get("trailerNumber"), criteriaList.get("trailerNumber")));
                }
                if(criteriaList.containsKey("receiptNumber") && !criteriaList.get("receiptNumber").isEmpty()) {
                    Join<PutawayPolicy, Receipt> joinReceipt = root.join("receipt",JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(joinReceipt.get("number"), criteriaList.get("receiptNumber")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });


        return putawayPolicyList;
    }

    public void deleteByPutawayPolicyID(int putawayPolicyID) {
        putawayPolicyRepository.deleteById(putawayPolicyID);
    }

    public Location getPutawayDestination(PutawayPolicy putawayPolicy, Inventory inventory) {
        // First, let's check whether the inventory matches with the putaway policy

        if (checkInventoryMatchWithPutawayPolicy(putawayPolicy, inventory)) {
            return getSingleDestinationLocation(putawayPolicy, inventory);

        } else {
            return null;
        }
    }

    private boolean checkInventoryMatchWithPutawayPolicy(PutawayPolicy putawayPolicy, Inventory inventory) {
        if (putawayPolicy.getItem() != null &&
            !putawayPolicy.getItem().equals(inventory.getItemFootprint().getItem())) {
            return false;
        }
        if (putawayPolicy.getItemFamily() != null &&
            putawayPolicy.getItemFamily().equals(inventory.getItemFootprint().getItem().getItemFamily())) {
            return false;
        }
        if (putawayPolicy.getSupplier() != null ||
            putawayPolicy.getReceipt() != null ||
            putawayPolicy.getTrailer() != null) {
            // inventory needs to match with receipt / supplier information
            if (inventory.getReceiptLine() == null) {
                return false;
            }
            if (putawayPolicy.getSupplier() != null &&
                    inventory.getReceiptLine().getReceipt().getSupplier().equals(putawayPolicy.getSupplier())) {
                return false;
            }
            if (putawayPolicy.getReceipt() != null &&
                    inventory.getReceiptLine().getReceipt().equals(putawayPolicy.getReceipt())) {
                return false;
            }
            if (putawayPolicy.getTrailer() != null &&
                    inventory.getReceiptLine().getReceipt().getTrailer().equals(putawayPolicy.getTrailer())) {
                return false;
            }
        }
        if (putawayPolicy.getMinimumSize() != null ||
                putawayPolicy.getMaximumSize() != null ||
                putawayPolicy.getMinimumWeight() != null ||
                putawayPolicy.getMaximumWeight() != null) {
            // Let's get current inventory size and weight first
            double size = inventoryService.getSize(inventory);
            double weight = inventoryService.getWeight(inventory);

            if (putawayPolicy.getMinimumSize() != null &&
                    putawayPolicy.getMinimumSize() > 0 &&
                    size < putawayPolicy.getMinimumSize()) {
                return false;
            }
            if (putawayPolicy.getMaximumSize() != null &&
                    putawayPolicy.getMaximumSize() > 0 &&
                    size > putawayPolicy.getMaximumSize()) {
                return false;
            }
            if (putawayPolicy.getMinimumWeight() != null &&
                    putawayPolicy.getMinimumWeight() > 0 &&
                    weight < putawayPolicy.getMinimumWeight()) {
                return false;
            }
            if (putawayPolicy.getMaximumWeight() != null &&
                    putawayPolicy.getMaximumWeight() > 0 &&
                    weight > putawayPolicy.getMaximumWeight()) {
                return false;
            }

        }

        // If we are still here, it means we passed all the
        // validation and the inventory matches with the putaway
        // policy. let's get the right location
        return true;
    }

    public List<PutawayPolicy> getMatchedPutawayPolicy(Inventory inventory) {
        List<PutawayPolicy> putawayPolicyList = findAll();
        List<PutawayPolicy> matchedPutawayPolicyList = new ArrayList<>(putawayPolicyList.size());
        for(PutawayPolicy putawayPolicy : putawayPolicyList) {
            if (checkInventoryMatchWithPutawayPolicy(putawayPolicy, inventory)) {
                matchedPutawayPolicyList.add(putawayPolicy);
            }
        }
        return matchedPutawayPolicyList;
    }

    public PutawayPolicy getMostMatchedPutawayPolicy(Inventory inventory) {
        List<PutawayPolicy> putawayPolicyList = getMatchedPutawayPolicy(inventory);
        if (putawayPolicyList.size() == 0) {
            return null;
        }
        else {
            return putawayPolicyList.get(0);
        }
    }

    private List<Location> findLocationsByPutawayPolicy(PutawayPolicy putawayPolicy){

        Map<String, String> locationFilter = new HashMap<>();
        // Check if we will need to restrict by area / area group
        if (putawayPolicy.getAreaGroup() != null) {
            locationFilter.put("areaGroup", putawayPolicy.getAreaGroup().getName());
        }
        if (putawayPolicy.getArea() != null) {
            locationFilter.put("areaID", String.valueOf(putawayPolicy.getArea().getId()));
        }
        if (putawayPolicy.getLocation() != null) {
            locationFilter.put("name", putawayPolicy.getLocation().getName());
        }
        if (putawayPolicy.getLocationGroup() != null) {
            locationFilter.put("locationGroup", putawayPolicy.getLocationGroup().getName());
        }
        if (putawayPolicy.getLocationLevel() != null) {
            locationFilter.put("level", putawayPolicy.getLocationLevel());
        }
        if (putawayPolicy.getLocationAisleID() != null) {
            locationFilter.put("aisleID", putawayPolicy.getLocationAisleID());
        }
        return locationService.findLocation(locationFilter);
    }

    private List<Location> getValidPutawayLocation(List<Location> locationList, PutawayPolicy putawayPolicy, Inventory inventory) {
        // valid the locations from passed in list
        // 1. the location needs to have enough room
        // 2. match with the mix rule
        // 3. match with the putaway strategy
        return validatePutawayLocationByPutawayStrategy(
                validatePutawayLocationByMixingRule(
                        validatePutawayLocationBySize(locationList, inventory), inventory),
                putawayPolicy);

    }

    private List<Location> validatePutawayLocationBySize(List<Location> locationList, Inventory inventory) {

        Iterator<Location> locationIterator = locationList.iterator();
        while(locationIterator.hasNext()) {
            Location location = locationIterator.next();
            if (locationService.getEmptyVolume(location) <
                    inventoryService.getSize(inventory)) {
                locationIterator.remove();
            }
        }
        return locationList;
    }
    private List<Location> validatePutawayLocationByMixingRule(List<Location> locationList, Inventory inventory) {

        // TO-DO: Validate if the inventory can be put away into the location
        // based upon the mixing rule
        return locationList;
    }
    private List<Location> validatePutawayLocationByPutawayStrategy(List<Location> locationList, PutawayPolicy putawayPolicy) {

        Iterator<Location> locationIterator = locationList.iterator();
        while(locationIterator.hasNext()) {
            Location location = locationIterator.next();
            switch (putawayPolicy.getPutawayPolicyStrategy()) {
                case EMPTY_LOCATION_ONLY:
                    if (location.getUsedVolume() > 0 || location.getPendingVolume() > 0) {
                        locationIterator.remove();
                    }
                    break;
                case MIXED_LOCATION_ONLY:
                    if (location.getUsedVolume() == 0d && location.getPendingVolume() == 0d) {
                        locationIterator.remove();
                    }
                    break;
            }
        }
        return locationList;
    }

    // return the single location from the locations that matches policy
    private Location getSingleDestinationLocation(PutawayPolicy putawayPolicy, Inventory inventory) {

        List<Location> suitableLocationList = findLocationsByPutawayPolicy(putawayPolicy);
        System.out.println("Get " + suitableLocationList.size() + " location for current inventory");

        // validate the locations by weight and size
        List<Location> validateLocation = getValidPutawayLocation(suitableLocationList, putawayPolicy, inventory);

        validateLocation = sortValidPutawayLocation(validateLocation, putawayPolicy);

        if (validateLocation.size() > 0) {
            return validateLocation.get(0);
        }
        return null;
    }

    private List<Location> sortValidPutawayLocation(List<Location> locationList, PutawayPolicy putawayPolicy) {
        // If policy is
        // 1. Mixed location first: then always return mixed location first
        // 2. Empty location first: always return empty location first
        // 3. otherwise, return location with less empty volume first
        //           Location with same volume, return based on putaway travel sequence
        switch (putawayPolicy.getPutawayPolicyStrategy()) {
            case MIXED_LOCATION_FIRST:
                locationList.sort(new Comparator<Location>() {
                    @Override
                    public int compare(Location o1, Location o2) {
                        if (o1.getLocationStatus() == LocationStatus.EMPTY &&
                                o2.getLocationStatus() != LocationStatus.EMPTY) {
                            // first location is truly empty while second location is not
                            // let's return second location first
                            return -1;
                        }
                        else if (o1.getLocationStatus() != LocationStatus.EMPTY &&
                                o2.getLocationStatus() == LocationStatus.EMPTY) {
                            // second location is truly empty while first location is not
                            // let's return first location first
                            return 1;
                        }
                        else {
                            // both location are equal based up the status, let's return
                            // based on the volume and travel sequence
                            int o1_emptyVolume = (int) locationService.getEmptyVolume(o1);
                            int o2_emptyVolume = (int) locationService.getEmptyVolume(o2);
                            if (o1_emptyVolume == o2_emptyVolume) {
                                return o1.getPutawaySequence() - o2.getPutawaySequence();
                            } else {
                                return o1_emptyVolume - o2_emptyVolume;
                            }
                        }
                    }
                });
                break;
            case EMPTY_LOCATION_FIRST:
                locationList.sort(new Comparator<Location>() {
                    @Override
                    public int compare(Location o1, Location o2) {
                        if (o1.getLocationStatus() == LocationStatus.EMPTY &&
                                o2.getLocationStatus() != LocationStatus.EMPTY) {
                            // first location is truly empty while second location is not
                            // let's return FIRST location first
                            return 1;
                        }
                        else if (o1.getLocationStatus() != LocationStatus.EMPTY &&
                                o2.getLocationStatus() == LocationStatus.EMPTY) {
                            // second location is truly empty while first location is not
                            // let's return SECOND location first
                            return -1;
                        }
                        else {
                            // both location are equal based up the status, let's return
                            // based on the volume and travel sequence
                            int o1_emptyVolume = (int) locationService.getEmptyVolume(o1);
                            int o2_emptyVolume = (int) locationService.getEmptyVolume(o2);
                            if (o1_emptyVolume == o2_emptyVolume) {
                                return o1.getPutawaySequence() - o2.getPutawaySequence();
                            } else {
                                return o1_emptyVolume - o2_emptyVolume;
                            }
                        }
                    }
                });
                break;
            default:
                locationList.sort(new Comparator<Location>() {
                    @Override
                    public int compare(Location o1, Location o2) {
                        // both location are equal based up the status, let's return
                        // based on the volume and travel sequence
                        int o1_emptyVolume = (int) locationService.getEmptyVolume(o1);
                        int o2_emptyVolume = (int) locationService.getEmptyVolume(o2);
                        if (o1_emptyVolume == o2_emptyVolume) {
                            return o1.getPutawaySequence() - o2.getPutawaySequence();
                        } else {
                            return o1_emptyVolume - o2_emptyVolume;
                        }
                    }
                });

        }
        return locationList;
    }

}
