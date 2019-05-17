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

public class CartonTypeSerializer extends StdSerializer<CartonType> {

    public CartonTypeSerializer() {
        this(null);
    }

    public CartonTypeSerializer(Class<CartonType> cartonType) {
        super(cartonType);
    }

    @Override
    public void serialize(
            CartonType cartonType, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeNumberField("id", cartonType.getId());
        jgen.writeStringField("name", cartonType.getName());
        jgen.writeStringField("description", cartonType.getDescription());
        jgen.writeNumberField("cost", cartonType.getCost());
        jgen.writeBooleanField("enabled", cartonType.getEnabled());
        jgen.writeNumberField("length", cartonType.getLength());
        jgen.writeNumberField("width", cartonType.getWidth());
        jgen.writeNumberField("height", cartonType.getHeight());
        jgen.writeNumberField("fillRate", cartonType.getFillRate());
        jgen.writeNumberField("weight", cartonType.getWeight());
        jgen.writeNumberField("weightCapacity", cartonType.getWeightCapacity());

        jgen.writeEndObject();
    }
}