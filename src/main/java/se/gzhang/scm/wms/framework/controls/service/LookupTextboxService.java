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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.framework.controls.model.LookupTextbox;
import se.gzhang.scm.wms.framework.controls.repository.LookupTextboxRepository;

import javax.persistence.EntityManagerFactory;

@Service
public class LookupTextboxService {
    @Autowired
    LookupTextboxRepository lookupTextboxRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Cacheable("lookupTextbox")
    public LookupTextbox findByVariable(String variable) {
        return getLookupTextboxContent(variable);
    }

    public LookupTextbox getLookupTextboxContent(String variable) {
        LookupTextbox lookupTextbox = lookupTextboxRepository.findByVariable(variable);

        // Check if we need to get the options from database table or
        // from SQL command
        if (lookupTextbox != null) {
            if (lookupTextbox.getCommand() != null
                && !lookupTextbox.getCommand().equals("")) {
                // Let's execute the SQL and construct another list of dropdown options
                lookupTextbox.setResultSet(getResultset(lookupTextbox.getCommand()));

            }
        }
        return lookupTextbox;
    }

    private SqlRowSet getResultset(String sqlCommand) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlCommand);
        int rowCount = 0;
        while (sqlRowSet.next()) {
            rowCount++;
        }
        System.out.println("SQLCommand: " + sqlCommand + "\n >> total row returns: " + rowCount);
        return sqlRowSet;

    }



}
