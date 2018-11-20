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

import java.io.IOException;

public class ItemFootprintUOMSerializer extends StdSerializer<ItemFootprintUOM> {

    public ItemFootprintUOMSerializer() {
        this(null);
    }

    public ItemFootprintUOMSerializer(Class<ItemFootprintUOM> itemFootprintUOM) {
        super(itemFootprintUOM);
    }

    @Override
    public void serialize(
            ItemFootprintUOM itemFootprintUOM, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", itemFootprintUOM.getId());
        jgen.writeFieldName("unitOfMeasure");
        jgen.writeStartObject();
        if (itemFootprintUOM.getUnitOfMeasure() != null) {
            jgen.writeStringField("name", itemFootprintUOM.getUnitOfMeasure().getName());
            jgen.writeStringField("description", itemFootprintUOM.getUnitOfMeasure().getDescription());
        }
        jgen.writeEndObject();
        jgen.writeNumberField("quantity", itemFootprintUOM.getQuantity());
        jgen.writeNumberField("length", itemFootprintUOM.getLength());
        jgen.writeNumberField("width", itemFootprintUOM.getWidth());
        jgen.writeNumberField("height", itemFootprintUOM.getHeight());
        jgen.writeNumberField("weight", itemFootprintUOM.getWeight());


        jgen.writeBooleanField("stockUOM", itemFootprintUOM.isStockUOM());
        jgen.writeBooleanField("caseUOM", itemFootprintUOM.isCaseUOM());
        jgen.writeBooleanField("palletUOM", itemFootprintUOM.isPalletUOM());
        jgen.writeBooleanField("cartonUOM", itemFootprintUOM.isCartonUOM());


        // Write item and footprint object
        jgen.writeFieldName("footprint");
        jgen.writeStartObject();
        if (itemFootprintUOM.getItemFootprint() != null) {
            jgen.writeNumberField("id", itemFootprintUOM.getItemFootprint().getId());
            jgen.writeStringField("name", itemFootprintUOM.getItemFootprint().getName());
            jgen.writeStringField("description", itemFootprintUOM.getItemFootprint().getDescription());
            jgen.writeFieldName("item");
            jgen.writeStartObject();
            if (itemFootprintUOM.getItemFootprint().getItem() != null) {
                jgen.writeNumberField("id", itemFootprintUOM.getItemFootprint().getItem().getId());
                jgen.writeStringField("name", itemFootprintUOM.getItemFootprint().getItem().getName());
                jgen.writeStringField("description", itemFootprintUOM.getItemFootprint().getItem().getDescription());
                jgen.writeFieldName("warehouse");
                jgen.writeStartObject();
                if (itemFootprintUOM.getItemFootprint().getItem().getWarehouse() != null) {
                    jgen.writeNumberField("id", itemFootprintUOM.getItemFootprint().getItem().getWarehouse().getId());
                    jgen.writeStringField("name", itemFootprintUOM.getItemFootprint().getItem().getWarehouse().getName());
                }
                jgen.writeEndObject();
                jgen.writeFieldName("client");
                jgen.writeStartObject();
                if (itemFootprintUOM.getItemFootprint().getItem().getClient() != null) {
                    jgen.writeNumberField("id", itemFootprintUOM.getItemFootprint().getItem().getClient().getId());
                    jgen.writeStringField("name", itemFootprintUOM.getItemFootprint().getItem().getClient().getName());
                }
                jgen.writeEndObject();
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();

        jgen.writeEndObject();
    }
}
