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

public class PickException extends StandProductException {

    public static final PickException NO_SUCH_PICK = new PickException(PickExceptionType.NO_SUCH_PICK);

    public static final PickException NOT_VALID_STATE_FOR_CANCELLATION= new PickException(PickExceptionType.NOT_VALID_STATE_FOR_CANCELLATION);
    public static final PickException OVER_PICK_PROHIBIT = new PickException(PickExceptionType.OVER_PICK_PROHIBIT);
    public static final PickException NOT_SUFFICIENT_QUANTITY = new PickException(PickExceptionType.NOT_SUFFICIENT_QUANTITY);

    public static final PickException SPLIT_NOT_ALLOWED = new PickException(PickExceptionType.SPLIT_NOT_ALLOWED);
    public static final PickException SPLIT_NOT_ALLOWED_NOT_RIGHT_STATE = new PickException(PickExceptionType.SPLIT_NOT_ALLOWED_NOT_RIGHT_STATE);

    public PickException(PickExceptionType pickExceptionType) {
        super("PickException." + pickExceptionType.name(), pickExceptionType.getDescription());
    }

    public PickException(PickExceptionType pickExceptionType, String message) {
        super("PickException." + pickExceptionType.name(), message);
    }
}
