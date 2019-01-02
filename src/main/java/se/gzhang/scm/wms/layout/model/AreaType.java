/**
 * Copyright 2018
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

public enum AreaType implements EnumWithDescription<String> {
    OUTBOUND_STAGE("Outbound Stage"),
    INBOUND_STAGE("Inbound Stage"),
    INBOUND_OUTBOUND_STAGE("Inbound and Outbound Stage"),
    STOREAGE("Storage"),
    PICKUP_AND_DEPOSIT("Pickup and Deposit"),
    PROCESSING("Processing"),
    PRODUCTION_LINE("Production Line"),
    PRODUCTION_LINE_IN_STAGE("Production Line Inbound Stage"),
    PRODUCTION_LINE_OUT_STAGE("Production Line Outbound Stage"),
    YARD("Yard"),
    DOCKDOOR("Dock Door");

    private String description;

    private AreaType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription(){
        return description;
    }
}
