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
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.model.CarrierServiceLevel;
import se.gzhang.scm.wms.common.repository.CarrierRepository;
import se.gzhang.scm.wms.common.repository.CarrierServiceLevelRepository;

import java.util.List;

@Service
public class CarrierServiceLevelService {

    @Autowired
    CarrierServiceLevelRepository carrierServiceLevelRepository;

    public List<CarrierServiceLevel> findAll(){

        return carrierServiceLevelRepository.findAll();
    }

    public CarrierServiceLevel findByCarrierServiceLevelId(int id){
        return carrierServiceLevelRepository.findById(id);

    }

    public CarrierServiceLevel findByCarrierServiceLevelName(String name) {
        return carrierServiceLevelRepository.findByName(name);

    }

    @Transactional
    public CarrierServiceLevel save(CarrierServiceLevel carrierServiceLevel) {
        return carrierServiceLevelRepository.save(carrierServiceLevel);
    }

    @Transactional
    public void deleteByCarrierServiceLevelID(int carrierServiceLevelID) {
        carrierServiceLevelRepository.deleteById(carrierServiceLevelID);

    }
}
