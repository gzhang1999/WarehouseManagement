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

import lombok.Data;

import javax.persistence.*;
import javax.validation.Constraint;
import java.io.Serializable;

@Data
@Entity
@Table(name = "address")
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "address_id")
    private int id;

    @Column(name = "name")
    private String name;

    // A universal ID that can be used
    // all across any host system.
    // Need to be unique
    @Column(name = "universal_id", unique = true)
    private String universalID;

    // U.S / Chinese type of address

    // State or 省
    @Column(name = "state")
    private String state;

    // 市
    @Column(name = "county")
    private String county;

    // city or 区/县
    @Column(name = "city")
    private String city;

    // Address details
    @Column(name = "address_line_1")
    private String addressLine1;

    // Address details / Apartment #, etc
    @Column(name = "address_line_2")
    private String addressLine2;

    // 街道
    @Column(name = "village")
    private String village;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "homePhone")
    private String homePhone;

    @Column(name = "isResident")
    private boolean isResident;

    @Column(name = "comment")
    private String comment;


}
