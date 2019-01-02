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
import se.gzhang.scm.wms.authorization.model.User;
import se.gzhang.scm.wms.reporting.model.Report;
import se.gzhang.scm.wms.reporting.model.ReportArchive;
import se.gzhang.scm.wms.reporting.model.ReportTriggerPoint;
import se.gzhang.scm.wms.reporting.repository.ReportArchiveRepository;
import se.gzhang.scm.wms.reporting.repository.ReportTriggerPointRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportTriggerPointService {

    @Autowired
    ReportTriggerPointRepository reportTriggerPointRepository;

    public List<ReportTriggerPoint> findAll() {

        return reportTriggerPointRepository.findAll();

    }

    public ReportTriggerPoint findByReportTriggerPointId(int reportTriggerPointID) {

        return reportTriggerPointRepository.findById(reportTriggerPointID);
    }

    public List<ReportTriggerPoint> findByReportTriggerPointName(String reportTriggerPointName) {

        return reportTriggerPointRepository.findByName(reportTriggerPointName);
    }

    public List<ReportTriggerPoint> findByReportName(String reportName) {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("reportName", reportName);
        return findReportTriggerPoint(criteriaList);
    }
    public List<ReportTriggerPoint> findByReportID(int reportID) {
        Map<String, String> criteriaList = new HashMap<>();
        criteriaList.put("reportID", String.valueOf(reportID));
        return findReportTriggerPoint(criteriaList);
    }

    public List<Report> findReportsByTriggerPoint(String reportTriggerPointName) {
        List<ReportTriggerPoint> reportTriggerPointList = findByReportTriggerPointName(reportTriggerPointName);
        List<Report> reportList = new ArrayList<>();
        for(ReportTriggerPoint reportTriggerPoint : reportTriggerPointList) {
            reportList.add(reportTriggerPoint.getReport());
        }
        return reportList;
    }



    public List<ReportTriggerPoint> findReportTriggerPoint(Map<String, String> criteriaList) {
        System.out.println("Find ReportTriggerPoint by :");
        for(Map.Entry<String, String> entry : criteriaList.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        return reportTriggerPointRepository.findAll(new Specification<ReportTriggerPoint>() {
            @Override
            public Predicate toPredicate(Root<ReportTriggerPoint> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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

                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }



}
