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

import se.gzhang.scm.wms.inventory.model.Inventory;

import java.io.Serializable;
import java.util.List;

public class AddPutawayPolicyFlowModel implements Serializable {

    private PutawayPolicy putawayPolicy;

    // After we setup a new putaway, we will fill the list with inventory
    // 1. matches the putaway policy
    // 2. in receiving stage
    // so that the user can apply the putaway right after creating
    private List<Inventory> inventoryList;

    public AddPutawayPolicyFlowModel(){
        putawayPolicy = new PutawayPolicy();
    }


    public PutawayPolicy getPutawayPolicy() {
        return putawayPolicy;
    }

    public List<Inventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }
}