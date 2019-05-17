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

package se.gzhang.scm.wms.exception.Inbound;

import se.gzhang.scm.wms.common.model.EnumWithDescription;

public enum ReceiptExceptionType implements EnumWithDescription<String> {
    TRAILER_NOT_VALID_STATE("The trailer is not in the right state"),
    NO_SUCH_RECEIPT("Can not find the receipt"),
    INVALID_REMOVE_RECEIPT_STARTED("Can't remove this receipt as it is already started"),
    INVALID_MODIFY_TRAILER_CHECKEDIN("Can't modify this receipt after the trailer is checked in");

    private String description;

    private ReceiptExceptionType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }


}
