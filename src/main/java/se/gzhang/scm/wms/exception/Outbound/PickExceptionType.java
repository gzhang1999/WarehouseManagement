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

import se.gzhang.scm.wms.common.model.EnumWithDescription;

public enum PickExceptionType implements EnumWithDescription<String> {
    NO_SUCH_PICK("Can not find the pick"),
    NOT_VALID_STATE_FOR_CANCELLATION("Pick is in a state that is not for cancellation"),
    OVER_PICK_PROHIBIT("Over Pick is not allowed"),
    NOT_SUFFICIENT_QUANTITY("There's not enough quantity left in the location to be picked"),
    SPLIT_NOT_ALLOWED("Can't split the pick - internal error"),
    SPLIT_NOT_ALLOWED_NOT_RIGHT_STATE("Can't split the pick - pick is not in right state");

    private String description;

    private PickExceptionType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
