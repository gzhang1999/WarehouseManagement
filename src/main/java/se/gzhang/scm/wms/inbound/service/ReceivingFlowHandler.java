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

package se.gzhang.scm.wms.inbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.LocalParameterMap;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.inbound.model.AddReceiptFlowModel;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inbound.model.ReceivingFlowModel;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.service.InventoryStatusService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.service.WarehouseService;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class ReceivingFlowHandler {

    @Autowired
    ReceiptService receiptService;

    public ReceivingFlowModel init(int receiptID) {

        Receipt receipt = receiptService.findByReceiptId(receiptID);
        System.out.println("receipt == null? " + (receipt == null));
        System.out.println("receipt.getTrailer() == null? " + (receipt.getTrailer() == null));
        System.out.println("receipt.getTrailer().getTrailerNumber() == null? " + (receipt.getTrailer().getTrailerNumber() == null));
        return new ReceivingFlowModel(receipt);
    }

}
