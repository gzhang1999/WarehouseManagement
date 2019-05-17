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

package se.gzhang.scm.wms.outbound.order.model;

import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;

import java.io.Serializable;
import java.util.ArrayList;


public class AddSalesOrderFlowModel implements Serializable {

    private SalesOrder salesOrder;

    public AddSalesOrderFlowModel() {
        salesOrder = new SalesOrder();
    }
    public AddSalesOrderFlowModel(SalesOrder salesOrder) {

        this.salesOrder = salesOrder;
    }



    public void setupSalesOrder(SalesOrder salesOrder) {
        this.salesOrder.setExternalID(salesOrder.getExternalID());
        this.salesOrder.setNumber(salesOrder.getNumber());
        this.salesOrder.setShipToCustomer(salesOrder.getShipToCustomer());
        this.salesOrder.setBillToCustomer(salesOrder.getBillToCustomer());
        this.salesOrder.setWarehouse(salesOrder.getWarehouse());
        this.salesOrder.setShippingMethod(salesOrder.getShippingMethod());
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }
    public void setupSalesOrderLine(SalesOrderLine salesOrderLine) {
        SalesOrderLine existingSalesOrderLine = getExistingSalesOrderLine(salesOrderLine);
        if (existingSalesOrderLine == null) {
            // Copy the attribute from the receipt into current receipt structure
            // and save it to the list
            SalesOrderLine newSalesOrderLine = new SalesOrderLine();

            newSalesOrderLine.setExternalID(salesOrderLine.getExternalID());
            newSalesOrderLine.setLineNumber(salesOrderLine.getLineNumber());
            newSalesOrderLine.setQuantity(salesOrderLine.getQuantity());
            newSalesOrderLine.setInventoryStatus(salesOrderLine.getInventoryStatus());
            newSalesOrderLine.setItem(salesOrderLine.getItem());
            newSalesOrderLine.setSalesOrder(salesOrder);


            addSalesOrderLine(newSalesOrderLine);
        }
        else {
            existingSalesOrderLine.setExternalID(salesOrderLine.getExternalID());
            // The only thing we can't change for an existing sales order line is
            // its line number, which is a business primary key
            // existingSalesOrderLine.setLineNumber(receiptLine.getLineNumber());
            existingSalesOrderLine.setQuantity(salesOrderLine.getQuantity());
            existingSalesOrderLine.setInventoryStatus(salesOrderLine.getInventoryStatus());
            existingSalesOrderLine.setItem(salesOrderLine.getItem());
            existingSalesOrderLine.setSalesOrder(salesOrder);
        }

    }
    private SalesOrderLine getExistingSalesOrderLine(SalesOrderLine salesOrderLine) {
        // Get the receipt line from current Receipt if the
        // receipt line number is the same
        if (salesOrder.getSalesOrderLines() == null
                || salesOrder.getSalesOrderLines().size() == 0) {
            return null;
        }

        for(SalesOrderLine existingSalesOrderLine : salesOrder.getSalesOrderLines()) {
            if (existingSalesOrderLine.equals(salesOrderLine)) {
                return existingSalesOrderLine;
            }
        }
        return null;
    }
    private void addSalesOrderLine(SalesOrderLine salesOrderLine) {
        if (salesOrder.getSalesOrderLines() == null) {
            salesOrder.setSalesOrderLines(new ArrayList<SalesOrderLine>());
        }
        salesOrder.getSalesOrderLines().add(salesOrderLine);
    }

    public void removeSalesOrderLine(SalesOrderLine salesOrderLine) {
        // We will only remove the sales order line when the line already exists
        // in the current modal
        SalesOrderLine existingSalesOrderLine = getExistingSalesOrderLine(salesOrderLine);
        if (existingSalesOrderLine != null) {
            salesOrder.getSalesOrderLines().remove(existingSalesOrderLine);
        }
    }

}
