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

package se.gzhang.scm.wms.exception.Inbound;

import se.gzhang.scm.wms.exception.StandProductException;
import se.gzhang.scm.wms.exception.inventory.InventoryExceptionType;

public class ReceiptException extends StandProductException {

    public static final ReceiptException TRAILER_NOT_VALID_STATE = new ReceiptException(ReceiptExceptionType.TRAILER_NOT_VALID_STATE);
    public static final ReceiptException NO_SUCH_RECEIPT = new ReceiptException(ReceiptExceptionType.NO_SUCH_RECEIPT);
    public static final ReceiptException INVALID_REMOVE_RECEIPT_STARTED = new ReceiptException(ReceiptExceptionType.INVALID_REMOVE_RECEIPT_STARTED);
    public static final ReceiptException INVALID_MODIFY_TRAILER_CHECKEDIN = new ReceiptException(ReceiptExceptionType.INVALID_MODIFY_TRAILER_CHECKEDIN);

    public ReceiptException(ReceiptExceptionType receiptExceptionType) {
        super("ReceiptException." + receiptExceptionType.name(), receiptExceptionType.getDescription());
    }

    public ReceiptException(ReceiptExceptionType receiptExceptionType, String message) {
        super("ReceiptException." + receiptExceptionType.name(), message);
    }
}