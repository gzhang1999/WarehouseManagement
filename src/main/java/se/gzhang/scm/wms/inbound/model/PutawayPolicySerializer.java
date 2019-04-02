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

public class PutawayPolicySerializer extends StdSerializer<PutawayPolicy> {

        public PutawayPolicySerializer() {
            this(null);
        }

        public PutawayPolicySerializer(Class<PutawayPolicy> putawayPolicy) {
            super(putawayPolicy);
        }

        @Override
        public void serialize(
                PutawayPolicy putawayPolicy, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", putawayPolicy.getId());
        jgen.writeNumberField("sequence", putawayPolicy.getSequence());

        // list all the criteria
        jgen.writeFieldName("item");
        jgen.writeStartObject();
        if(putawayPolicy.getItem() != null) {
            jgen.writeNumberField("id", putawayPolicy.getItem().getId());
            jgen.writeStringField("name", putawayPolicy.getItem().getName());
            jgen.writeStringField("description", putawayPolicy.getItem().getDescription());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("itemFamily");
        jgen.writeStartObject();
        if(putawayPolicy.getItemFamily() != null) {
            jgen.writeNumberField("id", putawayPolicy.getItemFamily().getId());
            jgen.writeStringField("name", putawayPolicy.getItemFamily().getName());
            jgen.writeStringField("description", putawayPolicy.getItemFamily().getDescription());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("supplier");
        jgen.writeStartObject();
        if(putawayPolicy.getSupplier() != null) {
            jgen.writeNumberField("id", putawayPolicy.getSupplier().getId());
            jgen.writeStringField("name", putawayPolicy.getSupplier().getName());
            jgen.writeStringField("description", putawayPolicy.getSupplier().getDescription());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("trailer");
        jgen.writeStartObject();
        if(putawayPolicy.getTrailer() != null) {
            jgen.writeNumberField("id", putawayPolicy.getTrailer().getId());
            jgen.writeStringField("trailerNumber", putawayPolicy.getTrailer().getTrailerNumber());
            jgen.writeStringField("licensePlate", putawayPolicy.getTrailer().getLicensePlate());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("receipt");
        jgen.writeStartObject();
        if(putawayPolicy.getReceipt() != null) {
            jgen.writeNumberField("id", putawayPolicy.getReceipt().getId());
            jgen.writeStringField("number", putawayPolicy.getReceipt().getNumber());
            jgen.writeStringField("externalID", putawayPolicy.getReceipt().getExternalID());
        }
        jgen.writeEndObject();


        jgen.writeNumberField("minimumSize", (putawayPolicy.getMinimumSize() == null ? 0 : putawayPolicy.getMinimumSize()));
        jgen.writeNumberField("maximumSize", (putawayPolicy.getMaximumSize() == null ? 0 : putawayPolicy.getMaximumSize()));
        jgen.writeNumberField("minimumWeight", (putawayPolicy.getMinimumWeight() == null ? 0 : putawayPolicy.getMinimumWeight()));
        jgen.writeNumberField("maximumWeight", (putawayPolicy.getMaximumWeight() == null ? 0 : putawayPolicy.getMaximumWeight()));

        jgen.writeFieldName("area");
        jgen.writeStartObject();
        if(putawayPolicy.getArea() != null) {
            jgen.writeNumberField("id", putawayPolicy.getArea().getId());
            jgen.writeStringField("name", putawayPolicy.getArea().getName());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("areaGroup");
        jgen.writeStartObject();
        if(putawayPolicy.getAreaGroup() != null) {
            jgen.writeNumberField("id", putawayPolicy.getAreaGroup().getId());
            jgen.writeStringField("name", putawayPolicy.getAreaGroup().getName());
            jgen.writeStringField("description", putawayPolicy.getAreaGroup().getDescription());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("location");
        jgen.writeStartObject();
        if(putawayPolicy.getLocation() != null) {
            jgen.writeNumberField("id", putawayPolicy.getLocation().getId());
            jgen.writeStringField("name", putawayPolicy.getLocation().getName());
        }
        jgen.writeEndObject();

        jgen.writeFieldName("locationGroup");
        jgen.writeStartObject();
        if(putawayPolicy.getLocationGroup() != null) {
            jgen.writeNumberField("id", putawayPolicy.getLocationGroup().getId());
            jgen.writeStringField("name", putawayPolicy.getLocationGroup().getName());
            jgen.writeStringField("description", putawayPolicy.getLocationGroup().getDescription());
        }
        jgen.writeEndObject();


        jgen.writeStringField("locationLevel", (putawayPolicy.getLocationLevel() == null ? "" : putawayPolicy.getLocationLevel()));
        jgen.writeStringField("locationAisle", (putawayPolicy.getLocationAisleID() == null ? "" : putawayPolicy.getLocationAisleID()));

        jgen.writeEndObject();
    }
}
