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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.framework.controls.model.UniversalIdentifier;
import se.gzhang.scm.wms.framework.controls.repository.UniversalIdentifierRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UniversalIdentifierService {

    @Autowired
    UniversalIdentifierRepository universalIdentifierRepository;


    public UniversalIdentifier save(UniversalIdentifier universalIdentifier) {
        UniversalIdentifier newUniversalIdentifier = universalIdentifierRepository.save(universalIdentifier);
        universalIdentifierRepository.flush();
        return newUniversalIdentifier;
    }

    public UniversalIdentifier deleteUniversalIdentifierById(Integer universalIdentifierId) {
        UniversalIdentifier universalIdentifier = findById(universalIdentifierId);
        universalIdentifierRepository.deleteById(universalIdentifierId);
        universalIdentifierRepository.flush();
        return universalIdentifier;
    }

    public UniversalIdentifier findById(Integer id) {
        return universalIdentifierRepository.findById(id).orElse(null);
    }

    public String getNextNumber(String variable) {
        UniversalIdentifier universalIdentifier = universalIdentifierRepository.findByVariable(variable);
        // Check if we already reaches the maximum number allowed
        int maxNumber = (int)Math.pow(10, universalIdentifier.getLength());
        int nextNumber = universalIdentifier.getCurrentNumber() + 1;
        if (nextNumber > maxNumber && !universalIdentifier.getRollover()) {
            throw new GenericException(0, variable + " already reached the maximum number allowed and not supposed to be rolled over");
        }
        else if (nextNumber > maxNumber) {
            // next number is bigger than the maximum number allowed but
            // we allow roll over. So start from 0 again
            nextNumber = 0;
        }

        universalIdentifier.setCurrentNumber(nextNumber);

        save(universalIdentifier);
        return universalIdentifier.getPrefix() + String.format("%0" + universalIdentifier.getLength() +"d", nextNumber) + universalIdentifier.getPostfix();
    }

    public List<UniversalIdentifier> findUniversalIdentifiers(Map<String, String> criteriaList) {
        return universalIdentifierRepository.findAll(new Specification<UniversalIdentifier>() {
            @Override
            public Predicate toPredicate(Root<UniversalIdentifier> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("variable") && !criteriaList.get("variable").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("variable"), criteriaList.get("variable")));
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public UniversalIdentifier findByVarialbe(String variable) {
        UniversalIdentifier universalIdentifier = universalIdentifierRepository.findByVariable(variable);
        return universalIdentifier;
    }

    public UniversalIdentifier createUniversalIdentifier(String variable,
                                                         Boolean rollover,
                                                         String prefix,
                                                         String postfix,
                                                         Integer length,
                                                         Integer currentNumber) {
        UniversalIdentifier universalIdentifier = new UniversalIdentifier();
        universalIdentifier.setVariable(variable);
        universalIdentifier.setRollover(rollover);
        universalIdentifier.setPrefix(prefix);
        universalIdentifier.setPostfix(postfix);
        universalIdentifier.setLength(length);

        universalIdentifier.setCurrentNumber(currentNumber);
        return save(universalIdentifier);

    }
    public UniversalIdentifier editUniversalIdentifier(Integer id,
                                                       String variable,
                                                       Boolean rollover,
                                                       String prefix,
                                                       String postfix,
                                                       Integer length,
                                                       Integer currentNumber) {
        UniversalIdentifier universalIdentifier = findById(id);
        universalIdentifier.setVariable(variable);
        universalIdentifier.setRollover(rollover);
        universalIdentifier.setPrefix(prefix);
        universalIdentifier.setPostfix(postfix);
        universalIdentifier.setLength(length);

        universalIdentifier.setCurrentNumber(currentNumber);

        return save(universalIdentifier);

    }
    public UniversalIdentifier deleteUniversalIdentifier(Integer id) {

        return deleteUniversalIdentifierById(id);

    }



}
