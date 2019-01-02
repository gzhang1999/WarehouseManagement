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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ReceiptSerializer extends StdSerializer<Receipt> {

        public ReceiptSerializer() {
            this(null);
        }

        public ReceiptSerializer(Class<Receipt> receipt) {
            super(receipt);
        }

        @Override
        public void serialize(
                Receipt receipt, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", receipt.getId());
        jgen.writeStringField("externalID", receipt.getExternalID());
        jgen.writeStringField("number", receipt.getNumber());
        jgen.writeStringField("purchaseOrderNumber", receipt.getPurchaseOrderNumber());


        jgen.writeFieldName("supplier");
        jgen.writeStartObject();
        if (receipt.getSupplier() != null) {
            jgen.writeNumberField("id", receipt.getSupplier().getId());
            jgen.writeStringField("name", receipt.getSupplier().getName());
            jgen.writeStringField("description", receipt.getSupplier().getDescription());
            jgen.writeStringField("contactPerson", receipt.getSupplier().getContactPerson());
            jgen.writeStringField("contactPersonTelephone", receipt.getSupplier().getContactPersonTelephone());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("receiptLines");
        jgen.writeStartArray();
        if (receipt.getReceiptLineList() != null) {
            for (ReceiptLine receiptLine : receipt.getReceiptLineList()) {
                jgen.writeStartObject();
                jgen.writeNumberField("id", receiptLine.getId());
                jgen.writeStringField("externalID", receiptLine.getExternalID());
                jgen.writeStringField("lineNumber", receiptLine.getLineNumber());
                jgen.writeNumberField("expectedQuantity", receiptLine.getExpectedQuantity());
                jgen.writeNumberField("receivedQuantity", receiptLine.getReceivedQuantity());

                jgen.writeFieldName("item");
                jgen.writeStartObject();
                jgen.writeNumberField("id", receiptLine.getItem().getId());
                jgen.writeStringField("name", receiptLine.getItem().getName());
                jgen.writeEndObject();

                jgen.writeFieldName("inventoryStatus");
                jgen.writeStartObject();
                jgen.writeNumberField("id", receiptLine.getInventoryStatus().getId());
                jgen.writeStringField("name", receiptLine.getInventoryStatus().getName());
                jgen.writeEndObject();

                jgen.writeEndObject();

            }
        }
        jgen.writeEndArray();

        jgen.writeFieldName("trailer");
        jgen.writeStartObject();
        if (receipt.getTrailer() != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            jgen.writeNumberField("id", receipt.getTrailer().getId());
            jgen.writeStringField("type", receipt.getTrailer().getTrailerType().name());
            jgen.writeStringField("number", receipt.getTrailer().getTrailerNumber()  == null ? "" :  receipt.getTrailer().getTrailerNumber());
            jgen.writeStringField("licensePlate", receipt.getTrailer().getLicensePlate() == null ? "" : receipt.getTrailer().getLicensePlate());
            jgen.writeStringField("driver", receipt.getTrailer().getDriver() == null ? "" : receipt.getTrailer().getDriver());
            jgen.writeStringField("driverTelephone", receipt.getTrailer().getDriverTelephone() == null ? "" : receipt.getTrailer().getDriverTelephone());
            jgen.writeStringField("state", receipt.getTrailer().getTrailerState().name());
            jgen.writeStringField("checkedInDate", (receipt.getTrailer().getCheckedInDate() == null ? "" : dateFormat.format(receipt.getTrailer().getCheckedInDate())));
            jgen.writeStringField("closedDate", (receipt.getTrailer().getClosedDate() == null ? "" : dateFormat.format(receipt.getTrailer().getClosedDate())));
            jgen.writeStringField("dispatchedDate", (receipt.getTrailer().getDispatchedDate() == null ? "" : dateFormat.format(receipt.getTrailer().getDispatchedDate())));
        }
        jgen.writeEndObject();

        jgen.writeEndObject();
    }
}
