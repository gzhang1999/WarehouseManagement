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

package se.gzhang.scm.wms.layout.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import se.gzhang.scm.wms.common.model.UnitOfMeasure;

import java.io.IOException;

public class AreaSerializer  extends StdSerializer<Area> {

    public AreaSerializer() {
        this(null);
    }

    public AreaSerializer(Class<Area> area) {
        super(area);
    }

    @Override
    public void serialize(
            Area area, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", area.getId());
        jgen.writeStringField("name", area.getName());

        // write array for pickable uom
        jgen.writeFieldName("pickableUOMs");
        jgen.writeStartArray();
        for (UnitOfMeasure pickableUOM: area.getPickableUOMs()) {
            jgen.writeString(pickableUOM.getName());
        }
        jgen.writeEndArray();

        jgen.writeStringField("areaType", area.getAreaType().toString());
        jgen.writeStringField("volumeType", area.getVolumeType().toString());

        jgen.writeNumberField("buildingID", area.getBuilding().getId());
        jgen.writeStringField("building", area.getBuilding().getName());
        jgen.writeNumberField("warehouseID", area.getBuilding().getWarehouse().getId());
        jgen.writeStringField("warehouse", area.getBuilding().getWarehouse().getName());

        jgen.writeEndObject();
    }
}
