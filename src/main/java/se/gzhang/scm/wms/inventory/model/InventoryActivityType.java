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

package se.gzhang.scm.wms.inventory.model;

public enum InventoryActivityType {
    INVENTORY_QUANTITY_ADJUSTMENT {
        @Override
        public String toString(){
            return "Inventory Quantity Adjustment";
        }
    },
    CREATE_INVENTORY {
        @Override
        public String toString() {
            return "Create Inventory";
        }
    },
    DELETE_INVENTORY {
        @Override
        public String toString() {
            return "Delete Inventory";
        }
    },
    CYCLE_COUNT{
        @Override
        public String toString() {
            return "Cycle Count";
        }
    },
    AUDIT_COUNT{
        @Override
        public String toString() {
            return "Audit Count";
        }
    },
    INVENTORY_STATUS_CHANGE{
        @Override
        public String toString() {
            return "Inventory Status Change";
        }
    },
    LPN_CHANGE{
        @Override
        public String toString() {
            return "LPN Change(Relabel)";
        }
    }
}
