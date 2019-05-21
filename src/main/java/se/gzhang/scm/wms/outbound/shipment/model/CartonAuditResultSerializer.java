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

public class CartonAuditResultSerializer extends StdSerializer<CartonAuditResult> {

    public CartonAuditResultSerializer() {
        this(null);
    }

    public CartonAuditResultSerializer(Class<CartonAuditResult> cartonAuditResult) {
        super(cartonAuditResult);
    }

    @Override
    public void serialize(
            CartonAuditResult cartonAuditResult, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeNumberField("id", cartonAuditResult.getId());

        jgen.writeStringField("auditState", cartonAuditResult.getCartonAuditState().name());
        jgen.writeStringField("auditDate", cartonAuditResult.getAuditDate() == null ? "" : cartonAuditResult.getAuditDate().toString());
        jgen.writeStringField("auditUser", cartonAuditResult.getAuditDate() == null ? "" : cartonAuditResult.getAuditUser().getUsername());

        jgen.writeFieldName("carton");
        jgen.writeStartObject();
        if (cartonAuditResult.getCarton() != null) {
            jgen.writeNumberField("id", cartonAuditResult.getCarton().getId());
            jgen.writeStringField("number", cartonAuditResult.getCarton().getNumber());

            jgen.writeFieldName("type");
            jgen.writeStartObject();
            if (cartonAuditResult.getCarton().getCartonType() != null) {
                jgen.writeNumberField("id", cartonAuditResult.getCarton().getCartonType().getId());
                jgen.writeStringField("name", cartonAuditResult.getCarton().getCartonType().getName());
                jgen.writeNumberField("length", cartonAuditResult.getCarton().getCartonType().getLength());
                jgen.writeNumberField("width", cartonAuditResult.getCarton().getCartonType().getWidth());
                jgen.writeNumberField("height", cartonAuditResult.getCarton().getCartonType().getHeight());
                jgen.writeNumberField("fillRate", cartonAuditResult.getCarton().getCartonType().getFillRate());
            }
            jgen.writeEndObject();
        }
        jgen.writeEndObject();


        jgen.writeFieldName("cartonAuditLines");
        jgen.writeStartArray();
        if (cartonAuditResult.getCartonAuditResultLines() != null) {
            for (CartonAuditResultLine cartonAuditResultLine : cartonAuditResult.getCartonAuditResultLines() ) {
                jgen.writeStartObject();
                jgen.writeNumberField("id", cartonAuditResultLine.getId());
                jgen.writeNumberField("expectedQuantity", cartonAuditResultLine.getExpectedQuantity());
                jgen.writeNumberField("auditQuantity", cartonAuditResultLine.getAuditQuantity());

                jgen.writeFieldName("item");
                jgen.writeStartObject();
                if (cartonAuditResultLine.getItem() != null) {
                    jgen.writeNumberField("id", cartonAuditResultLine.getItem().getId());
                    jgen.writeStringField("name", cartonAuditResultLine.getItem().getName());
                    jgen.writeStringField("description", cartonAuditResultLine.getItem().getDescription());
                }
                jgen.writeEndObject();

                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();


        jgen.writeEndObject();
    }
}