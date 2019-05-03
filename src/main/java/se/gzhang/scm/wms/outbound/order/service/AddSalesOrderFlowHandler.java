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
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.LocalParameterMap;
import se.gzhang.scm.wms.common.model.Customer;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.service.CustomerService;
import se.gzhang.scm.wms.common.service.SupplierService;
import se.gzhang.scm.wms.inbound.model.AddReceiptFlowModel;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inbound.service.ReceiptService;
import se.gzhang.scm.wms.inventory.model.InventoryStatus;
import se.gzhang.scm.wms.inventory.model.Item;
import se.gzhang.scm.wms.inventory.service.InventoryStatusService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.outbound.order.model.AddSalesOrderFlowModel;
import se.gzhang.scm.wms.outbound.order.model.SalesOrder;
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class AddSalesOrderFlowHandler {

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private InventoryStatusService inventoryStatusService;

    @Autowired
    private SalesOrderService salesOrderService;



    public AddSalesOrderFlowModel init() {
        return new AddSalesOrderFlowModel();
    }
    public AddSalesOrderFlowModel init(int salesOrderID) {
        return new AddSalesOrderFlowModel(salesOrderService.findBySalesOrderId(salesOrderID));
    }

    public void saveSalesOrder(AddSalesOrderFlowModel addSalesOrderFlowModel, LocalParameterMap parameters, HttpSession session){
        SalesOrder salesOrder = getSalesOrderFromHttpRequestParameters(parameters);

        salesOrder.setWarehouse(warehouseService.findByWarehouseId(Integer.parseInt(session.getAttribute("warehouse_id").toString())));
        addSalesOrderFlowModel.setupSalesOrder(salesOrder);
    }
    public void serializeSalesOrder(AddSalesOrderFlowModel addSalesOrderFlowModel){
        SalesOrder salesOrder = addSalesOrderFlowModel.getSalesOrder();
        salesOrderService.save(salesOrder);
    }

    public void saveSalesOrderLine(AddSalesOrderFlowModel addSalesOrderFlowModel, LocalParameterMap parameters) {
        if (parameters.contains("lineNumber") && parameters.get("lineNumber") != null && !parameters.get("lineNumber").isEmpty()) {
            SalesOrderLine salesOrderLine  = getSalesOrderLineFromHttpRequestParameters(parameters);
            addSalesOrderFlowModel.setupSalesOrderLine(salesOrderLine);
        }

    }

    public void removeSalesOrderLine(AddSalesOrderFlowModel addSalesOrderFlowModel, LocalParameterMap parameters){
        SalesOrderLine salesOrderLine  = getSalesOrderLineFromHttpRequestParameters(parameters);
        addSalesOrderFlowModel.removeSalesOrderLine(salesOrderLine);

    }

    private SalesOrder getSalesOrderFromHttpRequestParameters(LocalParameterMap parameters) {
        SalesOrder salesOrder = new SalesOrder();

        Map<String, Object> parameterMap = parameters.asMap();

        for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
            try {

                String fieldName = parameter.getKey();
                Object fieldValue = parameter.getValue();

                Field field = salesOrder.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                // Special handling for
                // 1. bill to / ship to customer: find the customer by passing id
                if ("billToCustomer".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        Customer billToCustomer = customerService.findByCustomerId(Integer.parseInt(fieldValue.toString()));
                        if (billToCustomer != null) {
                            field.set(salesOrder, billToCustomer);
                        }
                    }
                }
                else if ("shipToCustomer".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        Customer shipToCustomer = customerService.findByCustomerId(Integer.parseInt(fieldValue.toString()));
                        if (shipToCustomer != null) {
                            field.set(salesOrder, shipToCustomer);
                        }
                    }
                }
                else if (field.getType() == Integer.class) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        field.set(salesOrder, Integer.parseInt(fieldValue.toString()));
                    }
                }
                else {
                    field.set(salesOrder, fieldValue);

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
        return salesOrder;
    }

    private SalesOrderLine getSalesOrderLineFromHttpRequestParameters(LocalParameterMap parameters) {
        SalesOrderLine salesOrderLine = new SalesOrderLine();

        Map<String, Object> parameterMap = parameters.asMap();

        for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
            try {
                String fieldName = parameter.getKey();
                Object fieldValue = parameter.getValue();

                Field field = salesOrderLine.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                // Special handling for
                // 1. Item
                // 2. Inventory Status
                if ("item".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        Item item = itemService.findByItemName(fieldValue.toString());
                        if (item != null) {
                            field.set(salesOrderLine, item);
                        }
                    }
                }
                else if ("inventoryStatus".equals(fieldName)) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        InventoryStatus inventoryStatus = inventoryStatusService.findByInventoryStatusName(fieldValue.toString());
                        if (inventoryStatus != null) {
                            field.set(salesOrderLine, inventoryStatus);
                        }
                    }
                }
                else if (field.getType() == Integer.class) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        field.set(salesOrderLine, Integer.parseInt(fieldValue.toString()));
                    }
                }
                else {
                    field.set(salesOrderLine, fieldValue);

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
        return salesOrderLine;
    }
}
