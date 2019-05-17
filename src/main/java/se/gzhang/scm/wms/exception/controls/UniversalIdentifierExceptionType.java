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

package se.gzhang.scm.wms.exception.controls;

import se.gzhang.scm.wms.common.model.EnumWithDescription;

public enum UniversalIdentifierExceptionType implements EnumWithDescription<String> {
    NO_SUCH_IDENTIFIER("Can not find the identifier"),
    MAX_NUMBER_REACHED("Not able to get the next number as the maximum number has been reached");

    private String description;

    private UniversalIdentifierExceptionType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }


}
