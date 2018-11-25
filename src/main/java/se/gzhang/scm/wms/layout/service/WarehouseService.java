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

package se.gzhang.scm.wms.layout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.repository.WarehouseRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("warehouseService")
public class WarehouseService {
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private UserService userService;

    public Warehouse findByWarehouseId(int id) {
        return warehouseRepository.findById(id);
    }

    public List<Warehouse> findAll() {

        return warehouseRepository.findAll();
    }

    public Warehouse findByWarehouseName(String name){
        return warehouseRepository.findByName(name);
    }

    // Return all the users that have access to the warehouse
    public List<User> getAccessibleUsers(Warehouse warehouse) {
        List<User> accessibleUserList = userService.findAll();

        for(Iterator<User> iterator = accessibleUserList.iterator(); iterator.hasNext();) {
            User user = iterator.next();
            boolean warehouseAccessible = false;
            for(Warehouse accessibleWarehouse : user.getWarehouses()) {
                if (accessibleWarehouse.equals(warehouse)) {
                    // Current user has access to the warehouse
                    warehouseAccessible = true;
                    break;
                }
            }
            if (!warehouseAccessible) {
                // remove the user from the return list as it doesn't have
                // access to the warehouse
                iterator.remove();
            }
        }
        return accessibleUserList;
    }

    // Return all the users' id that have access to the warehouse
    public List<Integer> getAccessibleUserIDs(Warehouse warehouse) {
        List<User> accessibleUser = getAccessibleUsers(warehouse);
        List<Integer> accessibleUserIDList = new ArrayList<>();
        for(User user : accessibleUser) {
            accessibleUserIDList.add(user.getId());
        }
        return accessibleUserIDList;
    }

    public List<User> getAccessibleUsers(int warehouseID) {
        return getAccessibleUsers(findByWarehouseId(warehouseID));
    }

}
