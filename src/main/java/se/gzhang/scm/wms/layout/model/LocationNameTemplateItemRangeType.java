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

package se.gzhang.scm.wms.layout.model;

// Location Name Template Item's range type. When
// creating a range of location by naming template,
// we when choose to create the location that
// 1. Only allow ODD number
// 2. only allow EVEN number
// 3. includes both ODD and EVEN number
// This only works for digit type of name template items
public enum LocationNameTemplateItemRangeType {
    ODD,
    EVEN,
    BOTH
}
