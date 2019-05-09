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

public class ShortAllocationSerializer extends StdSerializer<ShortAllocation> {

    public ShortAllocationSerializer() {
        this(null);
    }

    public ShortAllocationSerializer(Class<ShortAllocation> shortAllocation) {
        super(shortAllocation);
    }

    @Override
    public void serialize(
            ShortAllocation shortAllocation, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeNumberField("id", shortAllocation.getId());
        jgen.writeStringField("number", shortAllocation.getNumber());
        jgen.writeNumberField("quantity", shortAllocation.getQuantity());
        jgen.writeStringField("state", shortAllocation.getShortAllocationState().name());
        jgen.writeStringField("cancelledDate", shortAllocation.getCancelledDate() == null ? "" : shortAllocation.getCancelledDate().toString());

        jgen.writeFieldName("shipmentLine");
        jgen.writeStartObject();
        if (shortAllocation.getShipmentLine() != null) {

            jgen.writeNumberField("id", shortAllocation.getShipmentLine().getId());
            jgen.writeStringField("number", shortAllocation.getShipmentLine().getNumber());
            jgen.writeNumberField("orderQuantity", shortAllocation.getShipmentLine().getOrderQuantity());
            jgen.writeNumberField("inprocessQuantity", shortAllocation.getShipmentLine().getInprocessQuantity());
            jgen.writeNumberField("shippedQuantity", shortAllocation.getShipmentLine().getShippedQuantity());
            jgen.writeStringField("state", shortAllocation.getShipmentLine().getShipmentLineState().name());

            jgen.writeFieldName("salesOrderLine");
            jgen.writeStartObject();
            if (shortAllocation.getShipmentLine().getSalesOrderLine() != null) {
                jgen.writeNumberField("id", shortAllocation.getShipmentLine().getSalesOrderLine().getId());
                jgen.writeStringField("number", shortAllocation.getShipmentLine().getSalesOrderLine().getLineNumber());
                jgen.writeStringField("externalID", shortAllocation.getShipmentLine().getSalesOrderLine().getExternalID());
                jgen.writeNumberField("quantity", shortAllocation.getShipmentLine().getSalesOrderLine().getQuantity());

                jgen.writeFieldName("inventoryStatus");
                jgen.writeStartObject();
                if (shortAllocation.getShipmentLine().getSalesOrderLine().getInventoryStatus() != null) {
                    jgen.writeNumberField("id", shortAllocation.getShipmentLine().getSalesOrderLine().getInventoryStatus().getId());
                    jgen.writeStringField("name", shortAllocation.getShipmentLine().getSalesOrderLine().getInventoryStatus().getName());
                }
                jgen.writeEndObject();

                jgen.writeFieldName("item");
                jgen.writeStartObject();
                if (shortAllocation.getShipmentLine().getSalesOrderLine().getItem() != null) {
                    jgen.writeNumberField("id", shortAllocation.getShipmentLine().getSalesOrderLine().getItem().getId());
                    jgen.writeStringField("name", shortAllocation.getShipmentLine().getSalesOrderLine().getItem().getName());
                }
                jgen.writeEndObject();

                jgen.writeFieldName("salesOrder");
                jgen.writeStartObject();
                if (shortAllocation.getShipmentLine().getSalesOrderLine().getSalesOrder() != null) {
                    jgen.writeNumberField("id", shortAllocation.getShipmentLine().getSalesOrderLine().getSalesOrder().getId());
                    jgen.writeStringField("number", shortAllocation.getShipmentLine().getSalesOrderLine().getSalesOrder().getNumber());
                    jgen.writeStringField("externalID", shortAllocation.getShipmentLine().getSalesOrderLine().getSalesOrder().getExternalID());
                }
                jgen.writeEndObject();
            }
            jgen.writeEndObject();

            jgen.writeFieldName("shipment");
            jgen.writeStartObject();
            if (shortAllocation.getShipmentLine().getShipment() != null) {
                jgen.writeNumberField("id", shortAllocation.getShipmentLine().getShipment().getId());
                jgen.writeStringField("number", shortAllocation.getShipmentLine().getShipment().getNumber());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();

        jgen.writeEndObject();
    }
}