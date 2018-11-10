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
import se.gzhang.scm.wms.common.model.VehicleType;

import java.io.IOException;

public class LocationSerializer extends StdSerializer<Location> {

    public LocationSerializer() {
        this(null);
    }

    public LocationSerializer(Class<Location> location) {
        super(location);
    }

    @Override
    public void serialize(
            Location location, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", location.getId());
        jgen.writeStringField("name", location.getName());
        jgen.writeStringField("area", location.getArea().getName());
        jgen.writeNumberField("areaID", location.getArea().getId());
        jgen.writeStringField("building", location.getArea().getName());
        jgen.writeNumberField("buildingID", location.getArea().getId());
        jgen.writeStringField("aisleID", location.getAisleID());

        jgen.writeBooleanField("pickable", location.isPickable());
        jgen.writeBooleanField("storable", location.isStorable());
        jgen.writeBooleanField("usable", location.isUsable());

        jgen.writeNumberField("length", location.getLength());
        jgen.writeNumberField("width", location.getWidth());
        jgen.writeNumberField("height", location.getHeight());
        jgen.writeNumberField("volume", location.getVolume());

        jgen.writeStringField("velocity", location.getVelocity().getName());
        jgen.writeNumberField("velocityID", location.getVelocity().getId());


        jgen.writeFieldName("vehicleTypes");
        jgen.writeStartArray();
        /*****
         * We will not write location's vehicle type here to the JSON file as
         * this will cause lots of database query, one for each location. It will
         * cause performance issue when we need to return lots of locations.
         ****/
        if (location.getAccessibleVehicleTypes() != null) {
            for (VehicleType vehicleType : location.getAccessibleVehicleTypes()) {
                jgen.writeString(vehicleType.getName());
            }
        }
        jgen.writeEndArray();

        jgen.writeNumberField("coordinateX", location.getCoordinateX());
        jgen.writeNumberField("coordinateY", location.getCoordinateY());
        jgen.writeNumberField("coordinateZ", location.getCoordinateZ());

        jgen.writeEndObject();
    }
}