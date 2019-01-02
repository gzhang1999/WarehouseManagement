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

package se.gzhang.scm.wms.common.model;

import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddTrailerFlowModel implements Serializable {

    private Trailer trailer;

    private Receipt currentReceipt;

    private List<Receipt> receiptList;

    public AddTrailerFlowModel() {

        trailer = new Trailer();
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setupTrailer(Trailer trailer) {
        this.trailer = trailer;

        switch (trailer.getTrailerType()) {
            case RECEIVING_TRAILER:
                receiptList = new ArrayList<>();
                break;
        }
    }

    public void addEmptyReceipt() {
        currentReceipt = new Receipt();
    }

    public void setupReceipt(Receipt receipt) {
        // If the receipt already exists, then change the attribute and point current receipt
        // into this receipt.
        // Otherwise create the new receipt and add it to the list
        Receipt existingReceipt = getExistingReceipt(receipt);
        if (existingReceipt == null) {
            // Copy the attribute from the receipt into current receipt structure
            // and save it to the list
            currentReceipt.setExternalID(receipt.getExternalID());
            currentReceipt.setNumber(receipt.getNumber());
            currentReceipt.setPurchaseOrderNumber(receipt.getPurchaseOrderNumber());
            currentReceipt.setSupplier(receipt.getSupplier());
            currentReceipt.setTrailer(trailer);
            receiptList.add(currentReceipt);
        }
        else {
            existingReceipt.setExternalID(receipt.getExternalID());
            // existingReceipt.setNumber(receipt.getNumber());
            existingReceipt.setPurchaseOrderNumber(receipt.getPurchaseOrderNumber());
            existingReceipt.setSupplier(receipt.getSupplier());
            currentReceipt = existingReceipt;
        }

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

    public void removeReceipt(Receipt receipt) {
        // We will only remove the receipt when the receipt already exists
        // in the current modal
        Receipt existingReceipt = getExistingReceipt(receipt);
        if (existingReceipt != null) {
            receiptList.remove(existingReceipt);
        }

    }
    public void removeReceiptLine(ReceiptLine receiptLine) {
        // We will only remove the receipt when the receipt already exists
        // in the current modal
        ReceiptLine existingReceiptLine = getExistingReceiptLine(receiptLine);
        if (existingReceiptLine != null) {
            getCurrentReceiptLinesList().remove(existingReceiptLine);
        }

    }
    private Receipt getExistingReceipt(Receipt receipt) {
        for(Receipt existingReceipt : receiptList) {
            if (existingReceipt.equals(receipt)) {
                return existingReceipt;
            }
        }
        return null;
    }
    private ReceiptLine getExistingReceiptLine(ReceiptLine receiptLine) {
        // Get the receipt line from current Receipt if the
        // receipt line number is the same
        for(ReceiptLine existingReceiptLine : getCurrentReceiptLinesList()) {
            if (existingReceiptLine.equals(receiptLine)) {
                return existingReceiptLine;
            }
        }
        return null;
    }

    public Receipt getCurrentReceipt() {
        return currentReceipt;
    }
    public List<ReceiptLine> getCurrentReceiptLinesList() {
        return currentReceipt.getReceiptLineList();
    }

    private void addReceiptLine(ReceiptLine receiptLine) {
        if (currentReceipt.getReceiptLineList() == null) {
            currentReceipt.setReceiptLineList(new ArrayList<ReceiptLine>());
        }
        currentReceipt.getReceiptLineList().add(receiptLine);
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public void setCurrentReceipt(Receipt currentReceipt) {
        this.currentReceipt = currentReceipt;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }
}
