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

package se.gzhang.scm.wms.exception.Outbound;

import se.gzhang.scm.wms.exception.StandProductException;

public class CartonTypeException extends StandProductException {

    public static final CartonTypeException NO_SUCH_CARTON_TYPE = new CartonTypeException(CartonTypeExceptionType.NO_SUCH_CARTON_TYPE);
    public static final CartonTypeException DUPLICATED_CARTON_TYPE_NAME = new CartonTypeException(CartonTypeExceptionType.DUPLICATED_CARTON_TYPE_NAME);


    public CartonTypeException(CartonTypeExceptionType cartonTypeExceptionType) {
        super("CartonTypeException." + cartonTypeExceptionType.name(), cartonTypeExceptionType.getDescription());
    }

    public CartonTypeException(CartonTypeExceptionType cartonTypeExceptionType, String message) {
        super("CartonTypeException." + cartonTypeExceptionType.name(), message);
    }
}
