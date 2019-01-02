/**
 * Copyright 2019
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

package se.gzhang.scm.wms.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se.gzhang.scm.wms.reporting.model.ReportArchive;
import se.gzhang.scm.wms.reporting.model.ReportType;
import se.gzhang.scm.wms.reporting.service.ReportArchiveService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ReportController {

    @Autowired
    ReportArchiveService reportArchiveService;

    @RequestMapping(value="/reporting/archive/{id}", method= RequestMethod.GET)
    public ResponseEntity<byte[]> displayReport(@PathVariable("id") int reportArchiveID){

        ReportArchive reportArchive = reportArchiveService.findByReportArchiveId(reportArchiveID);

        if (reportArchive == null) {
            return getErrorResponseEntity("Can't find report archive by id:" + reportArchiveID);
        }
        try {
            switch (reportArchive.getReport().getReportType()) {
                case PDF:
                    return displayPDFReport(reportArchive);
                default:
                    return getErrorResponseEntity("Type: " + reportArchive.getReport().getReportType() + " is not supported at this moment");
            }
        }
        catch(IOException ioException) {
            return getErrorResponseEntity(ioException);
        }
    }

    private ResponseEntity<byte[]> displayPDFReport(ReportArchive reportArchive)
            throws IOException {

        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.parseMediaType("application/pdf"));

        String pdfFilename = reportArchive.getFileName();

        Path pdfPath = Paths.get(reportArchive.getFilePath());
        byte[] pdfFileContent = Files.readAllBytes(pdfPath);

        responseHeaders.add("content-disposition", "inline;filename=" + pdfFilename);

        responseHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfFileContent, responseHeaders, HttpStatus.OK);
        return response;
    }

    private ResponseEntity<byte[]> getErrorResponseEntity(Exception exception) {
        String errorMessage = "Error while retrieve the report:" +
                "Error Type: " + exception.getClass() + ", " +
                "Error Message: " + exception.getMessage();

        return getErrorResponseEntity(errorMessage);
    }

    private ResponseEntity<byte[]> getErrorResponseEntity(String errorMessage) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        byte[] message = errorMessage.getBytes();
        return new ResponseEntity<byte[]>(message, responseHeaders, HttpStatus.CREATED);
    }
}
