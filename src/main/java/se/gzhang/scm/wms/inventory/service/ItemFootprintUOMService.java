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

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inventory.model.ItemFootprint;
import se.gzhang.scm.wms.inventory.model.ItemFootprintUOM;
import se.gzhang.scm.wms.inventory.repository.ItemFootprintRepository;
import se.gzhang.scm.wms.inventory.repository.ItemFootprintUOMRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ItemFootprintUOMService {
    @Autowired
    ItemFootprintUOMRepository itemFootprintUOMRepository;

    @Autowired
    ItemFootprintService itemFootprintService;

    public List<ItemFootprintUOM> findAll(){

        return itemFootprintUOMRepository.findAll();
    }

    public ItemFootprintUOM findByItemFootprintUOMId(int id){
        return itemFootprintUOMRepository.findById(id);
    }
    public List<ItemFootprintUOM> findByItemFootprintId(int footprintID){
        return itemFootprintService.findByItemFootprintId(footprintID).getItemFootprintUOMs();
    }
    public ItemFootprintUOM save(ItemFootprintUOM itemFootprintUOM) throws GenericException {

        List<ItemFootprintUOM> existItemFootprintUOMList = findByItemFootprintId(itemFootprintUOM.getItemFootprint().getId());

        // Make sure we don't have duplicated UOM in the same item footprint
        for(ItemFootprintUOM existItemFootprintUOM : existItemFootprintUOMList) {
            if (existItemFootprintUOM.getUnitOfMeasure().getName().equals(itemFootprintUOM.getUnitOfMeasure().getName()) &&
                    (itemFootprintUOM.getId() == null || existItemFootprintUOM.getId() !=  itemFootprintUOM.getId())) {
                throw new GenericException(10000,"The same UOM " + itemFootprintUOM.getUnitOfMeasure().getName() + " already exists in the footprint: " + itemFootprintUOM.getItemFootprint().getName());
            }
        }
        // If the item footprint UOM to be change is a case UOM, mark this UOM as case
        // and release other UOMs from the item footprint as one footprint can only have
        // one case UOM. We will always use the case UOM to calculate the size of inventory
        System.out.println(">> itemFootprintUOM.isCaseUOM()?" + itemFootprintUOM.isCaseUOM());
        if (itemFootprintUOM.isCaseUOM()) {
            // Check if there's other UOM in the same item footprint that is also case uom
            for(ItemFootprintUOM existItemFootprintUOM : existItemFootprintUOMList) {
                System.out.println(">> existItemFootprintUOM: + " + existItemFootprintUOM.getUnitOfMeasure().getName() + ", existItemFootprintUOM.isCaseUOM()?" + existItemFootprintUOM.isCaseUOM());
                if (existItemFootprintUOM.isCaseUOM() &&
                      (itemFootprintUOM.getId() == null || existItemFootprintUOM.getId() !=  itemFootprintUOM.getId())) {
                    // we found another footprint UOM from the same item footprint that also marked as
                    // case uom, let's change the flag to NON case UOM
                    System.out.println(">>  existItemFootprintUOM.setCaseUOM(false)");
                    existItemFootprintUOM.setCaseUOM(false);
                    itemFootprintUOMRepository.save(existItemFootprintUOM);
                }
            }
        }
        // The same as case UOM, make sure we only have one pallet UOM. This is the UOM we are using to calculate the
        // size and number of pallets
        if (itemFootprintUOM.isPalletUOM()) {
            // Check if there's other UOM in the same item footprint that is also case uom
            for(ItemFootprintUOM existItemFootprintUOM : existItemFootprintUOMList) {
                if (existItemFootprintUOM.isPalletUOM() &&
                        (itemFootprintUOM.getId() == null || existItemFootprintUOM.getId() !=  itemFootprintUOM.getId())) {
                    // we found another footprint UOM from the same item footprint that also marked as
                    // case uom, let's change the flag to NON case UOM
                    existItemFootprintUOM.setPalletUOM(false);
                    itemFootprintUOMRepository.save(existItemFootprintUOM);
                }
            }
        }

        ItemFootprintUOM newItemFootprintUOM = itemFootprintUOMRepository.save(itemFootprintUOM);
        itemFootprintUOMRepository.flush();

        resetStockUOM(itemFootprintUOM.getItemFootprint().getId());

        return newItemFootprintUOM;
    }
    public void deleteByItemFootprintId(int id) {
        int itemFootprintID = itemFootprintUOMRepository.findById(id).getItemFootprint().getId();

        itemFootprintUOMRepository.deleteById(id);
        itemFootprintUOMRepository.flush();
        resetStockUOM(itemFootprintID);
    }

    private void resetStockUOM(int itemFootprintID) {
        List<ItemFootprintUOM> existItemFootprintUOMList = findByItemFootprintId(itemFootprintID);

        if (existItemFootprintUOMList.size() > 0) {
            // Sort the existing UOM from smallest quantity to the biggest quantity and mark
            // the smallest UOM as stock UOM
            Collections.sort(existItemFootprintUOMList, new Comparator<ItemFootprintUOM>() {
                @Override
                public int compare(ItemFootprintUOM itemFootprintUOM1, ItemFootprintUOM itemFootprintUOM2) {
                    return itemFootprintUOM1.getQuantity() - itemFootprintUOM2.getQuantity();
                }
            });
            if (!existItemFootprintUOMList.get(0).isStockUOM()) {
                existItemFootprintUOMList.get(0).setStockUOM(true);
                itemFootprintUOMRepository.save(existItemFootprintUOMList.get(0));

            }
            for (int i = 1; i < existItemFootprintUOMList.size() && existItemFootprintUOMList.get(i).isStockUOM(); i++) {

                existItemFootprintUOMList.get(i).setStockUOM(false);
                itemFootprintUOMRepository.save(existItemFootprintUOMList.get(i));
            }
            itemFootprintUOMRepository.flush();
        }
    }
}
