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

package se.gzhang.scm.wms.reporting.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import se.gzhang.scm.wms.authorization.model.User;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ReportArchiveSerializer extends StdSerializer<ReportArchive> {

        public ReportArchiveSerializer() {
            this(null);
        }

        public ReportArchiveSerializer(Class<ReportArchive> reportArchive) {
            super(reportArchive);
        }

        @Override
        public void serialize(
                ReportArchive reportArchive, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            jgen.writeStartObject();
            jgen.writeNumberField("id", reportArchive.getId());

            jgen.writeFieldName("report");
            jgen.writeStartObject();
            if (reportArchive.getReport() != null) {

                Report report = reportArchive.getReport();
                jgen.writeNumberField("id", report.getId());
                jgen.writeStringField("name", report.getName());
                jgen.writeStringField("description", report.getDescription());
                jgen.writeStringField("layoutFile", report.getLayoutFile());
                jgen.writeNumberField("saveDays", report.getSaveDays());
                jgen.writeStringField("reportType", report.getReportType().getDescription());
            }
            jgen.writeEndObject();

            jgen.writeFieldName("printedUser");
            jgen.writeStartObject();
            if (reportArchive.getPrintedUser() != null) {
                User user = reportArchive.getPrintedUser();
                jgen.writeNumberField("id", user.getId());
                jgen.writeStringField("username", user.getUsername());
                jgen.writeStringField("email", user.getEmail());
                jgen.writeStringField("firstname", user.getFirstname());
                jgen.writeStringField("lastname", user.getLastname());
            }
            jgen.writeEndObject();

            jgen.writeStringField("printedDate", reportArchive.getPrintedDate() == null ? "" : dateFormat.format(reportArchive.getPrintedDate()));
            // jgen.writeStringField("filePath", reportArchive.getFilePath());

            jgen.writeStringField("key1", reportArchive.getKey1());
            jgen.writeStringField("key2", reportArchive.getKey2());
            jgen.writeStringField("key3", reportArchive.getKey3());
            jgen.writeStringField("key4", reportArchive.getKey4());
            jgen.writeStringField("key5", reportArchive.getKey5());

            jgen.writeEndObject();
        }
}
