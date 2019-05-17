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

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carton")
public class Carton {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carton_id")
    private Integer id;

    @Column(name = "number")
    private String number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="carton_type_id")
    private CartonType cartonType;

    @OneToMany(
            mappedBy = "carton",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Pick> pickList = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public CartonType getCartonType() {
        return cartonType;
    }

    public void setCartonType(CartonType cartonType) {
        this.cartonType = cartonType;
    }

    public List<Pick> getPickList() {
        return pickList;
    }

    public void setPickList(List<Pick> pickList) {
        this.pickList = pickList;
    }
}
