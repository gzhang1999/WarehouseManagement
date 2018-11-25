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
import se.gzhang.scm.wms.inventory.model.ItemFootprint;
import se.gzhang.scm.wms.inventory.model.ItemFootprintUOM;
import se.gzhang.scm.wms.inventory.service.ItemBarcodeService;
import se.gzhang.scm.wms.inventory.service.ItemFootprintService;
import se.gzhang.scm.wms.inventory.service.ItemFootprintUOMService;
import se.gzhang.scm.wms.inventory.service.ItemService;
import se.gzhang.scm.wms.layout.service.LocationService;
import se.gzhang.scm.wms.system.tools.model.FileUploadOption;
import se.gzhang.scm.wms.system.tools.model.FileUploadProcess;
import se.gzhang.scm.wms.system.tools.repository.FileUploadOptionRepository;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class FileUploadOptionService {

    @Autowired
    FileUploadOptionRepository fileUploadOptionRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemFootprintService itemFootprintService;

    @Autowired
    ItemFootprintUOMService itemFootprintUOMService;
    @Autowired
    ItemBarcodeService itemBarcodeService;

    private static final int  MAX_PROCESS_COUNT = 999;
    // Max kept days: 5 days = 432000 seconds
    private static final int  MAX_KEEP_SECONDS = 432000;

    // A map to track the upload progress. Each upload attempt
    // will be assigned with a unique ID
    private Map<String, FileUploadProcess> fileUploadProcessHashMap = new HashMap<>();


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
        // Setup the process with the current total number of record in the file
        initialFileUploadProcess(processID, fileContent.size());

        System.out.println("## File Upload Process / Set total number count" );
        System.out.println("## " + getFileUploadProgress(processID));

        System.out.println("fileContent.size(): " + fileContent.size());

        if(optionName.equalsIgnoreCase("Location")) {
            locationService.loadFromFile(columnName,fileContent, processID);
        }
        else if(optionName.equalsIgnoreCase("Item")) {
            itemService.loadFromFile(columnName,fileContent, processID);
        }
        else if(optionName.equalsIgnoreCase("ItemFootprint")) {
            itemFootprintService.loadFromFile(columnName,fileContent, processID);
        }
        else if(optionName.equalsIgnoreCase("ItemFootprintUOM")) {
            itemFootprintUOMService.loadFromFile(columnName,fileContent, processID);
        }
        else if(optionName.equalsIgnoreCase("ItemBarcode")) {
            itemBarcodeService.loadFromFile(columnName,fileContent, processID);
        }
        getFileUploadProgress(processID).markFinished();
    }

    public FileUploadProcess getFileUploadProgress(String processID) {
        if (fileUploadProcessHashMap.containsKey(processID)) {
            return fileUploadProcessHashMap.get(processID);
        }
        else {
            FileUploadProcess fileUploadProcess = new FileUploadProcess(processID);
            fileUploadProcessHashMap.put(processID, fileUploadProcess);
            // When we have too many process instance in the map, we will
            // clear the older process that already finished
            if (fileUploadProcessHashMap.size() >= MAX_PROCESS_COUNT) {
                clearFileUploadProcessHashMap();
            }
            return fileUploadProcess;
        }
    }

    public void initialFileUploadProcess(String processID, int totalRecordCount) {
        FileUploadProcess fileUploadProcess = getFileUploadProgress(processID);
        fileUploadProcess.setTotalRecordCount(totalRecordCount);
        fileUploadProcess.setCurrentRecordNumber(0);

    }
    public void setFileUploadProgress(String processID, int currentRecordNumber) {
        FileUploadProcess fileUploadProcess = getFileUploadProgress(processID);
        fileUploadProcess.setCurrentRecordNumber(currentRecordNumber);

    }

    // When we loaded one record, this function will add one
    // to the current Record Number and success(or fail) record number
    public void increaseRecordNumberLoaded(String processID, boolean successful) {
        FileUploadProcess fileUploadProcess = getFileUploadProgress(processID);
        fileUploadProcess.setCurrentRecordNumber(fileUploadProcess.getCurrentRecordNumber() + 1);
        if (successful) {
            fileUploadProcess.setSuccessfullyLoadedRecordNumber(fileUploadProcess.getSuccessfullyLoadedRecordNumber() + 1);
        }
        else  {
            fileUploadProcess.setFailLoadedRecordNumber(fileUploadProcess.getFailLoadedRecordNumber() + 1);
        }
    }

    private void clearFileUploadProcessHashMap() {
        for(Iterator<Map.Entry<String, FileUploadProcess>> iterator = fileUploadProcessHashMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, FileUploadProcess> entry = iterator.next();
            if(entry.getValue().isFinished() && entry.getValue().secondsSinceFinished() > MAX_KEEP_SECONDS) {
                iterator.remove();
            }
        }
    }

}
