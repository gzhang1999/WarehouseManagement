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

public class ShipmentLineSerializer extends StdSerializer<ShipmentLine> {

    public ShipmentLineSerializer() {
        this(null);
    }

    public ShipmentLineSerializer(Class<ShipmentLine> shipmentLine) {
        super(shipmentLine);
    }

    @Override
    public void serialize(
            ShipmentLine shipmentLine, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeNumberField("id", shipmentLine.getId());
        jgen.writeStringField("number", shipmentLine.getNumber());
        jgen.writeNumberField("orderQuantity", shipmentLine.getOrderQuantity());
        jgen.writeNumberField("inprocessQuantity", shipmentLine.getInprocessQuantity());
        jgen.writeNumberField("shippedQuantity", shipmentLine.getShippedQuantity());
        jgen.writeStringField("state", shipmentLine.getShipmentLineState().name());

        jgen.writeFieldName("salesOrderLine");
        jgen.writeStartObject();
        if (shipmentLine.getSalesOrderLine() != null) {
            jgen.writeNumberField("id", shipmentLine.getSalesOrderLine().getId());
            jgen.writeStringField("number", shipmentLine.getSalesOrderLine().getLineNumber());
            jgen.writeStringField("externalID", shipmentLine.getSalesOrderLine().getExternalID());
            jgen.writeNumberField("quantity", shipmentLine.getSalesOrderLine().getQuantity());

            jgen.writeFieldName("inventoryStatus");
            jgen.writeStartObject();
            if (shipmentLine.getSalesOrderLine().getInventoryStatus() != null) {
                jgen.writeNumberField("id", shipmentLine.getSalesOrderLine().getInventoryStatus().getId());
                jgen.writeStringField("name", shipmentLine.getSalesOrderLine().getInventoryStatus().getName());
            }
            jgen.writeEndObject();

            jgen.writeFieldName("item");
            jgen.writeStartObject();
            if (shipmentLine.getSalesOrderLine().getItem() != null) {
                jgen.writeNumberField("id", shipmentLine.getSalesOrderLine().getItem().getId());
                jgen.writeStringField("name", shipmentLine.getSalesOrderLine().getItem().getName());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();

        jgen.writeFieldName("picks");
        jgen.writeStartArray();
        for(Pick pick : shipmentLine.getPicks()) {
            if (pick.getPickState() != PickState.CANCELLED) {
                jgen.writeStartObject();
                jgen.writeNumberField("id", pick.getId());
                jgen.writeStringField("number", pick.getNumber());
                jgen.writeStringField("pickState", pick.getPickState().name());
                jgen.writeNumberField("quantity", pick.getQuantity());
                jgen.writeNumberField("pickedQuantity", pick.getPickedQuantity());

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



        jgen.writeFieldName("shipment");
        jgen.writeStartObject();
        if (shipmentLine.getShipment() != null) {
            jgen.writeNumberField("id", shipmentLine.getShipment().getId());
            jgen.writeStringField("number", shipmentLine.getShipment().getNumber());


            jgen.writeFieldName("customer");
            jgen.writeStartObject();
            if (shipmentLine.getShipment().getCustomer() != null) {
                jgen.writeNumberField("id", shipmentLine.getShipment().getCustomer().getId());
                jgen.writeStringField("name", shipmentLine.getShipment().getCustomer().getName());
                jgen.writeStringField("description", shipmentLine.getShipment().getCustomer().getDescription());
                jgen.writeStringField("firstname", shipmentLine.getShipment().getCustomer().getFirstName());
                jgen.writeStringField("lastname", shipmentLine.getShipment().getCustomer().getLastName());

                jgen.writeFieldName("address");
                jgen.writeStartObject();
                if (shipmentLine.getShipment().getCustomer().getAddress() != null) {
                    jgen.writeNumberField("id", shipmentLine.getShipment().getCustomer().getAddress().getId());
                    jgen.writeStringField("name", shipmentLine.getShipment().getCustomer().getAddress().getName());
                    jgen.writeStringField("externalID", shipmentLine.getShipment().getCustomer().getAddress().getExternalID());
                    jgen.writeStringField("firstname", shipmentLine.getShipment().getCustomer().getAddress().getFirstname());
                    jgen.writeStringField("lastname", shipmentLine.getShipment().getCustomer().getAddress().getLastname());

                    jgen.writeStringField("state", shipmentLine.getShipment().getCustomer().getAddress().getState());
                    jgen.writeStringField("county", shipmentLine.getShipment().getCustomer().getAddress().getCounty());
                    jgen.writeStringField("city", shipmentLine.getShipment().getCustomer().getAddress().getCity());
                    jgen.writeStringField("addressLine1", shipmentLine.getShipment().getCustomer().getAddress().getAddressLine1());
                    jgen.writeStringField("addressLine2", shipmentLine.getShipment().getCustomer().getAddress().getAddressLine2());
                    jgen.writeStringField("postcode", shipmentLine.getShipment().getCustomer().getAddress().getPostcode());

                }
                jgen.writeEndObject();
            }
            jgen.writeEndObject();


            jgen.writeFieldName("warehouse");
            jgen.writeStartObject();
            if (shipmentLine.getShipment().getWarehouse() != null) {
                jgen.writeNumberField("id", shipmentLine.getShipment().getWarehouse().getId());
                jgen.writeStringField("name", shipmentLine.getShipment().getWarehouse().getName());

            }
            jgen.writeEndObject();
        }


        jgen.writeEndObject();
    }
}