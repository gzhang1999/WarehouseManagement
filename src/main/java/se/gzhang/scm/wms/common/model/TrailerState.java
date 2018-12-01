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

package se.gzhang.scm.wms.common.model;

public enum TrailerState implements EnumWithDescription<String>{
    EXPECTED("Expected"),    // State before the trailer arrive
    CHECKED_IN("Checked In"),  // State when the trailer arrived, before start loading / unloadind
    PROCESSING("Processing"),  // loading / unloading the trailer
    CLOSED("Closed"),      // close the trailer to move to the yard or dispatch to the destination
    DISPATCHED("Dispatched"),  // Dispatched to the destination
    VOID("Void");         // Trailer is not valid any more

    private String description;

    private TrailerState(String description) {
        this.description = description;
    }

    @Override
    public String getDescription(){
        return description;
    }
}
