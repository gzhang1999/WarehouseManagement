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

package se.gzhang.scm.wms.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.common.model.Client;
import se.gzhang.scm.wms.common.model.UnitOfMeasure;
import se.gzhang.scm.wms.common.repository.ClientRepository;
import se.gzhang.scm.wms.common.repository.UnitOfMeasureRepository;

import java.util.List;

@Service
public class UnitOfMeasureService {

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    public List<UnitOfMeasure> findAll(){

        return unitOfMeasureRepository.findAll();
    }

    public UnitOfMeasure findByUOMId(int id){
        return unitOfMeasureRepository.findById(id);
    }

    public UnitOfMeasure findByUOMName(String name) {
        return unitOfMeasureRepository.findByName(name);
    }

    public UnitOfMeasure save(UnitOfMeasure unitOfMeasure) {
        UnitOfMeasure newUnitOfMeasure= unitOfMeasureRepository.save(unitOfMeasure);
        unitOfMeasureRepository.flush();
        return newUnitOfMeasure;
    }
}
