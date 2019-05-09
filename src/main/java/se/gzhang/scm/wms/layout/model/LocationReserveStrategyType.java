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

package se.gzhang.scm.wms.layout.model;

import se.gzhang.scm.wms.common.model.EnumWithDescription;

public enum LocationReserveStrategyType  implements EnumWithDescription<String> {
    BY_ORDER("Reserve Location By Order"),
    BY_CUSTOMER("Reserve Location By Customer"),
    BY_ANYTHING("One Location for All");

    private String description;

    private LocationReserveStrategyType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
