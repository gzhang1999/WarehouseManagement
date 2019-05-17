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

public class ItemException extends StandProductException {

    public static final ItemException NO_SUCH_ITEM = new ItemException(ItemExceptionType.NO_SUCH_ITEM);

    public ItemException(ItemExceptionType itemExceptionType) {
        super("ItemException." + itemExceptionType.name(), itemExceptionType.getDescription());
    }

    public ItemException(ItemExceptionType itemExceptionType, String message) {
        super("ItemException." + itemExceptionType.name(), message);
    }
}