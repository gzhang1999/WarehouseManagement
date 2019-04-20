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

package se.gzhang.scm.wms.inbound.model;

import se.gzhang.scm.wms.common.model.EnumWithDescription;

public enum PutawayPolicyStrategy implements EnumWithDescription<String> {
    EMPTY_LOCATION_ONLY("Empty Location Only"),
    MIXED_LOCATION_ONLY("Mixed Location Only"),
    EMPTY_LOCATION_FIRST("Empty Location First"),
    MIXED_LOCATION_FIRST("Mixed Location First");

    private String description;

    private PutawayPolicyStrategy(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
