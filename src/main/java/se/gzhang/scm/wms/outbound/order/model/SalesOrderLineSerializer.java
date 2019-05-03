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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import se.gzhang.scm.wms.outbound.shipment.model.Pick;
import se.gzhang.scm.wms.outbound.shipment.model.PickState;
import se.gzhang.scm.wms.outbound.shipment.model.ShipmentLine;
import se.gzhang.scm.wms.outbound.shipment.model.ShortAllocation;

import java.io.IOException;

public class SalesOrderLineSerializer extends StdSerializer<SalesOrderLine> {

        public SalesOrderLineSerializer() {
            this(null);
        }

        public SalesOrderLineSerializer(Class<SalesOrderLine> salesOrderLine) {
            super(salesOrderLine);
        }

        @Override
        public void serialize(
                SalesOrderLine salesOrderLine, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", salesOrderLine.getId());
        jgen.writeStringField("externalID", salesOrderLine.getExternalID());
        jgen.writeStringField("lineNumber", salesOrderLine.getLineNumber());
        jgen.writeNumberField("quantity", salesOrderLine.getQuantity());


        jgen.writeFieldName("salesOrder");
        jgen.writeStartObject();
        if (salesOrderLine.getSalesOrder() != null) {
            jgen.writeNumberField("id", salesOrderLine.getSalesOrder().getId());
            jgen.writeStringField("externalID", salesOrderLine.getSalesOrder().getExternalID());
            jgen.writeStringField("number", salesOrderLine.getSalesOrder().getNumber());

            jgen.writeFieldName("warehouse");
            jgen.writeStartObject();
            if (salesOrderLine.getSalesOrder().getWarehouse() != null) {
                jgen.writeNumberField("id", salesOrderLine.getSalesOrder().getWarehouse().getId());
                jgen.writeStringField("name", salesOrderLine.getSalesOrder().getWarehouse().getName());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();

        jgen.writeFieldName("item");
        jgen.writeStartObject();
        if (salesOrderLine.getItem() != null) {
            jgen.writeNumberField("id", salesOrderLine.getItem().getId());
            jgen.writeStringField("name", salesOrderLine.getItem().getName());
            jgen.writeStringField("description", salesOrderLine.getItem().getDescription());

            jgen.writeFieldName("client");
            jgen.writeStartObject();
            jgen.writeNumberField("id", salesOrderLine.getItem().getClient().getId());
            jgen.writeStringField("name", salesOrderLine.getItem().getClient().getName());
            jgen.writeEndObject();

        }
        jgen.writeEndObject();


        jgen.writeFieldName("inventoryStatus");
        jgen.writeStartObject();
        if (salesOrderLine.getInventoryStatus() != null) {
            jgen.writeStringField("name", salesOrderLine.getInventoryStatus().getName());
            jgen.writeStringField("description", salesOrderLine.getInventoryStatus().getDescription());

        }
        jgen.writeEndObject();

        jgen.writeFieldName("shipmentLines");
        jgen.writeStartArray();
        if (salesOrderLine.getShipmentLineList() != null) {
            for (ShipmentLine shipmentLine : salesOrderLine.getShipmentLineList()) {
                jgen.writeStartObject();

                jgen.writeNumberField("id", shipmentLine.getId());
                jgen.writeStringField("number", shipmentLine.getNumber());
                jgen.writeNumberField("orderQuantity", shipmentLine.getOrderQuantity());
                jgen.writeNumberField("inprocessQuantity", shipmentLine.getInprocessQuantity());
                jgen.writeNumberField("shippedQuantity", shipmentLine.getShippedQuantity());
                jgen.writeStringField("state", shipmentLine.getShipmentLineState().name());

                jgen.writeFieldName("picks");
                jgen.writeStartArray();
                for(Pick pick : shipmentLine.getPicks()) {
                    if (pick.getPickState() != PickState.CANCELLED) {
                        jgen.writeStartObject();
                        jgen.writeNumberField("id", pick.getId());
                        jgen.writeStringField("number", pick.getNumber());
                        jgen.writeNumberField("quantity", pick.getQuantity());
                        jgen.writeNumberField("pickedQuantity", pick.getPickedQuantity());
                        jgen.writeStringField("pickState", pick.getPickState().name());

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

                        jgen.writeEndObject();
                    }
                }
                jgen.writeEndArray();

                jgen.writeFieldName("shortAllocation");
                jgen.writeStartArray();
                for(ShortAllocation shortAllocation : shipmentLine.getShortAllocation()) {
                    jgen.writeStartObject();
                    jgen.writeNumberField("id", shortAllocation.getId());
                    jgen.writeStringField("number", shortAllocation.getNumber());
                    jgen.writeNumberField("quantity", shortAllocation.getQuantity());

                    jgen.writeEndObject();
                }
                jgen.writeEndArray();

                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }
}
