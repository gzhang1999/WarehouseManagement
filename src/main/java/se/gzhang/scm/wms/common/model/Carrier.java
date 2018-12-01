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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "carrier")
@JsonSerialize(using = CarrierSerializer.class)
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "carrier_id")
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_person_telephone")
    private String contactPersonTelephone;


    @OneToMany(
            mappedBy = "carrier",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CarrierServiceLevel> carrierServiceLevels = new ArrayList<>();


}
