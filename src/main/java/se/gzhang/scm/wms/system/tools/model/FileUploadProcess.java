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

package se.gzhang.scm.wms.system.tools.model;

import java.util.Date;

// Class to keep track of each file upload process
// We will save some meta data in this class
public class FileUploadProcess {
    private String id;

    private int totalRecordCount;

    private int currentRecordNumber;

    private int successfullyLoadedRecordNumber;

    private int failLoadedRecordNumber;

    private Date finishedDate;
    private Date startedDate;

    public FileUploadProcess(String id) {
        this.id = id;
        totalRecordCount = 0;
        currentRecordNumber = 0;
        successfullyLoadedRecordNumber = 0;
        failLoadedRecordNumber = 0;
        startedDate = new Date();
    }

    public  FileUploadProcess(String id, int totalRecordCount) {
        this.id = id;
        this.totalRecordCount = totalRecordCount;
        currentRecordNumber = 0;
        successfullyLoadedRecordNumber = 0;
        failLoadedRecordNumber = 0;
        startedDate = new Date();
    }

    public void markFinished() {
        finishedDate = new Date();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        if (finishedDate == null) {
            return  " process (" + id + ") " +
                    ", started @ " + startedDate +
                    ", finished @ NOT-FINISHED-YET " +
                    ", total number of lines: " + totalRecordCount +
                    ", loaded: " + currentRecordNumber +
                    ", successfully loaded: " + successfullyLoadedRecordNumber +
                    ", fail loaded: " + failLoadedRecordNumber;
        }
        else {
            return  " process (" + id + ") " +
                    ", started @ " + startedDate +
                    ", finished @ " + finishedDate +
                    ", total number of lines: " + totalRecordCount +
                    ", loaded: " + currentRecordNumber +
                    ", successfully loaded: " + successfullyLoadedRecordNumber +
                    ", fail loaded: " + failLoadedRecordNumber;
        }
    }

    public boolean isFinished() {
        return finishedDate != null;
    }

    // Age since it is started, in seconds;
    public long age() {

        Date now = new Date();
        return (now.getTime() - startedDate.getTime())/1000;
    }

    // how many seconds passed since the process is marked as finished
    public long secondsSinceFinished() {
        if (!isFinished()) {
            return -1;
        }

        Date now = new Date();
        return (now.getTime() - finishedDate.getTime())/1000;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public int getCurrentRecordNumber() {
        return currentRecordNumber;
    }

    public void setCurrentRecordNumber(int currentRecordNumber) {
        this.currentRecordNumber = currentRecordNumber;
    }

    public int getSuccessfullyLoadedRecordNumber() {
        return successfullyLoadedRecordNumber;
    }

    public void setSuccessfullyLoadedRecordNumber(int successfullyLoadedRecordNumber) {
        this.successfullyLoadedRecordNumber = successfullyLoadedRecordNumber;
    }

    public int getFailLoadedRecordNumber() {
        return failLoadedRecordNumber;
    }

    public void setFailLoadedRecordNumber(int failLoadedRecordNumber) {
        this.failLoadedRecordNumber = failLoadedRecordNumber;
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    public Date getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
    }
}
