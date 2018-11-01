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

package se.gzhang.scm.wms.layout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.repository.BuildingRepository;

import java.util.List;

@Service("buildingService")
public class BuildingService {
    @Autowired
    private BuildingRepository buildingRepository;

    public Building findByBuildingId(int id) {
        return buildingRepository.findById(id);
    }

    public List<Building> findAll() {

        return buildingRepository.findAll();
    }

    public List<Building> findByWarehouseId(int warehouseID) {

        return buildingRepository.findByWarehouseId(warehouseID);
    }

}
