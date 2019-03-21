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

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="dropdownList")
// We will use this table for
// 1. Dropdown list with options
// 2. Auto complete(It will show only the option's value)
public class DropdownList {
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

    @Column(name = "allow_blank")
    private boolean allowBlankRowFlag;


    @OneToMany(
            mappedBy = "dropdownList",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DropdownOption> dropdownOptions;

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

    public boolean isAllowBlankRowFlag() {
        return allowBlankRowFlag;
    }

    public void setAllowBlankRowFlag(boolean allowBlankRowFlag) {
        this.allowBlankRowFlag = allowBlankRowFlag;
    }

    public List<DropdownOption> getDropdownOptions() {
        return dropdownOptions;
    }

    public void setDropdownOptions(List<DropdownOption> dropdownOptions) {
        this.dropdownOptions = dropdownOptions;
    }

}
