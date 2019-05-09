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
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.exception.StandProductException;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemBarcode;
import se.gzhang.scm.wms.inventory.model.ItemBarcodeType;
import se.gzhang.scm.wms.inventory.model.ItemFootprint;
import se.gzhang.scm.wms.inventory.repository.ItemBarcodeRepository;
import se.gzhang.scm.wms.inventory.repository.ItemRepository;
import se.gzhang.scm.wms.system.tools.service.FileUploadOptionService;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemBarcodeService {
    @Autowired
    ItemBarcodeRepository itemBarcodeRepository;
    @Autowired
    private FileUploadOptionService fileUploadOptionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemBarcodeTypeService itemBarcodeTypeService;

    public List<ItemBarcode> findAll(){

        return itemBarcodeRepository.findAll();
    }

    public ItemBarcode findByItemBarcodeId(int id){

        return itemBarcodeRepository.findById(id);
    }

    public ItemBarcode findByItemBarcode(String barcode) {

        return itemBarcodeRepository.findByBarcode(barcode);
    }

    public void deleteByItemBarcodeId(int id) {
        itemBarcodeRepository.deleteById(id);
    }

    public List<ItemBarcode> findItemBarcodes(Map<String, String> criteriaList) {
        return itemBarcodeRepository.findAll(new Specification<ItemBarcode>() {
            @Override
            public Predicate toPredicate(Root<ItemBarcode> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if((criteriaList.containsKey("itemID") && !criteriaList.get("itemID").isEmpty()) ||
                        (criteriaList.containsKey("itemName") && !criteriaList.get("itemName").isEmpty()) ||
                        (criteriaList.containsKey("itemDescription") && !criteriaList.get("itemDescription").isEmpty()) ) {

                    Join<ItemBarcode, Item> joinItem = root.join("item",JoinType.INNER);

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
                if(criteriaList.containsKey("barcode") && !criteriaList.get("barcode").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("barcode"), criteriaList.get("barcode")));
                }
                if(criteriaList.containsKey("description") && !criteriaList.get("description").isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("description"), criteriaList.get("description")));
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public ItemBarcode save(ItemBarcode itemBarcode) {

        ItemBarcode newItemBarcode = itemBarcodeRepository.save(itemBarcode);
        itemBarcodeRepository.flush();
        return newItemBarcode;
    }

    public List<ItemBarcode> loadFromFile(String[] columnNameList, List<String> itemBarcodes, String processID) {


        List<ItemBarcode> itemBarcodeList = new ArrayList<>();


        for(String itemBarcodeString : itemBarcodes) {

            String[] itemBarcodeAttributeList = itemBarcodeString.split(",");
            if (columnNameList.length != itemBarcodeAttributeList.length) {
                continue;
            }
            try {
                ItemBarcode itemBarcode = setupItemBarcode(columnNameList, itemBarcodeAttributeList);

                itemBarcodeList.add(save(itemBarcode));

                fileUploadOptionService.increaseRecordNumberLoaded(processID, true);
            }
            catch (GenericException ex) {

                fileUploadOptionService.increaseRecordNumberLoaded(processID, false);
            }
        }
        return itemBarcodeList;
    }

    private ItemBarcode setupItemBarcode(String[] columnNameList, String[] itemBarcodeAttributeList)
            throws GenericException {

        String barcodeString="", barcodeType="", itemName="";

        for(int i = 0; i < columnNameList.length; i++) {

            String columnName = columnNameList[i];
            String itemBarcodeAttribute = itemBarcodeAttributeList[i];
            if (columnName.equalsIgnoreCase("barcode")){
                barcodeString = itemBarcodeAttribute;
            }
            else if (columnName.equalsIgnoreCase("type")){
                barcodeType = itemBarcodeAttribute;
            }
            else if (columnName.equalsIgnoreCase("itemName")){
                itemName = itemBarcodeAttribute;
            }
        }

        Item item = itemService.findByItemName(itemName);
        if (item == null) {
            throw new StandProductException("ItemException.CannotFindItem","Can't find item by name " + itemName);
        }
        ItemBarcodeType itemBarcodeType = itemBarcodeTypeService.findByItemBarcodeTypeName(barcodeType);
        if (itemBarcodeType == null) {
            throw new StandProductException("ItemBarcodeTypeException.CannotFindItemBarcodeType","Can't find item barcode by type " + barcodeType);
        }

        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("itemName", itemName);
        criteriaList.put("barcode", barcodeString);
        List<ItemBarcode> itemBarcodeList = findItemBarcodes(criteriaList);
        ItemBarcode itemBarcode = null;
        if (itemBarcodeList.size() == 1) {
            itemBarcode = itemBarcodeList.get(0);
        }

        if (itemBarcode == null) {
            itemBarcode = new ItemBarcode();
        }
        itemBarcode.setBarcode(barcodeString);
        itemBarcode.setItemBarcodeType(itemBarcodeType);
        itemBarcode.setItem(item);

        return itemBarcode;
    }
}
