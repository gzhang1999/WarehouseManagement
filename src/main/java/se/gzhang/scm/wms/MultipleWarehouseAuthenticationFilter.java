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

package se.gzhang.scm.wms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.layout.model.Warehouse;
import se.gzhang.scm.wms.layout.service.WarehouseService;
import se.gzhang.scm.wms.menu.service.MenuService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

// Authentication filter to support multiple warehouse environment.
// When the user login in a multi-warehouse environment, the user will need to
// choose the warehouse during login. In other word, the user can only login
// into one warehouse at a time
public class MultipleWarehouseAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    WarehouseService warehouseService;
    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest = getAuthRequest(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {

        String username = obtainUsername(request);

        String password = obtainPassword(request);

        List<Warehouse> warehouseList = warehouseService.findAll();
        if (warehouseList.size() > 1) {
            // This is more than one warehouse exists, let's check the
            // user pass in the correct warehouse id
            String warehouseId = request.getParameter("warehouse_id");
            if (warehouseId == null || warehouseId.equals("")) {
                // error! can't get the warehouse ID from the parameters
                throw new BadCredentialsException("Warehouse ID is required for a multiple warehouse environment");
            }
            else {
                boolean validWarehouse = false;
                for(Warehouse warehouse : warehouseList) {
                    if (warehouse.getId().equals(warehouseId)) {
                        validWarehouse = true;
                        // Save the warehouse in the session
                        request.getSession().setAttribute("warehouse_id", warehouseId);
                        request.getSession().setAttribute("warehouse_name", warehouse.getName());
                        break;
                    }
                }
                if (!validWarehouse) {
                    // The user pass in a invalid warehouse, let's return error
                    throw new BadCredentialsException("Warehouse ID is invalid");
                }

            }
        }
        else if (warehouseList.size() == 1) {
            // there's only one warehouse in the system, let's
            // use it as default warehouse

            request.getSession().setAttribute("warehouse_id", warehouseList.get(0).getId());
            request.getSession().setAttribute("warehouse_name", warehouseList.get(0).getName());
        }
        else {
            // raise error as there's no warehouse setup yet
            throw new BadCredentialsException("Non warehouse is setup");
        }



        return new UsernamePasswordAuthenticationToken(username, password);
    }

}