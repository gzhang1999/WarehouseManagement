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

import java.util.List;

public class DataTable {
    private List<String> columns;
    private List<List<String>> rows;

    private boolean allowNewFlag;
    private boolean allowEditFlag;
    private boolean allowDeleteFlag;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public boolean isAllowNewFlag() {
        return allowNewFlag;
    }

    public void setAllowNewFlag(boolean allowNewFlag) {
        this.allowNewFlag = allowNewFlag;
    }

    public boolean isAllowEditFlag() {
        return allowEditFlag;
    }

    public void setAllowEditFlag(boolean allowEditFlag) {
        this.allowEditFlag = allowEditFlag;
    }

    public boolean isAllowDeleteFlag() {
        return allowDeleteFlag;
    }

    public void setAllowDeleteFlag(boolean allowDeleteFlag) {
        this.allowDeleteFlag = allowDeleteFlag;
    }
}
