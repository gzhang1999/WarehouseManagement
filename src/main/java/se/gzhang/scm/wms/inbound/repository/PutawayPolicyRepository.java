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

package se.gzhang.scm.wms.inbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import se.gzhang.scm.wms.inbound.model.PutawayPolicy;
import se.gzhang.scm.wms.inbound.model.Receipt;

import java.util.List;

@Repository
public interface PutawayPolicyRepository extends JpaRepository<PutawayPolicy, Integer>, JpaSpecificationExecutor<PutawayPolicy> {
    List<PutawayPolicy> findAll();

    List<PutawayPolicy> findAllByOrderBySequenceAsc();

    PutawayPolicy findById(int id);

}
