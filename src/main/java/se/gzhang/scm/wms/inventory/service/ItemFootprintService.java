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

package se.gzhang.scm.wms.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.common.model.Client;
import se.gzhang.scm.wms.common.model.UnitOfMeasure;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemFootprint;
import se.gzhang.scm.wms.inventory.repository.ItemFootprintRepository;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.system.tools.service.FileUploadOptionService;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemFootprintService {
    @Autowired
    ItemFootprintRepository itemFootprintRepository;
    @Autowired
    private FileUploadOptionService fileUploadOptionService;
    @Autowired
    private ItemService itemService;

    public List<ItemFootprint> findAll(){

        return itemFootprintRepository.findAll();

    }

    public ItemFootprint findByItemFootprintId(int id){
        return itemFootprintRepository.findById(id);

    }

    public ItemFootprint findByItemFootprintName(String name) {
        return itemFootprintRepository.findByName(name);

    }

    public  List<ItemFootprint> findByItemId(int itemID) {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("itemID", String.valueOf(itemID));
        return findItemFootprints(criteriaList);
    }

    public  List<ItemFootprint> findByItemName(String itemName) {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("itemName", itemName);
        return findItemFootprints(criteriaList);
    }

    public List<ItemFootprint> findItemFootprints(Map<String, String> criteriaList) {
        return itemFootprintRepository.findAll(new Specification<ItemFootprint>() {
            @Override
            public Predicate toPredicate(Root<ItemFootprint> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if((criteriaList.containsKey("itemID") && !criteriaList.get("itemID").isEmpty()) ||
                        (criteriaList.containsKey("itemName") && !criteriaList.get("itemName").isEmpty()) ||
                        (criteriaList.containsKey("itemDescription") && !criteriaList.get("itemDescription").isEmpty()) ) {

                    Join<ItemFootprint, Item> joinItem = root.join("item",JoinType.INNER);

                    if (criteriaList.containsKey("itemID") && !criteriaList.get("itemID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinItem.get("id"), criteriaList.get("itemID")));
                    }
                    if (criteriaList.containsKey("itemName") && !criteriaList.get("itemName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinItem.get("name"), criteriaList.get("itemName")));
                    }
                    if (criteriaList.containsKey("itemDescription") && !criteriaList.get("itemDescription").isEmpty()) {
                        predicates.add(criteriaBuilder.like(joinItem.get("description"), criteriaList.get("itemDescription")));
                    }
                }

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }
                if(criteriaList.containsKey("description") && !criteriaList.get("description").isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("description"), criteriaList.get("description")));
                }
                if(criteriaList.containsKey("default") && !criteriaList.get("default").isEmpty()) {
                    // Default may be passed in as 'on'(from web client)
                    // or as 'true'(from other application). Both of those 2 values are treated as
                    // true
                    predicates.add(criteriaBuilder.equal(root.get("defaultFootprint"), (criteriaList.get("default").equals("on") || criteriaList.get("default").equals("true")) ));
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public ItemFootprint save(ItemFootprint itemFootprint) throws GenericException{
        return save(itemFootprint, false);
    }

    public ItemFootprint save(ItemFootprint itemFootprint, boolean ignoreDefaultFootprintValidation) throws GenericException{
        // If current footprint is default footprint, automatically revoke
        // the default footprint from other footprint of the same item, if any.
        // If current footprint is not default fooprint, make sure we have
        // at least one default footprint fo the same item
        if (itemFootprint.isDefaultFootprint()) {
            // make sure there's no other default footprint from same item
            Map<String, String> criteriaList = new HashMap<>();
            criteriaList.put("itemID", String.valueOf(itemFootprint.getItem().getId()));
            criteriaList.put("default", "true");
            List<ItemFootprint> defaultItemFootprintList = findItemFootprints(criteriaList);
            if (defaultItemFootprintList.size() > 0) {
                for(ItemFootprint defaultItemFootprint : defaultItemFootprintList) {
                    if (defaultItemFootprint.getId() != itemFootprint.getId()) {
                        defaultItemFootprint.setDefaultFootprint(false);
                        itemFootprintRepository.save(defaultItemFootprint);
                    }
                }
            }
        }
        else if (!ignoreDefaultFootprintValidation){
            // current footprint is not default footprint, let's make sure we at least have
            // one default footprint code
            Map<String, String> criteriaList = new HashMap<>();
            criteriaList.put("itemID", String.valueOf(itemFootprint.getItem().getId()));
            criteriaList.put("default", "true");
            List<ItemFootprint> defaultItemFootprintList = findItemFootprints(criteriaList);
            boolean defaultFootprintExists = false;
            if (defaultItemFootprintList.size() > 0) {
                // Since we have not saved current footprint, let's make sure we have default
                // footprint code other than current footprint
                for(ItemFootprint defaultItemFootprint : defaultItemFootprintList) {

                    if (defaultItemFootprint.getId() != itemFootprint.getId()) {
                        defaultFootprintExists = true;
                    }
                }
            }

            if (!defaultFootprintExists) {
                throw new GenericException(10000, "The footprint code must have a default footprint");
            }

        }


        ItemFootprint newItemFootprint = itemFootprintRepository.save(itemFootprint);
        itemFootprintRepository.flush();


        return newItemFootprint;
    }
    public void deleteByItemFootprintId(int id)  throws GenericException{
        // Check if there's default footprint code in the same item
        // after we remove this one
        ItemFootprint itemFootprint = itemFootprintRepository.findById(id);

        if (itemFootprint == null) {
            throw new GenericException(10000, "Not able to find the item footprint by id: " + id);
        }
        // If we are not empty all footprints from the item, let's make sure
        // we still have a default footprint after we remove this footprint
        if (findAll().size() > 1) {
            Map<String, String> criteriaList = new HashMap<>();
            criteriaList.put("itemID", String.valueOf(itemFootprint.getItem().getId()));
            criteriaList.put("default", "true");
            List<ItemFootprint> defaultItemFootprintList = findItemFootprints(criteriaList);
            boolean defaultFootprintExists = false;
            if (defaultItemFootprintList.size() > 0) {
                // Since we have not saved current footprint, let's make sure we have default
                // footprint code other than current footprint
                for(ItemFootprint defaultItemFootprint : defaultItemFootprintList) {
                    if (defaultItemFootprint.getId() != itemFootprint.getId()) {
                        defaultFootprintExists = true;
                    }
                }
            }

            if (!defaultFootprintExists) {
                throw new GenericException(10000, "The footprint code must have a default footprint");
            }
        }
        itemFootprintRepository.deleteById(id);

    }

    public List<ItemFootprint> loadFromFile(String[] columnNameList, List<String> itemFootprints, String processID) {


        List<ItemFootprint> itemFootprintList = new ArrayList<>();


        for(String itemFootprintString : itemFootprints) {

            String[] itemFootprintAttributeList = itemFootprintString.split(",");
            if (columnNameList.length != itemFootprintAttributeList.length) {
                continue;
            }
            try {
                ItemFootprint itemFootprint = setupItemFootprint(columnNameList, itemFootprintAttributeList);

                ItemFootprint newItemFootprint = save(itemFootprint, true);
                itemFootprintList.add(newItemFootprint);

                fileUploadOptionService.increaseRecordNumberLoaded(processID, true);
            }
            catch (GenericException ex) {

                fileUploadOptionService.increaseRecordNumberLoaded(processID, false);
            }
        }
        return itemFootprintList;
    }

    private ItemFootprint setupItemFootprint(String[] columnNameList, String[] itemFootprintAttributeList)
            throws GenericException {

        String itemFootprintName="", description="", itemName="", defaultFootprint="";

        for(int i = 0; i < columnNameList.length; i++) {

            String columnName = columnNameList[i];
            String itemFootprintAttribute = itemFootprintAttributeList[i];
            if (columnName.equalsIgnoreCase("name")){
                itemFootprintName = itemFootprintAttribute;
            }
            else if (columnName.equalsIgnoreCase("description")){
                description = itemFootprintAttribute;
            }
            else if (columnName.equalsIgnoreCase("itemName")){
                itemName = itemFootprintAttribute;
            }
            else if (columnName.equalsIgnoreCase("defaultFootprint")){
                defaultFootprint = itemFootprintAttribute;
            }
        }

        Item item = itemService.findByItemName(itemName);
        if (item == null) {
            throw new GenericException(10000,"Can't find item by name " + itemName);
        }

        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("itemName", itemName);
        criteriaList.put("name", itemFootprintName);

        List<ItemFootprint> itemFootprintList = findItemFootprints(criteriaList);
        ItemFootprint itemFootprint = null;
        if (itemFootprintList.size() == 1) {
            itemFootprint = itemFootprintList.get(0);
        }

        if (itemFootprint == null) {
            itemFootprint = new ItemFootprint();
            itemFootprint.setName(itemFootprintName);
        }
        itemFootprint.setDescription(description);
        itemFootprint.setItem(item);
        if ("1".equals(defaultFootprint) || "true".equalsIgnoreCase(defaultFootprint) || "T".equalsIgnoreCase("defaultFootprint")) {
            itemFootprint.setDefaultFootprint(true);
        }
        else {
            itemFootprint.setDefaultFootprint(false);
        }

        return itemFootprint;
    }
}
