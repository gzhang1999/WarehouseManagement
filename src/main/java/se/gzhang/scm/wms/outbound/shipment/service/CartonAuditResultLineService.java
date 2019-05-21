/**
 * Copyright 2019
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

package se.gzhang.scm.wms.outbound.shipment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.exception.Outbound.CartonAuditResultException;
import se.gzhang.scm.wms.exception.inventory.ItemException;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.outbound.shipment.model.*;
import se.gzhang.scm.wms.outbound.shipment.repository.CartonAuditResultLineRepository;
import se.gzhang.scm.wms.outbound.shipment.repository.CartonAuditResultRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartonAuditResultLineService {

    @Autowired
    private CartonAuditResultLineRepository cartonAuditResultLineRepository;

    @Autowired
    private ItemService itemService;


    public List<CartonAuditResultLine> findAll(){

        return cartonAuditResultLineRepository.findAll();
    }

    public CartonAuditResultLine findByCartonAuditResultLineId(int id){
        return cartonAuditResultLineRepository.findById(id);
    }

    @Transactional
    public CartonAuditResultLine save(CartonAuditResultLine cartonAuditResultLine) {
        return cartonAuditResultLineRepository.save(cartonAuditResultLine);
    }


    public CartonAuditResultLine findCartonAuditResultLine(CartonAuditResult cartonAuditResult, Item item) {
        for(CartonAuditResultLine cartonAuditResultLine : cartonAuditResult.getCartonAuditResultLines()) {
            if (cartonAuditResultLine.getItem().equals(item)) {
                return cartonAuditResultLine;
            }
        }
        throw CartonAuditResultException.NO_SUCH_CARTON_AUDIT_RESULT_LINE_ITEM;
    }

    public CartonAuditResultLine findCartonAuditResultLine(CartonAuditResult cartonAuditResult, String itemName) {
        Item item = itemService.findByItemName(itemName);
        if (item == null) {
            throw ItemException.NO_SUCH_ITEM;
        }

        return findCartonAuditResultLine(cartonAuditResult, item);
    }

}
