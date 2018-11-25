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

package se.gzhang.scm.wms.system.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.system.tools.model.FileUploadOption;
import se.gzhang.scm.wms.system.tools.model.FileUploadProcess;
import se.gzhang.scm.wms.system.tools.service.FileUploadOptionService;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class FileUploadOptionController {

    private static final String APPLICATION_ID = "SystemTools";
    private static final String FORM_ID = "FileUpload";
    @Autowired
    FileUploadOptionService fileUploadOptionService;


    @RequestMapping(value="/systemtool/fileupload", method = RequestMethod.GET)
    public ModelAndView listFileUploadOptions() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("systemtool/fileupload");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/systemtool/fileupload/list")
    public WebServiceResponseWrapper queryFileUploadOptions(@RequestParam Map<String, String> parameters) {

        List<FileUploadOption> fileUploadOptionList = fileUploadOptionService.findFileUploadOptions(parameters);
        return new WebServiceResponseWrapper<List<FileUploadOption>>(0, "", fileUploadOptionList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/systemtool/fileupload/upload/{name}")
    public WebServiceResponseWrapper uploadFile(@PathVariable("name") String fileUploadOptionName,
                                                @RequestParam("file") MultipartFile multipartFile,
                                                @RequestParam(value = "processID", required = false) String processID) {
        System.out.println("start upload file with process: " + processID);
        BufferedReader br;
        List<String> fileContent = new ArrayList<>();
        int lineNumber = 0;
        try {
            String line;
            InputStream is = multipartFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                fileContent.add(line);
                lineNumber++;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        fileUploadOptionService.loadFile(fileContent , fileUploadOptionName, processID);

        return new WebServiceResponseWrapper<FileUploadProcess>(0, "", fileUploadOptionService.getFileUploadProgress(processID));
    }

    @ResponseBody
    @RequestMapping(value="/ws/systemtool/fileupload/upload/progress/{processID}")
    public WebServiceResponseWrapper getFileUploadProcess(@PathVariable("processID") String processID) {
        FileUploadProcess fileUploadProcess = fileUploadOptionService.getFileUploadProgress(processID);
        return new WebServiceResponseWrapper<FileUploadProcess>(0, "", fileUploadProcess);


    }
    @ResponseBody
    @RequestMapping(value="/ws/systemtool/fileupload/upload/progress/new")
    public WebServiceResponseWrapper startUploadProcess() {
        String processID = UUID.randomUUID().toString();
        // fileUploadOptionService.getFileUploadProgress will create a new process if it doesn't exists yet
        // and regiest it in a map
        FileUploadProcess fileUploadProcess =  fileUploadOptionService.getFileUploadProgress(processID);
        System.out.println("## Initial process with ID: " + processID);
        return new WebServiceResponseWrapper<FileUploadProcess>(0, "", fileUploadProcess);


    }
}
