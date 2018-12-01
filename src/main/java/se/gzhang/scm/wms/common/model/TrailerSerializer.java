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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrailerSerializer extends StdSerializer<Trailer> {

        public TrailerSerializer() {
            this(null);
        }

        public TrailerSerializer(Class<Trailer> trailer) {
            super(trailer);
        }

        @Override
        public void serialize(
                Trailer trailer, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            jgen.writeStartObject();
            jgen.writeNumberField("id", trailer.getId());
            jgen.writeStringField("type", trailer.getTrailerType().name());
            jgen.writeStringField("number", trailer.getTrailerNumber()  == null ? "" :  trailer.getTrailerNumber());
            jgen.writeStringField("licensePlate", trailer.getLicensePlate() == null ? "" : trailer.getLicensePlate());
            jgen.writeStringField("driver", trailer.getDriver() == null ? "" : trailer.getDriver());
            jgen.writeStringField("driverTelephone", trailer.getDriverTelephone() == null ? "" : trailer.getDriverTelephone());
            jgen.writeStringField("state", trailer.getTrailerState().name());
            jgen.writeStringField("checkedInDate", (trailer.getCheckedInDate() == null ? "" : dateFormat.format(trailer.getCheckedInDate())));
            jgen.writeStringField("closedDate", (trailer.getClosedDate() == null ? "" : dateFormat.format(trailer.getClosedDate())));
            jgen.writeStringField("dispatchedDate", (trailer.getDispatchedDate() == null ? "" : dateFormat.format(trailer.getDispatchedDate())));

            jgen.writeFieldName("carrier");
            jgen.writeStartObject();
            if (trailer.getCarrier() != null) {

                jgen.writeNumberField("id", trailer.getCarrier().getId());
                jgen.writeStringField("name", trailer.getCarrier().getName());
                jgen.writeStringField("description", trailer.getCarrier().getDescription());
                jgen.writeStringField("contactPerson", trailer.getCarrier().getContactPerson());
                jgen.writeStringField("contactPersonTelephone", trailer.getCarrier().getContactPersonTelephone());
            }
            jgen.writeEndObject();

            jgen.writeFieldName("location");
            jgen.writeStartObject();
            if (trailer.getLocation() != null) {

                jgen.writeNumberField("id", trailer.getLocation().getId());
                jgen.writeStringField("name", trailer.getLocation().getName());
            }
            jgen.writeEndObject();

            jgen.writeFieldName("receipts");
            jgen.writeStartArray();
            if (trailer.getReceiptList() != null) {
                for (Receipt receipt : trailer.getReceiptList()) {
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
                    jgen.writeEndObject();
                }
            }
            jgen.writeEndArray();


            jgen.writeEndObject();
        }
}
