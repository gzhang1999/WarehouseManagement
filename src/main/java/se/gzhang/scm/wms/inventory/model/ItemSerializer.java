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
import se.gzhang.scm.wms.layout.model.Area;

import java.io.IOException;

public class ItemSerializer extends StdSerializer<Item> {

    public ItemSerializer() {
        this(null);
    }

    public ItemSerializer(Class<Item> item) {
        super(item);
    }

    @Override
    public void serialize(
            Item item, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", item.getId());
        jgen.writeStringField("name", item.getName());
        jgen.writeStringField("description", item.getDescription());

        // Write warehouse object
        jgen.writeFieldName("warehouse");
        jgen.writeStartObject();
        jgen.writeNumberField("id", item.getWarehouse().getId());
        jgen.writeStringField("name", item.getWarehouse().getName());
        jgen.writeEndObject();

        // Write client object
        jgen.writeFieldName("client");
        jgen.writeStartObject();
        jgen.writeNumberField("id", item.getClient().getId());
        jgen.writeStringField("name", item.getClient().getName());
        jgen.writeEndObject();

        // Write footprints
        jgen.writeFieldName("footprints");
        jgen.writeStartArray();
        if (item.getItemFootprints() != null){
            for (ItemFootprint itemFootprint : item.getItemFootprints()) {

                // We will write the stock UOM / case UOM / pallet UOM
                // as 3 specific UOM at the footprint most outer level
                // as those 3 UOMs are important to display and calculate
                // the size and etc.
                ItemFootprintUOM stockUOM = null;
                ItemFootprintUOM caseUOM = null;
                ItemFootprintUOM palletUOM = null;

                jgen.writeStartObject();
                jgen.writeNumberField("id", itemFootprint.getId());
                jgen.writeStringField("name", itemFootprint.getName());
                jgen.writeStringField("description", itemFootprint.getDescription());
                jgen.writeBooleanField("default", itemFootprint.isDefaultFootprint());
                jgen.writeFieldName("footprintUOMs");
                jgen.writeStartArray();
                if (itemFootprint.getItemFootprintUOMs() != null) {
                    for (ItemFootprintUOM itemFootprintUOM : itemFootprint.getItemFootprintUOMs()) {
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

                        jgen.writeBooleanField("stockUOM", itemFootprintUOM.isStockUOM());
                        jgen.writeBooleanField("caseUOM", itemFootprintUOM.isCaseUOM());
                        jgen.writeBooleanField("palletUOM", itemFootprintUOM.isPalletUOM());
                        jgen.writeBooleanField("cartonUOM", itemFootprintUOM.isCartonUOM());

                        jgen.writeEndObject();
                        if (itemFootprintUOM.isStockUOM()) {
                            stockUOM = itemFootprintUOM;
                        }
                        else if (itemFootprintUOM.isCaseUOM()) {
                            caseUOM = itemFootprintUOM;
                        }
                        else if (itemFootprintUOM.isPalletUOM()) {
                            palletUOM = itemFootprintUOM;
                        }
                    }
                }
                jgen.writeEndArray();

                // write stock uom / case uom / pallet uom to the first level
                // of the footprint
                jgen.writeFieldName("stockUnitOfMeasure");
                jgen.writeStartObject();
                if (stockUOM != null) {
                    jgen.writeFieldName("unitOfMeasure");
                    jgen.writeStartObject();
                    if (stockUOM.getUnitOfMeasure() != null) {
                        jgen.writeStringField("name", stockUOM.getUnitOfMeasure().getName());
                        jgen.writeStringField("description", stockUOM.getUnitOfMeasure().getDescription());
                    }
                    jgen.writeEndObject();
                    jgen.writeNumberField("quantity", stockUOM.getQuantity());
                    jgen.writeNumberField("length", stockUOM.getLength());
                    jgen.writeNumberField("width", stockUOM.getWidth());
                    jgen.writeNumberField("height", stockUOM.getHeight());
                    jgen.writeNumberField("weight", stockUOM.getWeight());
                }
                jgen.writeEndObject();

                jgen.writeFieldName("caseUnitOfMeasure");
                jgen.writeStartObject();
                if (caseUOM != null) {
                    jgen.writeFieldName("unitOfMeasure");
                    jgen.writeStartObject();
                    if (caseUOM.getUnitOfMeasure() != null) {
                        jgen.writeStringField("name", caseUOM.getUnitOfMeasure().getName());
                        jgen.writeStringField("description", caseUOM.getUnitOfMeasure().getDescription());
                    }
                    jgen.writeEndObject();
                    jgen.writeNumberField("quantity", caseUOM.getQuantity());
                    jgen.writeNumberField("length", caseUOM.getLength());
                    jgen.writeNumberField("width", caseUOM.getWidth());
                    jgen.writeNumberField("height", caseUOM.getHeight());
                    jgen.writeNumberField("weight", caseUOM.getWeight());
                }
                jgen.writeEndObject();

                jgen.writeFieldName("palletUnitOfMeasure");
                jgen.writeStartObject();
                if (palletUOM != null) {
                    jgen.writeFieldName("unitOfMeasure");
                    jgen.writeStartObject();
                    if (palletUOM.getUnitOfMeasure() != null) {
                        jgen.writeStringField("name", palletUOM.getUnitOfMeasure().getName());
                        jgen.writeStringField("description", palletUOM.getUnitOfMeasure().getDescription());
                    }
                    jgen.writeEndObject();
                    jgen.writeNumberField("quantity", palletUOM.getQuantity());
                    jgen.writeNumberField("length", palletUOM.getLength());
                    jgen.writeNumberField("width", palletUOM.getWidth());
                    jgen.writeNumberField("height", palletUOM.getHeight());
                    jgen.writeNumberField("weight", palletUOM.getWeight());
                }
                jgen.writeEndObject();


                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();


        // Write barcode
        jgen.writeFieldName("barcode");
        jgen.writeStartArray();
        if (item.getItemBarcodes() != null){
            for (ItemBarcode itemBarcode : item.getItemBarcodes()) {
                jgen.writeStartObject();
                jgen.writeNumberField("id", itemBarcode.getId());
                jgen.writeStringField("barcode", itemBarcode.getBarcode());
                jgen.writeStringField("type", itemBarcode.getItemBarcodeType().getName());
                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }
}
