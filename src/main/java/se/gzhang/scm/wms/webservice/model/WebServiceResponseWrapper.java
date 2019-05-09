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

package se.gzhang.scm.wms.webservice.model;


import se.gzhang.scm.wms.exception.GenericException;

import java.util.HashMap;
import java.util.Map;

// Class to wrap all the ajax returns
// status: 0 -- no error. Otherwise, error code
// message: empty -- no error. Otherwise, error message
// data: data
public class WebServiceResponseWrapper<T> {
    private int status;
    private String errorCode;
    private String message;
    private T data;

    // a map to store custmized data
    private Map<String, String> customFields;

    public WebServiceResponseWrapper(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        customFields = new HashMap<String, String>();
    }

    public WebServiceResponseWrapper(int status, String message, T data, Map<String, String> customFields) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.customFields = customFields;
    }
    public WebServiceResponseWrapper(int status, String errorCode, String message, T data) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.customFields = new HashMap<String, String>();
    }

    public void addCustomData(String name, String value) {
        customFields.put(name, value);
    }

    public static WebServiceResponseWrapper raiseError(String errorCode, String errorMessage) {
        return new WebServiceResponseWrapper<String>(-1, errorCode, errorMessage, "");
    }
    public static WebServiceResponseWrapper raiseError(GenericException exception) {
        return new WebServiceResponseWrapper<String>(-1, exception.getCode(), exception.getMessage(), "");
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
