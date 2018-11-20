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
import se.gzhang.scm.wms.inventory.model.ItemBarcodeType;
import se.gzhang.scm.wms.inventory.repository.ItemBarcodeRepository;
import se.gzhang.scm.wms.inventory.repository.ItemBarcodeTypeRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemBarcodeTypeService {
    @Autowired
    ItemBarcodeTypeRepository itemBarcodeTypeRepository;

    public List<ItemBarcodeType> findAll(){

        return itemBarcodeTypeRepository.findAll();
    }

    public ItemBarcodeType findByItemBarcodeTypeId(int id){

        return itemBarcodeTypeRepository.findById(id);
    }

    public ItemBarcodeType findByItemBarcodeTypeName(String name) {

        return itemBarcodeTypeRepository.findByName(name);
    }

    public void deleteByItemBarcodeTypeId(int id) {
        itemBarcodeTypeRepository.deleteById(id);
    }


    public ItemBarcodeType save(ItemBarcodeType itemBarcodeType) {

        ItemBarcodeType newItemBarcodeType = itemBarcodeTypeRepository.save(itemBarcodeType);
        itemBarcodeTypeRepository.flush();
        return newItemBarcodeType;
    }
}
