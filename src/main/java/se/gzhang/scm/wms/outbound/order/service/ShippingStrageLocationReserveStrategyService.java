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
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.inventory.service.InventoryService;
import se.gzhang.scm.wms.layout.model.LocationReserveStrategyType;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.outbound.order.model.*;
import se.gzhang.scm.wms.outbound.shipment.service.AllocationResultService;
import se.gzhang.scm.wms.outbound.shipment.service.PickService;
import se.gzhang.scm.wms.outbound.shipment.service.ShortAllocationService;

@Service
public class ShippingStrageLocationReserveStrategyService {
    @Autowired
    LocationService locationService;
    @Autowired
    SalesOrderService salesOrderService;

    public ShippingStageLocationReserveStrategy getShippingStrageLocationReserveStrategy(LocationReserveStrategyType locationReserveStrategyType) {
        switch (locationReserveStrategyType){
            case BY_ORDER:
                return new ReserveShippingStageLocationByOrderStrategy(locationService, salesOrderService);
            case BY_CUSTOMER:
                return new ReserveShippingStageLocationByCustomerStrategy(locationService, salesOrderService);
            case BY_ANYTHING:
                return new ReserveShippingStageLocationByAnythingStrategy(locationService, salesOrderService);

            default:
                return new ReserveShippingStageLocationByAnythingStrategy(locationService, salesOrderService);
        }
    }




}
