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
import se.gzhang.scm.wms.common.model.UnitOfMeasure;
import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.inventory.model.ItemFootprintUOM;

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
                jgen.writeStringField("description", receiptLine.getItem().getDescription());
                jgen.writeEndObject();

                jgen.writeFieldName("inventoryStatus");
                jgen.writeStartObject();
                jgen.writeNumberField("id", receiptLine.getInventoryStatus().getId());
                jgen.writeStringField("name", receiptLine.getInventoryStatus().getName());
                jgen.writeEndObject();


                jgen.writeFieldName("receivedInventory");
                jgen.writeStartArray();
                if (receiptLine.getReceivedInventory() != null) {
                    for (Inventory inventory : receiptLine.getReceivedInventory()) {
                        jgen.writeStartObject();
                        jgen.writeNumberField("id", inventory.getId());
                        jgen.writeStringField("lpn", inventory.getLpn());
                        jgen.writeNumberField("quantity", inventory.getQuantity());

                        // Write location / area / warehouse object
                        jgen.writeFieldName("location");
                        jgen.writeStartObject();
                        if (inventory.getLocation() != null) {
                            jgen.writeNumberField("id", inventory.getLocation().getId());
                            jgen.writeStringField("name", inventory.getLocation().getName());

                            jgen.writeFieldName("area");
                            jgen.writeStartObject();
                            if (inventory.getLocation().getArea() != null) {
                                jgen.writeNumberField("id", inventory.getLocation().getArea().getId());
                                jgen.writeStringField("name", inventory.getLocation().getArea().getName());

                                jgen.writeFieldName("building");
                                jgen.writeStartObject();
                                if (inventory.getLocation().getArea().getBuilding() != null) {
                                    jgen.writeNumberField("id", inventory.getLocation().getArea().getBuilding().getId());
                                    jgen.writeStringField("name", inventory.getLocation().getArea().getBuilding().getName());
                                    jgen.writeFieldName("warehouse");
                                    jgen.writeStartObject();
                                    if (inventory.getLocation().getArea().getBuilding().getWarehouse() != null) {
                                        jgen.writeNumberField("id", inventory.getLocation().getArea().getBuilding().getWarehouse().getId());
                                        jgen.writeStringField("name", inventory.getLocation().getArea().getBuilding().getWarehouse().getName());
                                    }
                                    jgen.writeEndObject();
                                }
                                jgen.writeEndObject();
                            }
                            jgen.writeEndObject();
                        }
                        jgen.writeEndObject();

                        // write item / footprint
                        jgen.writeFieldName("itemFootprint");
                        jgen.writeStartObject();
                        if (inventory.getItemFootprint() != null) {
                            jgen.writeNumberField("id", inventory.getItemFootprint().getId());
                            jgen.writeStringField("name", inventory.getItemFootprint().getName());
                            jgen.writeStringField("description", inventory.getItemFootprint().getDescription());

                            UnitOfMeasure stockUOM = null;
                            int stockUOMQuantity = 0;
                            jgen.writeFieldName("itemFootprintUOMs");
                            jgen.writeStartArray();
                            for(ItemFootprintUOM itemFootprintUOM : inventory.getItemFootprint().getItemFootprintUOMs()) {

                                jgen.writeStartObject();
                                jgen.writeNumberField("id", itemFootprintUOM.getId());
                                jgen.writeFieldName("unitOfMeasure");
                                jgen.writeStartObject();
                                jgen.writeStringField("name", itemFootprintUOM.getUnitOfMeasure().getName());
                                jgen.writeStringField("description", itemFootprintUOM.getUnitOfMeasure().getDescription());
                                jgen.writeEndObject();
                                jgen.writeNumberField("quantity", itemFootprintUOM.getQuantity());

                                if (stockUOMQuantity == 0) {
                                    // Initial the stock UOM
                                    stockUOM = itemFootprintUOM.getUnitOfMeasure();
                                    stockUOMQuantity = itemFootprintUOM.getQuantity();
                                }
                                else if (itemFootprintUOM.getQuantity() < stockUOMQuantity) {
                                    // Set the Stock UOM to the smallest UOM of the footprint
                                    stockUOM = itemFootprintUOM.getUnitOfMeasure();
                                    stockUOMQuantity = itemFootprintUOM.getQuantity();
                                }
                                jgen.writeEndObject();
                            }
                            jgen.writeEndArray();

                            jgen.writeFieldName("stockUOM");
                            jgen.writeStartObject();
                            if (stockUOM != null) {
                                jgen.writeStringField("name", stockUOM.getName());
                                jgen.writeNumberField("quantity", stockUOMQuantity);
                            }
                            jgen.writeEndObject();


                            jgen.writeFieldName("item");
                            jgen.writeStartObject();
                            if (inventory.getItemFootprint().getItem() != null) {
                                jgen.writeNumberField("id", inventory.getItemFootprint().getItem().getId());
                                jgen.writeStringField("name", inventory.getItemFootprint().getItem().getName());
                                jgen.writeStringField("description", inventory.getItemFootprint().getItem().getDescription());

                                jgen.writeFieldName("client");
                                jgen.writeStartObject();
                                if (inventory.getItemFootprint().getItem().getClient() != null) {
                                    jgen.writeNumberField("id", inventory.getItemFootprint().getItem().getClient().getId());
                                    jgen.writeStringField("name", inventory.getItemFootprint().getItem().getClient().getName());

                                }
                                jgen.writeEndObject();
                            }
                            jgen.writeEndObject();
                        }
                        jgen.writeEndObject();

                        jgen.writeEndObject();
                    }
                }
                jgen.writeEndArray();

                jgen.writeEndObject();

            }
        }
        jgen.writeEndArray();

        jgen.writeFieldName("trailer");
        jgen.writeStartObject();
        if (receipt.getTrailer() != null && receipt.getTrailer().getId() != null) {
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

            jgen.writeFieldName("carrier");
            jgen.writeStartObject();
            if (receipt.getTrailer().getCarrier() != null && receipt.getTrailer().getCarrier().getId() != null) {
                jgen.writeNumberField("id", receipt.getTrailer().getCarrier().getId());

                jgen.writeStringField("name", receipt.getTrailer().getCarrier().getName());
                jgen.writeStringField("description", receipt.getTrailer().getCarrier().getDescription());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();

        jgen.writeEndObject();
    }
}
