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

public class SalesOrderException extends StandProductException {

    public static final SalesOrderException NO_SUCH_SALESORDER_EXCEPTION = new SalesOrderException(SalesOrderExceptionType.NO_SUCH_SALESORDER);

    public SalesOrderException(SalesOrderExceptionType salesOrderExceptionType) {
        super("SalesOrderException." + salesOrderExceptionType.name(), salesOrderExceptionType.getDescription());
    }

    public SalesOrderException(SalesOrderExceptionType salesOrderExceptionType, String message) {
        super("SalesOrderException." + salesOrderExceptionType.name(), message);
    }
}
