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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.framework.controls.service.UniversalIdentifierService;
import se.gzhang.scm.wms.outbound.shipment.model.*;
import se.gzhang.scm.wms.outbound.shipment.repository.PickRepository;
import se.gzhang.scm.wms.outbound.shipment.repository.ShortAllocationRepository;

import java.util.Date;
import java.util.List;

@Service
public class ShortAllocationService {
    @Autowired
    ShortAllocationRepository shortAllocationRepository;
    @Autowired
    UniversalIdentifierService universalIdentifierService;
    @Autowired
    ShipmentLineService shipmentLineService;

    public List<ShortAllocation> findAll(){

        return shortAllocationRepository.findAll();
    }

    public ShortAllocation findByShortAllocationId(int id){
        return shortAllocationRepository.findById(id);
    }

    public ShortAllocation findByShortAllocationNumber(String number) {
        return shortAllocationRepository.findByNumber(number);
    }

    @Transactional
    public ShortAllocation save(ShortAllocation shortAllocation) {
        ShortAllocation newShortAllocation = shortAllocationRepository.save(shortAllocation);
        shortAllocationRepository.flush();
        return newShortAllocation;
    }

    @Transactional
    public ShortAllocation generateShortAllocationFromShipmentLineAllocation(ShipmentLine shipmentLine, int shortQuantity) {
        ShortAllocation shortAllocation = new ShortAllocation();
        shortAllocation.setQuantity(shortQuantity);
        shortAllocation.setShipmentLine(shipmentLine);
        shortAllocation.setNumber(universalIdentifierService.getNextNumber("short_allocation_number"));
        shortAllocation.setShortAllocationState(ShortAllocationState.NEW);

        return save(shortAllocation);
    }

    @Transactional
    public void cancelShortAllocation(ShortAllocation shortAllocation) {
        if (shortAllocation.getShortAllocationState().equals(ShortAllocationState.CANCELLED)) {
            return;
        }
        // Mark the pick as cancelled
        shortAllocation.setShortAllocationState(ShortAllocationState.CANCELLED);
        shortAllocation.setCancelledDate(new Date());
        save(shortAllocation);

        // Return the quantity back to shipment
        ShipmentLine shipmentLine = shortAllocation.getShipmentLine();
        shipmentLine.setInprocessQuantity(Math.max(0, shipmentLine.getInprocessQuantity() - shortAllocation.getQuantity()));
        shipmentLineService.save(shipmentLine);

    }
}
