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

public class ItemFootprintUOMException extends StandProductException {

    public static final ItemFootprintUOMException NO_SUCH_ITEM_FOOTPRINT_UOM = new ItemFootprintUOMException(ItemFootprintUOMExceptionType.NO_SUCH_ITEM_FOOTPRINT_UOM);
    public static final ItemFootprintUOMException ITEM_FOOTPRINT_UOM_EXISTS = new ItemFootprintUOMException(ItemFootprintUOMExceptionType.ITEM_FOOTPRINT_UOM_EXISTS);

    public ItemFootprintUOMException(ItemFootprintUOMExceptionType itemFootprintUOMExceptionType) {
        super("ItemFootprintUOMException." + itemFootprintUOMExceptionType.name(), itemFootprintUOMExceptionType.getDescription());
    }

    public ItemFootprintUOMException(ItemFootprintUOMExceptionType itemFootprintUOMExceptionType, String message) {
        super("ItemFootprintUOMException." + itemFootprintUOMExceptionType.name(), message);
    }
}