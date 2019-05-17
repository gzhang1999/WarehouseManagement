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

package se.gzhang.scm.wms.reporting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.AreaType;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.reporting.model.Report;
import se.gzhang.scm.wms.reporting.model.ReportArchive;
import se.gzhang.scm.wms.reporting.repository.ReportArchiveRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportArchiveService {

    @Autowired
    ReportArchiveRepository reportArchiveRepository;

    public List<ReportArchive> findAll() {

        return reportArchiveRepository.findAll();

    }

    public ReportArchive findByReportArchiveId(int reportArchiveID) {

        return reportArchiveRepository.findById(reportArchiveID);
    }


    public List<ReportArchive> findByReportName(String reportName) {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("reportName", reportName);
        return findReportArchive(criteriaList);
    }
    public List<ReportArchive> findByReportID(int reportID) {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("reportID", String.valueOf(reportID));
        return findReportArchive(criteriaList);
    }



    public List<ReportArchive> findReportArchive(Map<String, String> criteriaList) {
        return reportArchiveRepository.findAll(new Specification<ReportArchive>() {
            @Override
            public Predicate toPredicate(Root<ReportArchive> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if ((criteriaList.containsKey("reportID") && !criteriaList.get("reportID").isEmpty()) ||
                        (criteriaList.containsKey("reportName") && !criteriaList.get("reportName").isEmpty())) {
                    Join<ReportArchive, Report> joinReport = root.join("report",JoinType.INNER);

                    if(criteriaList.containsKey("reportID") && !criteriaList.get("reportID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReport.get("id"), criteriaList.get("reportID")));
                    }
                    if(criteriaList.containsKey("reportName") && !criteriaList.get("reportName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReport.get("name"), criteriaList.get("reportName")));
                    }
                }

                if ((criteriaList.containsKey("printedUserID") && !criteriaList.get("printedUserID").isEmpty()) ||
                        (criteriaList.containsKey("printedUserName") && !criteriaList.get("printedUserName").isEmpty())) {
                    Join<ReportArchive, User> joinUser = root.join("printedUser",JoinType.INNER);

                    if(criteriaList.containsKey("printedUserID") && !criteriaList.get("printedUserID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinUser.get("id"), criteriaList.get("printedUserID")));
                    }
                    if(criteriaList.containsKey("printedUserName") && !criteriaList.get("printedUserName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinUser.get("name"), criteriaList.get("printedUserName")));
                    }
                }

                if(criteriaList.containsKey("key1") && !criteriaList.get("key1").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("key1"), criteriaList.get("key1")));
                }
                if(criteriaList.containsKey("key2") && !criteriaList.get("key2").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("key2"), criteriaList.get("key2")));
                }
                if(criteriaList.containsKey("key3") && !criteriaList.get("key3").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("key3"), criteriaList.get("key3")));
                }
                if(criteriaList.containsKey("key4") && !criteriaList.get("key4").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("key4"), criteriaList.get("key4")));
                }
                if(criteriaList.containsKey("key5") && !criteriaList.get("key5").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("key5"), criteriaList.get("key5")));
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    @Transactional
    public ReportArchive save(ReportArchive reportArchive){
        return reportArchiveRepository.save(reportArchive);
    }


}
