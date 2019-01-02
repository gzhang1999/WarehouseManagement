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

package se.gzhang.scm.wms.menu.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menu")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "menu_id")
    private Integer id;

    @Column(name = "parent_menu_id")
    private int parentMenuID;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_class")
    private String iconClass;

    @Column(name = "mls_id")
    private String multiLanguageSupportID;

    @Column(name = "sequence")
    private int sequence;

    @Column(name = "url")
    private String url;

    @Transient
    List<MenuItem> childMenuList;
    @Transient
    private String parentMenuName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        return id != null && id.equals(((MenuItem) o).id);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getParentMenuID() {
        return parentMenuID;
    }

    public void setParentMenuID(int parentMenuID) {
        this.parentMenuID = parentMenuID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getMultiLanguageSupportID() {
        return multiLanguageSupportID;
    }

    public void setMultiLanguageSupportID(String multiLanguageSupportID) {
        this.multiLanguageSupportID = multiLanguageSupportID;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<MenuItem> getChildMenuList() {
        return childMenuList;
    }

    public void setChildMenuList(List<MenuItem> childMenuList) {
        this.childMenuList = childMenuList;
    }

    public String getParentMenuName() {
        return parentMenuName;
    }

    public void setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
    }
}
