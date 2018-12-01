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

package se.gzhang.scm.wms.inbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.model.ReceiptLine;
import se.gzhang.scm.wms.inbound.repository.ReceiptLineRepository;
import se.gzhang.scm.wms.inbound.repository.ReceiptRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReceiptLineService {

    @Autowired
    ReceiptLineRepository receiptLineRepository;

    public List<ReceiptLine> findAll(){

        return receiptLineRepository.findAll();
    }

    public ReceiptLine findByReceiptLineId(int id){
        return receiptLineRepository.findById(id);
    }


    public ReceiptLine findByExternalID(String externalID) {
        return receiptLineRepository.findByExternalID(externalID);

    }


    public ReceiptLine save(ReceiptLine receiptLine) {
        ReceiptLine newReceiptLine = receiptLineRepository.save(receiptLine);
        receiptLineRepository.flush();
        return newReceiptLine;
    }


    public List<ReceiptLine> findReceipts(Map<String, String> criteriaList) {
        return receiptLineRepository.findAll(new Specification<ReceiptLine>() {
            @Override
            public Predicate toPredicate(Root<ReceiptLine> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("externalID") && !criteriaList.get("externalID").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("externalID"), criteriaList.get("externalID")));
                }



                if((criteriaList.containsKey("trailerID") && !criteriaList.get("trailerID").isEmpty()) ||
                        (criteriaList.containsKey("trailerNumber") && !criteriaList.get("trailerNumber").isEmpty()) ||
                        (criteriaList.containsKey("trailerLicensePlate") && !criteriaList.get("trailerLicensePlate").isEmpty())) {
                    Join<ReceiptLine, Receipt> joinReceipt = root.join("receipt",JoinType.INNER);
                    Join<Receipt, Trailer> joinTrailer = joinReceipt.join("trailer",JoinType.INNER);

                    if(criteriaList.containsKey("trailerID") && !criteriaList.get("trailerID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinTrailer.get("id"), criteriaList.get("trailerID")));
                    }
                    if(criteriaList.containsKey("trailerNumber") && !criteriaList.get("trailerNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinTrailer.get("trailerNumber"), criteriaList.get("trailerNumber")));
                    }
                    if(criteriaList.containsKey("trailerLicensePlate") && !criteriaList.get("trailerLicensePlate").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinTrailer.get("licensePlate"), criteriaList.get("trailerLicensePlate")));
                    }


                    if(criteriaList.containsKey("receiptID") && !criteriaList.get("receiptID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("id"), criteriaList.get("receiptID")));
                    }
                    if(criteriaList.containsKey("receiptNumber") && !criteriaList.get("receiptNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("number"), criteriaList.get("receiptNumber")));
                    }
                    if(criteriaList.containsKey("receiptExternalID") && !criteriaList.get("receiptExternalID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("externalID"), criteriaList.get("receiptExternalID")));
                    }
                }
                else if((criteriaList.containsKey("receiptID") && !criteriaList.get("receiptID").isEmpty()) ||
                        (criteriaList.containsKey("receiptNumber") && !criteriaList.get("receiptNumber").isEmpty()) ||
                        (criteriaList.containsKey("receiptExternalID") && !criteriaList.get("receiptExternalID").isEmpty())) {

                    Join<ReceiptLine, Receipt> joinReceipt = root.join("receipt",JoinType.INNER);

                    if(criteriaList.containsKey("receiptID") && !criteriaList.get("receiptID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("id"), criteriaList.get("receiptID")));
                    }
                    if(criteriaList.containsKey("receiptNumber") && !criteriaList.get("receiptNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("number"), criteriaList.get("receiptNumber")));
                    }
                    if(criteriaList.containsKey("receiptExternalID") && !criteriaList.get("receiptExternalID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("externalID"), criteriaList.get("receiptExternalID")));
                    }

                }


                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public void deleteByReceiptLineID(int receiptLineID) {
        receiptLineRepository.deleteById(receiptLineID);
    }
}
