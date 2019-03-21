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

package se.gzhang.scm.wms.inbound.model;

import java.io.Serializable;
import java.util.ArrayList;

// Model to be used in the flow of adding receipt without
// any trailer. We allow the operator to receive inventory
// from receipt with or without trailer
public class AddReceiptFlowModel implements Serializable {

    private Receipt currentReceipt;

    public AddReceiptFlowModel() {
        currentReceipt = new Receipt();
    }

    public void setupReceipt(Receipt receipt) {
        currentReceipt.setExternalID(receipt.getExternalID());
        currentReceipt.setNumber(receipt.getNumber());
        currentReceipt.setPurchaseOrderNumber(receipt.getPurchaseOrderNumber());
        currentReceipt.setSupplier(receipt.getSupplier());
        currentReceipt.setWarehouse(receipt.getWarehouse());

    }

    public void setupReceiptLine(ReceiptLine receiptLine) {
        ReceiptLine existingReceiptLine = getExistingReceiptLine(receiptLine);
        if (existingReceiptLine == null) {
            // Copy the attribute from the receipt into current receipt structure
            // and save it to the list
            ReceiptLine newReceiptLine = new ReceiptLine();

            newReceiptLine.setExternalID(receiptLine.getExternalID());
            newReceiptLine.setLineNumber(receiptLine.getLineNumber());
            newReceiptLine.setExpectedQuantity(receiptLine.getExpectedQuantity());
            newReceiptLine.setReceipt(currentReceipt);
            newReceiptLine.setItem(receiptLine.getItem());
            newReceiptLine.setInventoryStatus(receiptLine.getInventoryStatus());
            // Always default the received quantity to 0 for new receipt line
            newReceiptLine.setReceivedQuantity(0);

            addReceiptLine(newReceiptLine);
        }
        else {
            existingReceiptLine.setExternalID(receiptLine.getExternalID());
            // The only thing we can't change for an existing receipt line is
            // its line number, which is a business primary key
            // existingReceipt.setLineNumber(receiptLine.getLineNumber());
            existingReceiptLine.setExpectedQuantity(receiptLine.getExpectedQuantity());
            existingReceiptLine.setReceipt(currentReceipt);
            existingReceiptLine.setItem(receiptLine.getItem());
            existingReceiptLine.setInventoryStatus(receiptLine.getInventoryStatus());
        }

    }

    private ReceiptLine getExistingReceiptLine(ReceiptLine receiptLine) {
        // Get the receipt line from current Receipt if the
        // receipt line number is the same
        if (currentReceipt.getReceiptLineList() == null
                || currentReceipt.getReceiptLineList().size() == 0) {
            return null;
        }

        for(ReceiptLine existingReceiptLine : currentReceipt.getReceiptLineList()) {
            if (existingReceiptLine.equals(receiptLine)) {
                return existingReceiptLine;
            }
        }
        return null;
    }

    public Receipt getCurrentReceipt() {
        return currentReceipt;
    }

    private void addReceiptLine(ReceiptLine receiptLine) {
        if (currentReceipt.getReceiptLineList() == null) {
            currentReceipt.setReceiptLineList(new ArrayList<ReceiptLine>());
        }
        currentReceipt.getReceiptLineList().add(receiptLine);
    }


    public void removeReceiptLine(ReceiptLine receiptLine) {
        // We will only remove the receipt when the receipt already exists
        // in the current modal
        ReceiptLine existingReceiptLine = getExistingReceiptLine(receiptLine);
        if (existingReceiptLine != null) {
            currentReceipt.getReceiptLineList().remove(existingReceiptLine);
        }
    }

}
