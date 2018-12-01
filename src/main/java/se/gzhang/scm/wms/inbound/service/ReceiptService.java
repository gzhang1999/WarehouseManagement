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
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.model.TrailerState;
import se.gzhang.scm.wms.common.repository.CarrierRepository;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.inbound.repository.ReceiptRepository;
import se.gzhang.scm.wms.layout.model.Area;
import se.gzhang.scm.wms.layout.model.Building;
import se.gzhang.scm.wms.layout.model.Location;

import javax.persistence.criteria.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReceiptService {

    @Autowired
    ReceiptRepository receiptRepository;

    public List<Receipt> findAll(){

        return receiptRepository.findAll();
    }

    public Receipt findByReceiptId(int id){
        return receiptRepository.findById(id);
    }

    public Receipt findByReceiptNumber(String number) {
        return receiptRepository.findByNumber(number);
    }

    public Receipt findByExternalID(String externalID) {
        return receiptRepository.findByExternalID(externalID);
    }


    public Receipt save(Receipt receipt) {
        Receipt newReceipt = receiptRepository.save(receipt);
        receiptRepository.flush();
        return newReceipt;
    }


    public List<Receipt> findReceipts(Map<String, String> criteriaList) {
        return receiptRepository.findAll(new Specification<Receipt>() {
            @Override
            public Predicate toPredicate(Root<Receipt> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("externalID") && !criteriaList.get("externalID").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("externalID"), criteriaList.get("externalID")));
                }
                if(criteriaList.containsKey("number") && !criteriaList.get("number").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("number"), criteriaList.get("number")));
                }
                if(criteriaList.containsKey("purchaseOrderNumber") && !criteriaList.get("purchaseOrderNumber").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("purchaseOrderNumber"), criteriaList.get("purchaseOrderNumber")));
                }


                if((criteriaList.containsKey("supplierID") && !criteriaList.get("supplierID").isEmpty()) ||
                        (criteriaList.containsKey("supplierName") && !criteriaList.get("supplierName").isEmpty())) {
                    Join<Receipt, Supplier> joinSupplier = root.join("supplier",JoinType.INNER);

                    if(criteriaList.containsKey("supplierID") && !criteriaList.get("supplierID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSupplier.get("id"), criteriaList.get("supplierID")));
                    }
                    if(criteriaList.containsKey("supplierName") && !criteriaList.get("supplierName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinSupplier.get("name"), criteriaList.get("supplierName")));
                    }
                }

                if((criteriaList.containsKey("trailerID") && !criteriaList.get("trailerID").isEmpty()) ||
                        (criteriaList.containsKey("trailerNumber") && !criteriaList.get("trailerNumber").isEmpty()) ||
                        (criteriaList.containsKey("trailerLicensePlate") && !criteriaList.get("trailerLicensePlate").isEmpty())) {
                    Join<Receipt, Trailer> joinTrailer = root.join("trailer",JoinType.INNER);

                    if(criteriaList.containsKey("trailerID") && !criteriaList.get("trailerID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinTrailer.get("id"), criteriaList.get("trailerID")));
                    }
                    if(criteriaList.containsKey("trailerNumber") && !criteriaList.get("trailerNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinTrailer.get("trailerNumber"), criteriaList.get("trailerNumber")));
                    }
                    if(criteriaList.containsKey("trailerLicensePlate") && !criteriaList.get("trailerLicensePlate").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinTrailer.get("licensePlate"), criteriaList.get("trailerLicensePlate")));
                    }
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public void deleteByReceiptID(int receiptID) {
        receiptRepository.deleteById(receiptID);
    }

    public Receipt createReceipt(String externalID,
                                 String number,
                                 String purchaseOrderNumber,
                                 Supplier supplier,
                                 Trailer trailer){
        Receipt receipt = new Receipt();
        receipt.setExternalID(externalID);
        receipt.setNumber(number);
        receipt.setPurchaseOrderNumber(purchaseOrderNumber);
        receipt.setSupplier(supplier);
        receipt.setTrailer(trailer);

        return save(receipt);
    }

    public Receipt changeReceipt(Receipt receipt,
                                 String externalID,
                                 String purchaseOrderNumber,
                                 Supplier supplier){
        if (receipt.getTrailer().getTrailerState() != TrailerState.EXPECTED) {
            throw new GenericException(10000, "Can not change the receipt when the trailer is already checked in");
        }
        receipt.setExternalID(externalID);
        receipt.setPurchaseOrderNumber(purchaseOrderNumber);
        receipt.setSupplier(supplier);


        return save(receipt);
    }
}
