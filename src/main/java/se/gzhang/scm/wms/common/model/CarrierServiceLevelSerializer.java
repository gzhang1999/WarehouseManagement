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

package se.gzhang.scm.wms.common.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CarrierServiceLevelSerializer extends StdSerializer<CarrierServiceLevel> {

        public CarrierServiceLevelSerializer() {
            this(null);
        }

        public CarrierServiceLevelSerializer(Class<CarrierServiceLevel> carrierServiceLevel) {
            super(carrierServiceLevel);

        }

        @Override
        public void serialize(
                CarrierServiceLevel carrierServiceLevel, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", carrierServiceLevel.getId());
        jgen.writeStringField("name", carrierServiceLevel.getName());
        jgen.writeStringField("description", carrierServiceLevel.getDescription());

        // write array for pickable uom
        jgen.writeFieldName("carrier");
        jgen.writeStartObject();

        jgen.writeNumberField("id", carrierServiceLevel.getCarrier().getId());
        jgen.writeStringField("name", carrierServiceLevel.getCarrier().getName());
        jgen.writeStringField("description", carrierServiceLevel.getCarrier().getDescription());

        jgen.writeEndObject();


        jgen.writeEndObject();
    }
}
