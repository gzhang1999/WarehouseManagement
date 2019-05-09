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
import se.gzhang.scm.wms.common.model.Velocity;
import se.gzhang.scm.wms.common.repository.ClientRepository;
import se.gzhang.scm.wms.common.service.ClientService;
import se.gzhang.scm.wms.common.service.UnitOfMeasureService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.exception.StandProductException;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.repository.ItemRepository;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.repository.WarehouseRepository;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.system.tools.service.FileUploadOptionService;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    WarehouseService warehouseService;
    @Autowired
    ClientService clientService;
    @Autowired
    UnitOfMeasureService unitOfMeasureService;

    @Autowired
    private FileUploadOptionService fileUploadOptionService;

    public List<Item> findAll(){

        return itemRepository.findAll();
    }

    public Item findByItemId(int id){

        return itemRepository.findById(id);
    }

    public Item findByItemName(String name) {

        return itemRepository.findByName(name);
    }

    public void deleteByItemId(int id) {
        itemRepository.deleteById(id);
    }

    public List<Item> findItems(Map<String, String> criteriaList) {
        System.out.println("Find item with following criteria");
        for(Map.Entry<String, String> entry : criteriaList.entrySet()) {
            System.out.println("name: " + entry.getKey() + " , value: " + entry.getValue());
        }
        return itemRepository.findAll(new Specification<Item>() {
            @Override
            public Predicate toPredicate(Root<Item> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }
                if(criteriaList.containsKey("description") && !criteriaList.get("description").isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("description"), criteriaList.get("description")));
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public Item save(Item item) {

        Item newItem = itemRepository.save(item);
        itemRepository.flush();
        return newItem;
    }


    public List<Item> loadFromFile(String[] columnNameList, List<String> items, String processID) {


        List<Item> itemList = new ArrayList<>();


        for(String itemString : items) {

            String[] itemAttributeList = itemString.split(",");
            if (columnNameList.length != itemAttributeList.length) {
                continue;
            }
            try {
                Item item = setupItem(columnNameList, itemAttributeList);

                itemList.add(save(item));

                fileUploadOptionService.increaseRecordNumberLoaded(processID, true);

            }
            catch (GenericException ex) {

                fileUploadOptionService.increaseRecordNumberLoaded(processID, false);
            }
        }
        return itemList;
    }

    private Item setupItem(String[] columnNameList, String[] itemAttributeList)
            throws GenericException {

        String itemName="", description="", warehouseName="", clientName="", lpnUOMName="";

        for(int i = 0; i < columnNameList.length; i++) {

            String columnName = columnNameList[i];
            String itemAttribute = itemAttributeList[i];
            if (columnName.equalsIgnoreCase("name")){
                itemName = itemAttribute;
            }
            else if (columnName.equalsIgnoreCase("description")){
                description = itemAttribute;
            }
            else if (columnName.equalsIgnoreCase("warehouse")){
                warehouseName = itemAttribute;
            }
            else if (columnName.equalsIgnoreCase("client")){
                clientName = itemAttribute;
            }
            else if (columnName.equalsIgnoreCase("lpn_uom")){
                lpnUOMName = itemAttribute;
            }
        }

        Warehouse warehouse = warehouseService.findByWarehouseName(warehouseName);
        if (warehouse == null) {
            throw new StandProductException("WarehouseExeption.CannotFindWarehouse","Can't find warehouse by name " + warehouseName);
        }

        Client client = clientService.findByClientName(clientName);
        if (client == null) {
            throw new GenericException("ClientExeption.CannotFindClient","Can't find client by name " + clientName);
        }

        UnitOfMeasure unitOfMeasure = unitOfMeasureService.findByUOMName(lpnUOMName);
        if (unitOfMeasure == null) {
            throw new GenericException("UnitOfMessageException.CannotFindUnitOfMessage","Can't find unit of measure by name " + lpnUOMName);
        }


        Item item = findByItemName(itemName);
        if (item == null) {
            item = new Item();
            item.setName(itemName);
        }
        item.setDescription(description);
        item.setWarehouse(warehouse);
        item.setClient(client);
        item.setLpnUnitOfMeasure(unitOfMeasure);
        return item;
    }
}
