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

public class ItemFootprintException extends StandProductException {

    public static final ItemFootprintException NO_SUCH_ITEM_FOOTPRINT = new ItemFootprintException(ItemFootprintExceptionType.NO_SUCH_ITEM_FOOTPRINT);
    public static final ItemFootprintException NO_DEFAULT_ITEM_FOOTPRINT = new ItemFootprintException(ItemFootprintExceptionType.NO_DEFAULT_ITEM_FOOTPRINT);

    public ItemFootprintException(ItemFootprintExceptionType itemFootprintExceptionType) {
        super("ItemFootprintException." + itemFootprintExceptionType.name(), itemFootprintExceptionType.getDescription());
    }

    public ItemFootprintException(ItemFootprintExceptionType itemFootprintExceptionType, String message) {
        super("ItemFootprintException." + itemFootprintExceptionType.name(), message);
    }
}