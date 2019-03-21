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
import se.gzhang.scm.wms.common.model.AddTrailerFlowModel;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.inbound.model.AddReceiptFlowModel;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.model.ItemSerializer;
import se.gzhang.scm.wms.inventory.service.InventoryStatusService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.service.WarehouseService;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class AddReceiptFlowHandler {

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    SupplierService supplierService;

    @Autowired
    ItemService itemService;

    @Autowired
    InventoryStatusService inventoryStatusService;

    @Autowired
    ReceiptService receiptService;

    public AddReceiptFlowModel init() {
        return new AddReceiptFlowModel();
    }

    public void saveReceipt(AddReceiptFlowModel addReceiptFlowModel, LocalParameterMap parameters, HttpSession session){
        Receipt receipt = getReceiptFromHttpRequestParameters(parameters);
        System.out.println("Create trailer for warheouse: " + session.getAttribute("warehouse_id") +
                " / " + session.getAttribute("warehouse_name"));
        receipt.setWarehouse(warehouseService.findByWarehouseId(Integer.parseInt(session.getAttribute("warehouse_id").toString())));
        addReceiptFlowModel.setupReceipt(receipt);
    }
    public void serializeReceipt(AddReceiptFlowModel addReceiptFlowModel){
        Receipt currentReceipt = addReceiptFlowModel.getCurrentReceipt();
        if (currentReceipt.getNumber() != null
            && currentReceipt.getNumber().length() > 0 ){
            receiptService.save(currentReceipt);
        }
    }

    public void saveReceiptLine(AddReceiptFlowModel addReceiptFlowModel, LocalParameterMap parameters) {
        if (parameters.contains("lineNumber") && parameters.get("lineNumber") != null && !parameters.get("lineNumber").isEmpty()) {
            ReceiptLine receiptLine = getReceiptLineFromHttpRequestParameters(parameters);
            addReceiptFlowModel.setupReceiptLine(receiptLine);
        }

    }

    public void removeReceiptLine(AddReceiptFlowModel addReceiptFlowModel, LocalParameterMap parameters){
        ReceiptLine receiptLine = getReceiptLineFromHttpRequestParameters(parameters);
        addReceiptFlowModel.removeReceiptLine(receiptLine);

    }

    private Receipt getReceiptFromHttpRequestParameters(LocalParameterMap parameters) {
        Receipt receipt = new Receipt();

        Map<String, Object> parameterMap = parameters.asMap();

        for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
            try {
                String fieldName = parameter.getKey();
                Object fieldValue = parameter.getValue();

                Field field = receipt.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                // Special handling for
                // 1. trailerType: convert String to Enum
                // 2. carrierID: get Carrier from id
                if ("supplier".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        Supplier supplier = supplierService.findBySupplierName(fieldValue.toString());
                        if (supplier != null) {
                            field.set(receipt, supplier);
                        }
                    }
                }
                else if (field.getType() == Integer.class) {
                    field.set(receipt, Integer.parseInt(fieldValue.toString()));
                }
                else {
                    field.set(receipt, fieldValue);

                }
            } catch (NoSuchFieldException ex) {
                // ignore
                System.out.println("NoSuchFieldException: " + ex.getMessage() + "\n" );
            } catch (Exception e) {
                // Ignore any error
                System.out.println("Error while saving trailer: " + e.getMessage() + "\n" );
                e.printStackTrace();
            }
        }
        return receipt;
    }

    private ReceiptLine getReceiptLineFromHttpRequestParameters(LocalParameterMap parameters) {
        ReceiptLine receiptLine = new ReceiptLine();

        Map<String, Object> parameterMap = parameters.asMap();

        for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
            try {
                String fieldName = parameter.getKey();
                Object fieldValue = parameter.getValue();

                Field field = receiptLine.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                // Special handling for
                // 1. Item
                // 2. Inventory Status
                if ("item".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        Item item = itemService.findByItemName(fieldValue.toString());
                        if (item != null) {
                            field.set(receiptLine, item);
                        }
                    }
                }
                else if ("inventoryStatus".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        InventoryStatus inventoryStatus = inventoryStatusService.findByInventoryStatusName(fieldValue.toString());
                        if (inventoryStatus != null) {
                            field.set(receiptLine, inventoryStatus);
                        }
                    }
                }
                else if (field.getType() == Integer.class) {
                    field.set(receiptLine, Integer.parseInt(fieldValue.toString()));
                }
                else {
                    field.set(receiptLine, fieldValue);

                }
            } catch (NoSuchFieldException ex) {
                // ignore
                System.out.println("NoSuchFieldException: " + ex.getMessage() + "\n" );
            } catch (Exception e) {
                // Ignore any error
                System.out.println("Error while saving trailer: " + e.getMessage() + "\n" );
                e.printStackTrace();
            }
        }
        return receiptLine;
    }
}
