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

package se.gzhang.scm.wms.authorization.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.gzhang.scm.wms.authorization.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer>, JpaSpecificationExecutor<Role> {

    Role findByName(String name);

    Role findById(int id);

    List<Role> findAll();

}