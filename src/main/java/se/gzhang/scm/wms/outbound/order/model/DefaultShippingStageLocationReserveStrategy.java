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

public abstract class DefaultShippingStageLocationReserveStrategy implements ShippingStageLocationReserveStrategy {

    protected LocationService locationService;
    protected SalesOrderService salesOrderService;
    public DefaultShippingStageLocationReserveStrategy(LocationService locationService, SalesOrderService salesOrderService) {
        this.locationService = locationService;
        this.salesOrderService = salesOrderService;
    }

    public abstract void reserve(Location location, SalesOrder salesOrder);

    public abstract  boolean isReservable(Location location, SalesOrder salesOrder);

}
