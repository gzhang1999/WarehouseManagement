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

package se.gzhang.scm.wms.framework.controls.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.framework.controls.model.DropdownList;
import se.gzhang.scm.wms.framework.controls.model.DropdownOption;
import se.gzhang.scm.wms.framework.controls.repository.DropdownListRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

@Service("dropdownListService")
public class DropdownListService {
    @Autowired
    DropdownListRepository dropdownListRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Cacheable("dropdownList")
    public DropdownList findByVariable(String variable){
        DropdownList dropdownList = dropdownListRepository.findByVariable(variable);

        // Check if we need to get the options from database table or
        // from SQL command
        if (dropdownList != null
                && dropdownList.getCommand() != null
                && !dropdownList.getCommand().equals("")) {
            // Let's execute the SQL and construct another list of dropdown options
            dropdownList.setDropdownOptions(getDropdownOptionsFromSQL(dropdownList));

        }
        return dropdownList;


    }

    private List<DropdownOption> getDropdownOptionsFromSQL(DropdownList dropdownList) {
        String sqlCommand = dropdownList.getCommand();
        EntityManager session = entityManagerFactory.createEntityManager();
        try {
            List<DropdownOption> dropdownOptionList = session.createNativeQuery(sqlCommand, DropdownOption.class)
                    .getResultList();

            for(DropdownOption option : dropdownOptionList) {
                System.out.println("\nValue: " + option.getValue() +
                                   "\nText: " + option.getText());
                option.setDropdownList(dropdownList);
            }

            return dropdownOptionList;
        }
        catch (NoResultException e){
            return null;
        }
        finally {
            if(session.isOpen()) session.close();
        }
    }
}
