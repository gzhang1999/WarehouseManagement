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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import se.gzhang.scm.wms.common.model.EnumWithDescription;
import se.gzhang.scm.wms.framework.controls.model.DropdownList;
import se.gzhang.scm.wms.framework.controls.model.DropdownOption;
import se.gzhang.scm.wms.framework.controls.repository.DropdownListRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("dropdownListService")
public class DropdownListService {
    @Autowired
    DropdownListRepository dropdownListRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Cacheable("dropdownList")
    public DropdownList findByVariable(String variable,
                                       Map<String, String> parameters){
        DropdownList dropdownList = dropdownListRepository.findByVariable(variable);

        // Check if we need to get the options from database table or
        // from SQL command
        if (dropdownList != null) {
            if (dropdownList.getCommand() != null
                && !dropdownList.getCommand().equals("")) {
                // Let's execute the SQL and construct another list of dropdown options
                dropdownList.setDropdownOptions(getDropdownOptionsFromSQL(dropdownList, parameters));
            }
            else if(dropdownList.getEnumClass() != null
                    && !dropdownList.getEnumClass().equals("")) {
                // Let's execute the SQL and construct another list of dropdown options
                dropdownList.setDropdownOptions(getDropdownOptionsFromEnumClass(dropdownList));
            }
        }
        return dropdownList;
    }

    @CacheEvict(value="dropdownList",allEntries=true)
    public void evictCache() {
        // evict all caches
    }

    private List<DropdownOption> getDropdownOptionsFromSQL(DropdownList dropdownList, Map<String, String> parameters) {
        String sqlCommand = dropdownList.getCommand();
        if (!parameters.isEmpty()) {
            sqlCommand += " where ";
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                sqlCommand += entry.getKey() + " = '" + entry.getValue() + "' and " ;
            }
            if (sqlCommand.endsWith(" and ")) {
                sqlCommand = sqlCommand.substring(0, sqlCommand.lastIndexOf(" and "));
            }
        }
        EntityManager session = entityManagerFactory.createEntityManager();
        try {
            System.out.println(">>>>>>>  Start to execute SQL: " + sqlCommand);
            List<DropdownOption> dropdownOptionList = session.createNativeQuery(sqlCommand, DropdownOption.class)
                    .getResultList();

            for(DropdownOption option : dropdownOptionList) {
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


    private List<DropdownOption> getDropdownOptionsFromEnumClass(DropdownList dropdownList) {
        String enumClassName = dropdownList.getEnumClass();
        List<DropdownOption> dropdownOptionList = new ArrayList<>();
        try {
            Class<? extends EnumWithDescription<String>> enumClass = (Class<? extends EnumWithDescription<String>>) Class.forName(enumClassName);
            EnumWithDescription<String>[] enumValues = enumClass.getEnumConstants();

            for (EnumWithDescription<String> enumItem : enumValues) {
                System.out.println(enumItem + " = " + enumItem.getDescription());
                DropdownOption dropdownOption = new DropdownOption();
                dropdownOption.setValue(enumItem.toString());
                dropdownOption.setText(enumItem.getDescription());
                dropdownOptionList.add(dropdownOption);
            }
        }
        catch (ClassNotFoundException ex) {
            System.out.println("Class not found exception(" + enumClassName + "): " + ex.getMessage());
        }
        return dropdownOptionList;
    }

}
