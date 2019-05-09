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

package se.gzhang.scm.wms.outbound.order.model;

import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.outbound.order.service.SalesOrderService;

public class ReserveShippingStageLocationByCustomerStrategy extends DefaultShippingStageLocationReserveStrategy {

    public ReserveShippingStageLocationByCustomerStrategy(LocationService locationService, SalesOrderService salesOrderService) {
        super(locationService, salesOrderService);
    }

    public void reserve(Location location, SalesOrder salesOrder){
        if (isReservable(location, salesOrder)) {
            locationService.reserveLocation(location, salesOrder.getShipToCustomer().getName(),
                    salesOrderService.getSalesOrderVolume(salesOrder, location.getArea().getVolumeType()));
        }
    }

    public boolean isReservable(Location location, SalesOrder salesOrder) {
        // if the location
        // 1. has enough room
        // 2. either not reserved yet, or reserved by the same sales order
        // then it is reservable by the same sales order
        if ((location.getReserveCode().isEmpty() || location.getReserveCode().equals(salesOrder.getShipToCustomer().getName()))
                && (location.getVolume() - location.getUsedVolume() > salesOrderService.getSalesOrderVolume(salesOrder, location.getArea().getVolumeType()))) {
            return true;
        }
        return false;
    }

}
