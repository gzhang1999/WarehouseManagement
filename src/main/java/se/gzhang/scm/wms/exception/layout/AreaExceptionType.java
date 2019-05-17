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

package se.gzhang.scm.wms.exception.layout;

import se.gzhang.scm.wms.common.model.EnumWithDescription;

public enum AreaExceptionType implements EnumWithDescription<String> {

    NO_SUCH_AREA("Can not find the area"),
    NO_SHIPPED_AREA_CONFIG("Can't find any area for shipped inventory"),
    MULTIPLE_SHIPPED_AREA_CONFIG("More than one shipped area configured"),
    NO_SHIPPING_STAGE_AREA_CONFIG("Can't find any area for shipment stage"),
    MULTIPLE_SHIPPING_STAGE_AREA_CONFIG("More than one shipping stage area configured");

    private String description;

    private AreaExceptionType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }


}
