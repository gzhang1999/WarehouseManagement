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

package se.gzhang.scm.wms.exception.inventory;

import se.gzhang.scm.wms.exception.StandProductException;

public class InventoryException extends StandProductException {

    public static final InventoryException SPLIT_ERROR_NOT_SUFFICIENT_QUANTITY = new InventoryException(InventoryExceptionType.SPLIT_ERROR_NOT_SUFFICIENT_QUANTITY);
    public static final InventoryException NO_SUCH_INVENTORY = new InventoryException(InventoryExceptionType.NO_SUCH_INVENTORY);
    public static final InventoryException NOT_VALID_MOVEMENT = new InventoryException(InventoryExceptionType.NOT_VALID_MOVEMENT);
    public static final InventoryException PARTIAL_PICK_NOT_ALLOWED = new InventoryException(InventoryExceptionType.PARTIAL_PICK_NOT_ALLOWED);

    public InventoryException(InventoryExceptionType inventoryExceptionType) {
        super("InventoryException." + inventoryExceptionType.name(), inventoryExceptionType.getDescription());
    }

    public InventoryException(InventoryExceptionType inventoryExceptionType, String message) {
        super("InventoryException." + inventoryExceptionType.name(), message);
    }
}