/**
 * Copyright 2019
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

package se.gzhang.scm.wms.outbound.shipment.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PickSerializer extends StdSerializer<Pick> {

    public PickSerializer() {
        this(null);
    }

    public PickSerializer(Class<Pick> pick) {
        super(pick);
    }

    @Override
    public void serialize(
            Pick pick, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeNumberField("id", pick.getId());
        jgen.writeStringField("number", pick.getNumber());
        jgen.writeNumberField("quantity", pick.getQuantity());
        jgen.writeNumberField("pickedQuantity", pick.getPickedQuantity());
        jgen.writeStringField("pickState", pick.getPickState().name());
        jgen.writeStringField("cancelledDate", pick.getCancelledDate() == null ? "" : pick.getCancelledDate().toString());

        jgen.writeFieldName("sourceLocation");
        jgen.writeStartObject();
        if (pick.getSourceLocation() != null) {
            jgen.writeNumberField("id", pick.getSourceLocation().getId());
            jgen.writeStringField("name", pick.getSourceLocation().getName());

            jgen.writeFieldName("area");
            jgen.writeStartObject();
            if (pick.getSourceLocation().getArea() != null) {
                jgen.writeNumberField("id", pick.getSourceLocation().getArea().getId());
                jgen.writeStringField("name", pick.getSourceLocation().getArea().getName());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();

        jgen.writeFieldName("destinationLocation");
        jgen.writeStartObject();
        if (pick.getDestinationLocation() != null) {
            jgen.writeNumberField("id", pick.getDestinationLocation().getId());
            jgen.writeStringField("name", pick.getDestinationLocation().getName());

            jgen.writeFieldName("area");
            jgen.writeStartObject();
            if (pick.getDestinationLocation().getArea() != null) {
                jgen.writeNumberField("id", pick.getDestinationLocation().getArea().getId());
                jgen.writeStringField("name", pick.getDestinationLocation().getArea().getName());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();


        jgen.writeFieldName("shipmentLine");
        jgen.writeStartObject();
        if (pick.getShipmentLine() != null) {

            jgen.writeNumberField("id", pick.getShipmentLine().getId());
            jgen.writeStringField("number", pick.getShipmentLine().getNumber());
            jgen.writeNumberField("orderQuantity", pick.getShipmentLine().getOrderQuantity());
            jgen.writeNumberField("inprocessQuantity", pick.getShipmentLine().getInprocessQuantity());
            jgen.writeNumberField("shippedQuantity", pick.getShipmentLine().getShippedQuantity());
            jgen.writeStringField("state", pick.getShipmentLine().getShipmentLineState().name());

            jgen.writeFieldName("salesOrderLine");
            jgen.writeStartObject();
            if (pick.getShipmentLine().getSalesOrderLine() != null) {
                jgen.writeNumberField("id", pick.getShipmentLine().getSalesOrderLine().getId());
                jgen.writeStringField("number", pick.getShipmentLine().getSalesOrderLine().getLineNumber());
                jgen.writeStringField("externalID", pick.getShipmentLine().getSalesOrderLine().getExternalID());
                jgen.writeNumberField("quantity", pick.getShipmentLine().getSalesOrderLine().getQuantity());

                jgen.writeFieldName("inventoryStatus");
                jgen.writeStartObject();
                if (pick.getShipmentLine().getSalesOrderLine().getInventoryStatus() != null) {
                    jgen.writeNumberField("id", pick.getShipmentLine().getSalesOrderLine().getInventoryStatus().getId());
                    jgen.writeStringField("name", pick.getShipmentLine().getSalesOrderLine().getInventoryStatus().getName());
                }
                jgen.writeEndObject();

                jgen.writeFieldName("item");
                jgen.writeStartObject();
                if (pick.getShipmentLine().getSalesOrderLine().getItem() != null) {
                    jgen.writeNumberField("id", pick.getShipmentLine().getSalesOrderLine().getItem().getId());
                    jgen.writeStringField("name", pick.getShipmentLine().getSalesOrderLine().getItem().getName());
                }
                jgen.writeEndObject();

                jgen.writeFieldName("salesOrder");
                jgen.writeStartObject();
                if (pick.getShipmentLine().getSalesOrderLine().getSalesOrder() != null) {
                    jgen.writeNumberField("id", pick.getShipmentLine().getSalesOrderLine().getSalesOrder().getId());
                    jgen.writeStringField("number", pick.getShipmentLine().getSalesOrderLine().getSalesOrder().getNumber());
                    jgen.writeStringField("externalID", pick.getShipmentLine().getSalesOrderLine().getSalesOrder().getExternalID());
                }
                jgen.writeEndObject();
            }
            jgen.writeEndObject();

            jgen.writeFieldName("shipment");
            jgen.writeStartObject();
            if (pick.getShipmentLine().getShipment() != null) {
                jgen.writeNumberField("id", pick.getShipmentLine().getShipment().getId());
                jgen.writeStringField("number", pick.getShipmentLine().getShipment().getNumber());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();

        jgen.writeEndObject();
    }
}