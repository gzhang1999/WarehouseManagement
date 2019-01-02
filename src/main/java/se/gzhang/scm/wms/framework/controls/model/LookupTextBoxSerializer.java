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

package se.gzhang.scm.wms.framework.controls.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.io.IOException;
import java.sql.Types;

public class LookupTextBoxSerializer extends StdSerializer<LookupTextbox> {
    public LookupTextBoxSerializer() {
        this(null);
    }

    public LookupTextBoxSerializer(Class<LookupTextbox> lookupTextbox) {
        super(lookupTextbox);
    }

    @Override
    public void serialize(
            LookupTextbox lookupTextbox, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", lookupTextbox.getId());
        jgen.writeStringField("variable", lookupTextbox.getVariable());
        jgen.writeStringField("returnColumn", lookupTextbox.getReturnColumn());

        jgen.writeFieldName("resultSet");
        jgen.writeStartObject();

        SqlRowSet resultSet = lookupTextbox.getResultSet();
        // move the cursor to the first record

        resultSet.beforeFirst();

        // Use isBeforeFirst() to check whether this result set has any
        // records
        if (resultSet.isBeforeFirst() ) {
                SqlRowSetMetaData rsmd = resultSet.getMetaData();
                int numColumns = rsmd.getColumnCount();
                String[] columnNames = new String[numColumns];
                int[] columnTypes = new int[numColumns];

                // write the meta data to the return JSON
                jgen.writeFieldName("columns");
                jgen.writeStartArray();

                for (int i = 0; i < columnNames.length; i++) {

                    columnNames[i] = rsmd.getColumnLabel(i + 1);
                    columnTypes[i] = rsmd.getColumnType(i + 1);
                    jgen.writeStartObject();
                    jgen.writeStringField("columnName", columnNames[i]);
                    jgen.writeStringField("columnType", rsmd.getColumnTypeName(i+1));
                    jgen.writeEndObject();

                }
                jgen.writeEndArray();

                jgen.writeFieldName("data");
                jgen.writeStartArray();

                while (resultSet.next()) {

                    boolean b;
                    long l;
                    double d;

                    jgen.writeStartObject();

                    for (int i = 0; i < columnNames.length; i++) {

                        jgen.writeFieldName(columnNames[i]);
                        switch (columnTypes[i]) {

                            case Types.INTEGER:
                                l = resultSet.getInt(i + 1);
                                if (resultSet.wasNull()) {
                                    jgen.writeNull();
                                } else {
                                    jgen.writeNumber(l);
                                }
                                break;

                            case Types.BIGINT:
                                l = resultSet.getLong(i + 1);
                                if (resultSet.wasNull()) {
                                    jgen.writeNull();
                                } else {
                                    jgen.writeNumber(l);
                                }
                                break;

                            case Types.DECIMAL:
                            case Types.NUMERIC:
                                jgen.writeNumber(resultSet.getBigDecimal(i + 1));
                                break;

                            case Types.FLOAT:
                            case Types.REAL:
                            case Types.DOUBLE:
                                d = resultSet.getDouble(i + 1);
                                if (resultSet.wasNull()) {
                                    jgen.writeNull();
                                } else {
                                    jgen.writeNumber(d);
                                }
                                break;

                            case Types.NVARCHAR:
                            case Types.VARCHAR:
                            case Types.LONGNVARCHAR:
                            case Types.LONGVARCHAR:
                                jgen.writeString(resultSet.getString(i + 1));
                                break;

                            case Types.BOOLEAN:
                            case Types.BIT:
                                b = resultSet.getBoolean(i + 1);
                                if (resultSet.wasNull()) {
                                    jgen.writeNull();
                                } else {
                                    jgen.writeBoolean(b);
                                }
                                break;

                            case Types.BINARY:
                            case Types.VARBINARY:
                            case Types.LONGVARBINARY:
                                throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type BINARY");

                            case Types.TINYINT:
                            case Types.SMALLINT:
                                l = resultSet.getShort(i + 1);
                                if (resultSet.wasNull()) {
                                    jgen.writeNull();
                                } else {
                                    jgen.writeNumber(l);
                                }
                                break;

                            case Types.DATE:
                                if (resultSet.getDate(i + 1) == null) {
                                    jgen.writeNull();
                                }
                                else {
                                    provider.defaultSerializeDateValue(resultSet.getDate(i + 1), jgen);
                                }
                                break;

                            case Types.TIMESTAMP:
                                if (resultSet.getTime(i + 1) == null) {
                                    jgen.writeNull();
                                }
                                else {
                                    provider.defaultSerializeDateValue(resultSet.getTime(i + 1), jgen);
                                }
                                break;

                            case Types.BLOB:
                                throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type BLOB");

                            case Types.CLOB:
                                throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type CLOB");

                            case Types.ARRAY:
                                throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type ARRAY");

                            case Types.STRUCT:
                                throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type STRUCT");

                            case Types.DISTINCT:
                                throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type DISTINCT");

                            case Types.REF:
                                throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type REF");

                            case Types.JAVA_OBJECT:
                            default:
                                provider.defaultSerializeValue(resultSet.getObject(i + 1), jgen);
                                break;
                        }
                    }

                    jgen.writeEndObject();
                }

                jgen.writeEndArray();

            // Reset the cursor
            resultSet.beforeFirst();
        }
        jgen.writeEndObject();
        jgen.writeEndObject();
    }
}
