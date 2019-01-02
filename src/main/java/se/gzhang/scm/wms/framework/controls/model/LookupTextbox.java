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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.persistence.*;
@Entity
@Table(name="lookup_textbox")
@JsonSerialize(using = LookupTextBoxSerializer.class)
public class LookupTextbox {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;


    @Column(name = "variable")
    private String variable;

    @Column(name = "command")
    private String command;

    @Column(name = "enumClass")
    private String enumClass;

    @Column(name = "returnColumn")
    private String returnColumn;


    @Transient
    private SqlRowSet resultSet;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(String enumClass) {
        this.enumClass = enumClass;
    }

    public String getReturnColumn() {
        return returnColumn;
    }

    public void setReturnColumn(String returnColumn) {
        this.returnColumn = returnColumn;
    }

    public SqlRowSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(SqlRowSet resultSet) {
        this.resultSet = resultSet;
    }
}
