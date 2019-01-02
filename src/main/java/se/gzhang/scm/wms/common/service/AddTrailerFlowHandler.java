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
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.LocalParameterMap;
import se.gzhang.scm.wms.common.model.*;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.service.InventoryStatusService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.service.WarehouseService;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class AddTrailerFlowHandler {

    @Autowired
    CarrierService carrierService;
    @Autowired
    SupplierService supplierService;
    @Autowired
    ItemService itemService;
    @Autowired
    InventoryStatusService inventoryStatusService;
    @Autowired
    TrailerService trailerService;
    @Autowired
    WarehouseService warehouseService;

    public AddTrailerFlowModel init() {
        return new AddTrailerFlowModel();
    }

    public void saveTrailer(AddTrailerFlowModel addTrailerFlowModel, LocalParameterMap parameters, HttpSession session){
        Trailer trailer = getTrailerFromHttpRequestParameters(parameters);
        System.out.println("Create trailer for warheouse: " + session.getAttribute("warehouse_id") +
                " / " + session.getAttribute("warehouse_name"));
        trailer.setWarehouse(warehouseService.findByWarehouseId(Integer.parseInt(session.getAttribute("warehouse_id").toString())));
        addTrailerFlowModel.setupTrailer(trailer);
    }


    public boolean isReceivingTrailer(AddTrailerFlowModel addTrailerFlowModel) {
        return addTrailerFlowModel.getTrailer().getTrailerType() != null && addTrailerFlowModel.getTrailer().getTrailerType().equals(TrailerType.RECEIVING_TRAILER);
    }
    public boolean isShippingTrailer(AddTrailerFlowModel addTrailerFlowModel) {
        return addTrailerFlowModel.getTrailer().getTrailerType() != null && addTrailerFlowModel.getTrailer().getTrailerType().equals(TrailerType.SHIPPING_TRAILER);
    }
    public boolean isStorageTrailer(AddTrailerFlowModel addTrailerFlowModel) {
        return addTrailerFlowModel.getTrailer().getTrailerType() != null && addTrailerFlowModel.getTrailer().getTrailerType().equals(TrailerType.STORAGE_TRAILER);
    }
    public TrailerType getTrailerType(AddTrailerFlowModel addTrailerFlowModel) {
        return addTrailerFlowModel.getTrailer().getTrailerType();
    }

    // Add a empty receipt to current trailer model and all the future changes(change attribute / add receipt line)
    // will be based on this receipt
    public void addEmptyReceipt(AddTrailerFlowModel addTrailerFlowModel) {

        addTrailerFlowModel.addEmptyReceipt();

    }


    public void saveReceipt(AddTrailerFlowModel addTrailerFlowModel, LocalParameterMap parameters){

        if (parameters.contains("number") && parameters.get("number") != null && !parameters.get("number").isEmpty()) {
            Receipt receipt = getReceiptFromHttpRequestParameters(parameters);

            addTrailerFlowModel.setupReceipt(receipt);
        }

    }


    public void removeReceipt(AddTrailerFlowModel addTrailerFlowModel, LocalParameterMap parameters){

        Receipt receipt = getReceiptFromHttpRequestParameters(parameters);
        addTrailerFlowModel.removeReceipt(receipt);

    }

    public void saveReceiptLine(AddTrailerFlowModel addTrailerFlowModel, LocalParameterMap parameters) {
        if (parameters.contains("lineNumber") && parameters.get("lineNumber") != null && !parameters.get("lineNumber").isEmpty()) {
            ReceiptLine receiptLine = getReceiptLineFromHttpRequestParameters(parameters);
            addTrailerFlowModel.setupReceiptLine(receiptLine);
        }

    }

    public void removeReceiptLine(AddTrailerFlowModel addTrailerFlowModel, LocalParameterMap parameters){
        ReceiptLine receiptLine = getReceiptLineFromHttpRequestParameters(parameters);
        addTrailerFlowModel.removeReceiptLine(receiptLine);

    }

    public void serializeTrailer(AddTrailerFlowModel addTrailerFlowModel){
        Trailer trailer = addTrailerFlowModel.getTrailer();
        // All receipt information, along with their lines, are saved in
        // AddTrailerFlowModel object temporary when the workflow presents.
        // We will need to assign the receipts to the trailer
        // before serialize the trailer
        trailer.setReceiptList(addTrailerFlowModel.getReceiptList());
        System.out.println("Start to serialize trailer:" + trailer.getTrailerNumber() +
                " / " + trailer.getLicensePlate());
        System.out.println("# How many lines? " + addTrailerFlowModel.getReceiptList().size() + " / " + trailer.getReceiptList().size());
        for(Receipt receipt : trailer.getReceiptList()) {
            System.out.println(">>>>  Receipt: " + receipt.getNumber() + "    <<<<<");
            if (receipt.getReceiptLineList() == null || receipt.getReceiptLineList().size() == 0) {
                System.out.println("## Receipt Line Numbers: 0" );
            }
            else  {
                System.out.println("## Receipt Line Numbers: " + receipt.getReceiptLineList().size());
                for(ReceiptLine receiptLine : receipt.getReceiptLineList()) {
                    System.out.println("====== " + receiptLine.getLineNumber() + "  =======");
                    System.out.println(">>>> " + receiptLine.getItem().getName() );
                    System.out.println(">>>> " + receiptLine.getExpectedQuantity());
                    System.out.println(">>>> " + receiptLine.getInventoryStatus().getName() );
                }

            }
        }
        trailerService.save(trailer);

    }

    private Trailer getTrailerFromHttpRequestParameters(LocalParameterMap parameters) {
        Trailer trailer = new Trailer();
        Map<String, Object> parameterMap = parameters.asMap();

        for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
            try {
                String fieldName = parameter.getKey();
                Object fieldValue = parameter.getValue();

                Field field = trailer.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                // Special handling for
                // 1. trailerType: convert String to Enum
                // 2. carrierID: get Carrier from id
                if ("trailerType".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        field.set(trailer, TrailerType.valueOf(fieldValue.toString()));
                    }
                }
                else if ("carrier".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        Carrier carrier = carrierService.findByCarrierId(Integer.parseInt(fieldValue.toString()));
                        if (carrier != null) {
                            field.set(trailer, carrier);
                        }
                    }
                }
                else {
                    field.set(trailer, fieldValue);
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
        return trailer;
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
