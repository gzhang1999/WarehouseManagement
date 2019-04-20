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
@Table(name="universal_identifier")
// Get a universal identifier for the variable(For example, L000000125 for LPN)
// it will be in the format of
// Prefix + Number + Postfix, which Number is a sequential number with a fixed
// predefined length. If current number's length is less than the predefined length
// value, padding 0 in the front
public class UniversalIdentifier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "variable", unique = true)
    private String variable;

    @Column(name = "prefix", nullable = false)
    private String Prefix;

    @Column(name = "postfix", nullable = false)
    private String postfix;

    @Column(name = "length", nullable = false)
    private Integer length;

    @Column(name = "current_number", nullable = false)
    private Integer currentNumber;

    // When the number reaches the maximum
    // allowed based on the length, do we want
    // to start from 0 again?
    @Column(name = "rollover", nullable = false)
    private Boolean rollover;

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

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String prefix) {
        Prefix = prefix;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Integer currentNumber) {
        this.currentNumber = currentNumber;
    }

    public Boolean getRollover() {
        return rollover;
    }

    public void setRollover(Boolean rollover) {
        this.rollover = rollover;
    }
}
