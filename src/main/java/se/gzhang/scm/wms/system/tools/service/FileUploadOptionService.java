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

package se.gzhang.scm.wms.system.tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.system.tools.model.FileUploadOption;
import se.gzhang.scm.wms.system.tools.repository.FileUploadOptionRepository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileUploadOptionService {

    @Autowired
    FileUploadOptionRepository fileUploadOptionRepository;

    @Autowired
    LocationService locationService;

    // A map to track the upload progress. Each upload attempt
    // will be assigned with a unique ID
    private Map<String, Integer> uploadProgress = new HashMap<>();


    public List<FileUploadOption> findAll() {
        return fileUploadOptionRepository.findAll();
    }

    public FileUploadOption fineByFileUploadOptionID(int id) {
        return fileUploadOptionRepository.findById(id);
    }

    public FileUploadOption save(FileUploadOption fileUploadOption) {
        FileUploadOption newFileUploadOption = fileUploadOptionRepository.save(fileUploadOption);
        fileUploadOptionRepository.flush();
        return newFileUploadOption;
    }


    public FileUploadOption fineByFileUploadOptionName(String name) {

        return fileUploadOptionRepository.findByName(name);

    }

    public void deleteByFileUploadOptionID(int id) {

        fileUploadOptionRepository.deleteById(id);

    }

    public List<FileUploadOption> findFileUploadOptions(Map<String, String> criteriaList) {
        System.out.println("Find item with following criteria");
        for(Map.Entry<String, String> entry : criteriaList.entrySet()) {
            System.out.println("name: " + entry.getKey() + " , value: " + entry.getValue());
        }
        return fileUploadOptionRepository.findAll(new Specification<FileUploadOption>() {
            @Override
            public Predicate toPredicate(Root<FileUploadOption> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }
                if(criteriaList.containsKey("name") && !criteriaList.get("name").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), criteriaList.get("name")));
                }
                if(criteriaList.containsKey("description") && !criteriaList.get("description").isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("description"), criteriaList.get("description")));
                }
                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    public void loadFile(List<String> fileContent, String optionName, String processID) {
        if (fileContent.size() <= 1) {
            // The file must as least contains more than 1 row
            // while the first row is always the column name
            return;
        }
        String[] columnName = fileContent.get(0).split(",");
        System.out.println("fileContent.size(): " + fileContent.size());
        // Remove first row so we will only have location information in the content
        fileContent.remove(0);
        System.out.println("fileContent.size(): " + fileContent.size());

        if(optionName.equalsIgnoreCase("Location")) {

            locationService.loadFromFile(columnName,fileContent, processID);
        }
    }

    public int getUploadProgress(String processID) {
        if (uploadProgress.containsKey(processID)) {
            return uploadProgress.get(processID);
        }
        else {
            uploadProgress.put(processID, 0);
            return 0;
        }
    }

    public void setUploadProgress(String processID, int progress) {
        uploadProgress.put(processID,progress);
    }


}
