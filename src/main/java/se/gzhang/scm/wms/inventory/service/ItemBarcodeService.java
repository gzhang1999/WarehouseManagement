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
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemBarcode;
import se.gzhang.scm.wms.inventory.model.ItemFootprint;
import se.gzhang.scm.wms.inventory.repository.ItemBarcodeRepository;
import se.gzhang.scm.wms.inventory.repository.ItemRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemBarcodeService {
    @Autowired
    ItemBarcodeRepository itemBarcodeRepository;

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
        System.out.println("Find item with following criteria");
        for(Map.Entry<String, String> entry : criteriaList.entrySet()) {
            System.out.println("name: " + entry.getKey() + " , value: " + entry.getValue());
        }
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

    public ItemBarcode save(ItemBarcode itemBarcode) {

        ItemBarcode newItemBarcode = itemBarcodeRepository.save(itemBarcode);
        itemBarcodeRepository.flush();
        return newItemBarcode;
    }
}
