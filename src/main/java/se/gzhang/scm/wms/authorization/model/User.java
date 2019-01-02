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

package se.gzhang.scm.wms.authorization.model;

import se.gzhang.scm.wms.layout.model.Warehouse;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    @NotEmpty(message = "*Please provide your password")
    private String password;

    @Column(name = "username")
    @NotEmpty(message = "*Please provide your username")
    private String username;

    @Column(name = "last_name")
    @NotEmpty(message = "*Please provide your last name")
    private String lastname;

    @Column(name = "first_name")
    @NotEmpty(message = "*Please provide your first name")
    private String firstname;

    @Column(name = "active")
    private boolean active;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // Only role manager can
    // 1. create / change / remove role
    // 2. assign / deassign menu to role
    @Column(name = "role_manager_flag")
    private boolean roleManager;


    @Column(name = "user_manager_flag")
    private boolean userManager;

    @Column(name = "menu_manager_flag")
    private boolean menuManager;

    // Whether the user need to change password
    // at next logon
    @Column(name = "change_password_flag")
    private boolean changePassword;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_warehouse", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "warehouse_id"))
    private Set<Warehouse> warehouses;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).id);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isRoleManager() {
        return roleManager;
    }

    public void setRoleManager(boolean roleManager) {
        this.roleManager = roleManager;
    }

    public boolean isUserManager() {
        return userManager;
    }

    public void setUserManager(boolean userManager) {
        this.userManager = userManager;
    }

    public boolean isMenuManager() {
        return menuManager;
    }

    public void setMenuManager(boolean menuManager) {
        this.menuManager = menuManager;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public Set<Warehouse> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(Set<Warehouse> warehouses) {
        this.warehouses = warehouses;
    }
}