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

package se.gzhang.scm.wms.outbound.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.common.model.Customer;
import se.gzhang.scm.wms.common.service.CustomerService;
import se.gzhang.scm.wms.inventory.model.ItemFootprintUOM;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.model.VolumeType;
import se.gzhang.scm.wms.layout.service.AreaService;
import se.gzhang.scm.wms.outbound.order.model.SalesOrder;
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;
import se.gzhang.scm.wms.outbound.order.model.ShippingStageLocationReserveStrategy;
import se.gzhang.scm.wms.outbound.order.repository.SalesOrderLineRepository;
import se.gzhang.scm.wms.outbound.order.repository.SalesOrderRepository;
import se.gzhang.scm.wms.outbound.shipment.model.Shipment;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentLineService;
import se.gzhang.scm.wms.outbound.shipment.service.ShipmentService;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalesOrderLineService {

    @Autowired
    SalesOrderLineRepository salesOrderLineRepository;

    public List<SalesOrderLine> findAll(){

        return salesOrderLineRepository.findAll();
    }

    public SalesOrderLine findBySalesOrderLineId(int id){
        return salesOrderLineRepository.findById(id);
    }


    public SalesOrderLine findBySalesOrderExternalID(String externalID) {
        return salesOrderLineRepository.findByExternalID(externalID);
    }


    public SalesOrderLine save(SalesOrderLine salesOrderLine) {
        // If the external ID is empty, we will use order number
        // as the external ID
        if (salesOrderLine.getExternalID().isEmpty()) {
            salesOrderLine.setExternalID(salesOrderLine.getSalesOrder().getNumber() + "-" + salesOrderLine.getLineNumber());
        }
        SalesOrderLine newSalesOrderLine = salesOrderLineRepository.save(salesOrderLine);
        salesOrderLineRepository.flush();
        return newSalesOrderLine;
    }

}
