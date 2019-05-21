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

public class CartonAuditResultException extends StandProductException {

    public static final CartonAuditResultException NO_SUCH_CARTON_AUDIT_RESULT = new CartonAuditResultException(CartonAuditResultExceptionType.NO_SUCH_CARTON_AUDIT_RESULT);
    public static final CartonAuditResultException NO_SUCH_CARTON_AUDIT_RESULT_LINE = new CartonAuditResultException(CartonAuditResultExceptionType.NO_SUCH_CARTON_AUDIT_RESULT_LINE);
    public static final CartonAuditResultException NO_SUCH_CARTON_AUDIT_RESULT_LINE_ITEM = new CartonAuditResultException(CartonAuditResultExceptionType.NO_SUCH_CARTON_AUDIT_RESULT_LINE_ITEM);


    public CartonAuditResultException(CartonAuditResultExceptionType cartonAuditResultExceptionType) {
        super("CartonAuditResultException." + cartonAuditResultExceptionType.name(), cartonAuditResultExceptionType.getDescription());
    }

    public CartonAuditResultException(CartonAuditResultExceptionType cartonAuditResultExceptionType, String message) {
        super("CartonAuditResultException." + cartonAuditResultExceptionType.name(), message);
    }
}
