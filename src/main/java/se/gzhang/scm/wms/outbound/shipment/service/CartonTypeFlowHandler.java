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

package se.gzhang.scm.wms.outbound.shipment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.LocalParameterMap;
import se.gzhang.scm.wms.exception.Outbound.CartonTypeException;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.outbound.shipment.model.CartonType;
import se.gzhang.scm.wms.outbound.shipment.model.CartonTypeFlowModel;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class CartonTypeFlowHandler {

    @Autowired
    private CartonTypeService cartonTypeService;
    @Autowired
    private WarehouseService warehouseService;

    public CartonTypeFlowModel init() {
        return new CartonTypeFlowModel();
    }
    public CartonTypeFlowModel init(int cartonTypeID) {
        return new CartonTypeFlowModel(cartonTypeService.findByCartonTypeId(cartonTypeID));
    }


    public boolean saveCartonType(CartonTypeFlowModel cartonTypeFlowModel, LocalParameterMap parameters,
                                  HttpSession session, MessageContext context){
        CartonType cartonType = getCartonTypeFromHttpRequestParameters(parameters);

        cartonType.setWarehouse(warehouseService.findByWarehouseId(Integer.parseInt(session.getAttribute("warehouse_id").toString())));

        cartonTypeFlowModel.setupCartonType(cartonType);

        // Make sure we won't have duplicate carton type name
        if(!cartonTypeFlowModel.isModify() && cartonTypeService.findByCartonTypeName(cartonType.getName()) != null) {

            context.addMessage(new MessageBuilder().error().source("name").code(CartonTypeException.DUPLICATED_CARTON_TYPE_NAME.getCode()).build());
            return false;
        }
        return true;
    }
    public void serializeCartonType(CartonTypeFlowModel cartonTypeFlowModel){
        CartonType cartonType = cartonTypeFlowModel.getCartonType();
        cartonTypeService.save(cartonType);
    }

    private CartonType getCartonTypeFromHttpRequestParameters(LocalParameterMap parameters) {
        CartonType cartonType = new CartonType();

        Map<String, Object> parameterMap = parameters.asMap();

        for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
            try {

                String fieldName = parameter.getKey();
                Object fieldValue = parameter.getValue();

                Field field = cartonType.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                if (field.getType() == Integer.class) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        field.set(cartonType, Integer.parseInt(fieldValue.toString()));
                    }
                }
                else if (field.getType() == Double.class) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        field.set(cartonType, Double.parseDouble(fieldValue.toString()));
                    }
                }
                else if (field.getType() == Boolean.class) {
                    if (fieldValue != null &&
                            !fieldValue.toString().isEmpty()) {
                        field.set(cartonType, Boolean.valueOf(fieldValue.toString()));
                    }
                }
                else {
                    field.set(cartonType, fieldValue);

                }
            } catch (NoSuchFieldException ex) {
                // ignore
                System.out.println("NoSuchFieldException: " + ex.getMessage() + "\n" );
            } catch (Exception e) {
                // Ignore any error
                System.out.println("Error while saving trailer: " + e.getMessage() + "\n" );
                e.printStackTrace();
            }
        }
        return cartonType;
    }

}
