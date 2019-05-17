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

public enum CartonTypeExceptionType implements EnumWithDescription<String> {
    NO_SUCH_CARTON_TYPE("Can not find the carton type"),
    DUPLICATED_CARTON_TYPE_NAME("Cannot save the carton type, duplicated carton type name");

    private String description;

    private CartonTypeExceptionType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
