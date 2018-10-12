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


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

// Class to wrap all the ajax returns
// status: 0 -- no error. Otherwise, error code
// message: empty -- no error. Otherwise, error message
// data: data
@Data
public class WebServiceResponseWrapper<T> {
    private int status;
    private String message;
    private T data;

    // a map to store custmized data
    private Map<String, String> customField;

    public WebServiceResponseWrapper(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        customField = new HashMap<String, String>();
    }

    public WebServiceResponseWrapper(int status, String message, T data, Map<String, String> customField) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.customField = customField;
    }

    public void addCustomData(String name, String value) {
        customField.put(name, value);
    }

    public static WebServiceResponseWrapper raiseError(int errorCode, String errorMessage) {
        return new WebServiceResponseWrapper(errorCode, errorMessage, "");
    }
}
