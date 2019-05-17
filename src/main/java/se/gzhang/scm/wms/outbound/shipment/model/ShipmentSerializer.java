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

public class ShipmentSerializer extends StdSerializer<Shipment> {

    public ShipmentSerializer() {
        this(null);
    }

    public ShipmentSerializer(Class<Shipment> shipment) {
        super(shipment);
    }

    @Override
    public void serialize(
            Shipment shipment, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", shipment.getId());
        jgen.writeStringField("number", shipment.getNumber());
        jgen.writeStringField("shipmentState", shipment.getShipmentState().name());


        jgen.writeFieldName("customer");
        jgen.writeStartObject();
        if (shipment.getCustomer() != null) {
            jgen.writeNumberField("id", shipment.getCustomer().getId());
            jgen.writeStringField("name", shipment.getCustomer().getName());
            jgen.writeStringField("description", shipment.getCustomer().getDescription());
            jgen.writeStringField("firstname", shipment.getCustomer().getFirstName());
            jgen.writeStringField("lastname", shipment.getCustomer().getLastName());

            jgen.writeFieldName("address");
            jgen.writeStartObject();
            if (shipment.getCustomer().getAddress() != null) {
                jgen.writeNumberField("id", shipment.getCustomer().getAddress().getId());
                jgen.writeStringField("name", shipment.getCustomer().getAddress().getName());
                jgen.writeStringField("externalID", shipment.getCustomer().getAddress().getExternalID());
                jgen.writeStringField("firstname", shipment.getCustomer().getAddress().getFirstname());
                jgen.writeStringField("lastname", shipment.getCustomer().getAddress().getLastname());

                jgen.writeStringField("state", shipment.getCustomer().getAddress().getState());
                jgen.writeStringField("county", shipment.getCustomer().getAddress().getCounty());
                jgen.writeStringField("city", shipment.getCustomer().getAddress().getCity());
                jgen.writeStringField("addressLine1", shipment.getCustomer().getAddress().getAddressLine1());
                jgen.writeStringField("addressLine2", shipment.getCustomer().getAddress().getAddressLine2());
                jgen.writeStringField("postcode", shipment.getCustomer().getAddress().getPostcode());

            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();


        jgen.writeFieldName("warehouse");
        jgen.writeStartObject();
        if (shipment.getWarehouse() != null) {
            jgen.writeNumberField("id", shipment.getWarehouse().getId());
            jgen.writeStringField("name", shipment.getWarehouse().getName());

        }
        jgen.writeEndObject();

        jgen.writeFieldName("shipmentLines");
        jgen.writeStartArray();
        if (shipment.getShipmentLines() != null) {
            for (ShipmentLine shipmentLine : shipment.getShipmentLines()) {
                if (shipmentLine.getShipmentLineState().equals(ShipmentLineState.CANCELLED)) {
                    continue;
                }
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

                    jgen.writeFieldName("salesOrder");
                    jgen.writeStartObject();
                    if (shipmentLine.getSalesOrderLine().getSalesOrder() != null) {
                        jgen.writeNumberField("id", shipmentLine.getSalesOrderLine().getSalesOrder().getId());
                        jgen.writeStringField("externalID", shipmentLine.getSalesOrderLine().getSalesOrder().getExternalID());
                        jgen.writeStringField("number", shipmentLine.getSalesOrderLine().getSalesOrder().getNumber());
                        jgen.writeStringField("shippingMethod",
                                (shipmentLine.getSalesOrderLine().getSalesOrder().getShippingMethod() == null ?
                                        "" : shipmentLine.getSalesOrderLine().getSalesOrder().getShippingMethod().name()));
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

                        jgen.writeFieldName("carton");
                        jgen.writeStartObject();
                        if (pick.getCarton() != null) {
                            jgen.writeNumberField("id", pick.getCarton().getId());
                            jgen.writeStringField("number", pick.getCarton().getNumber());
                            jgen.writeFieldName("type");
                            jgen.writeStartObject();
                            if (pick.getCarton().getCartonType() != null) {
                                jgen.writeNumberField("id", pick.getCarton().getCartonType().getId());
                                jgen.writeStringField("name", pick.getCarton().getCartonType().getName());
                                jgen.writeNumberField("length", pick.getCarton().getCartonType().getLength());
                                jgen.writeNumberField("width", pick.getCarton().getCartonType().getWidth());
                                jgen.writeNumberField("height", pick.getCarton().getCartonType().getHeight());
                                jgen.writeNumberField("fillRate", pick.getCarton().getCartonType().getFillRate());
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
                    // only add short allocation that is not cancelled
                    if (shortAllocation.getShortAllocationState() != ShortAllocationState.CANCELLED) {
                        jgen.writeStartObject();
                        jgen.writeNumberField("id", shortAllocation.getId());
                        jgen.writeStringField("number", shortAllocation.getNumber());
                        jgen.writeNumberField("quantity", shortAllocation.getQuantity());
                        jgen.writeStringField("state", shortAllocation.getShortAllocationState().name());

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