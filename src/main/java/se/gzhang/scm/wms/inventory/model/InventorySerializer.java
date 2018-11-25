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

package se.gzhang.scm.wms.inventory.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import se.gzhang.scm.wms.common.model.UnitOfMeasure;

import java.io.IOException;
import java.util.List;

public class InventorySerializer extends StdSerializer<Inventory> {

    public InventorySerializer() {
        this(null);
    }

    public InventorySerializer(Class<Inventory> inventory) {
        super(inventory);
    }

    @Override
    public void serialize(
            Inventory inventory, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
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
