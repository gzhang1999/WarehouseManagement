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

package se.gzhang.scm.wms.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.common.model.Client;
import se.gzhang.scm.wms.common.repository.ClientRepository;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public List<Client> findAll(){

        return clientRepository.findAll();
    }

    public Client findByClientId(int id){
        return clientRepository.findById(id);
    }

    public Client findByClientName(String name) {
        return clientRepository.findByName(name);
    }

    @Transactional
    public Client getDefaultClient() {
        Client client = findByClientName("DEFAULT");
        if (client == null) {
            client = new Client();
            client.setName("DEFAULT");
            return save(client);
        }
        return client;
    }

    @Transactional
    public Client save(Client client) {
        return clientRepository.save(client);
    }
}
