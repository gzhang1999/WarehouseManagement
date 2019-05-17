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

package se.gzhang.scm.wms.outbound.shipment.model;

import se.gzhang.scm.wms.outbound.order.model.SalesOrder;
import se.gzhang.scm.wms.outbound.order.model.SalesOrderLine;
import se.gzhang.scm.wms.outbound.shipment.model.CartonType;

import java.io.Serializable;
import java.util.ArrayList;


public class CartonTypeFlowModel implements Serializable {

    private CartonType cartonType;
    private boolean modify;

    public CartonTypeFlowModel() {
        cartonType = new CartonType();
        modify = false;
    }
    public CartonTypeFlowModel(CartonType cartonType) {

        this.cartonType = cartonType;
        modify = true;
    }

    public boolean isModify(){
        return modify;
    }
    public void setupCartonType(CartonType cartonType) {
        // If we are adding a new carton type, we are allow
        // to setup the name and description.
        if (modify == false) {
            this.cartonType.setName(cartonType.getName());
            this.cartonType.setDescription(cartonType.getDescription());
        }
        this.cartonType.setLength(cartonType.getLength());
        this.cartonType.setWidth(cartonType.getWidth());
        this.cartonType.setHeight(cartonType.getHeight());
        this.cartonType.setWeight(cartonType.getWeight());
        this.cartonType.setWeightCapacity(cartonType.getWeightCapacity());
        this.cartonType.setFillRate(cartonType.getFillRate());
        this.cartonType.setCost(cartonType.getCost());
        this.cartonType.setEnabled(cartonType.getEnabled() == null ? false : cartonType.getEnabled());
        this.cartonType.setWarehouse(cartonType.getWarehouse());

    }
    public CartonType getCartonType() {
        return cartonType;
    }

}
