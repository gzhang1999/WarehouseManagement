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
import se.gzhang.scm.wms.common.service.ClientService;
import se.gzhang.scm.wms.common.service.UnitOfMeasureService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemFamily;
import se.gzhang.scm.wms.inventory.repository.ItemFamilyRepository;
import se.gzhang.scm.wms.inventory.repository.ItemRepository;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.system.tools.service.FileUploadOptionService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemFamilyService {
    @Autowired
    ItemFamilyRepository itemFamilyRepository;

    public List<ItemFamily> findAll(){

        return itemFamilyRepository.findAll();
    }

    public ItemFamily findByItemFamilyId(int id){

        return itemFamilyRepository.findById(id);
    }

    public ItemFamily findByItemFamilyName(String name) {

        return itemFamilyRepository.findByName(name);
    }

}
