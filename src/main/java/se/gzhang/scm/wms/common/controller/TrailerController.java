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

package se.gzhang.scm.wms.common.controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.model.EnumWithDescription;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.service.CarrierService;
import se.gzhang.scm.wms.common.service.TrailerService;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.reporting.model.ReportArchive;
import se.gzhang.scm.wms.webservice.model.WebServiceResponseWrapper;

import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TrailerController {
    @Autowired
    private TrailerService trailerService;
    @Autowired
    private CarrierService carrierService;

    private static final String APPLICATION_ID = "Common";
    private static final String FORM_ID = "Trailer";


    @RequestMapping(value="/common/trailer", method = RequestMethod.GET)
    public ModelAndView listTrailers() {
        ModelAndView modelAndView = new ModelAndView();

        // Test for enum
        String enumName = "se.gzhang.scm.wms.common.model.TrailerState";

        modelAndView.addObject("applicationID",APPLICATION_ID);
        modelAndView.addObject("formID",FORM_ID);

        modelAndView.setViewName("common/trailer");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/list")
    public WebServiceResponseWrapper queryTrailers(@RequestParam Map<String, String> parameters) {

        System.out.println("Start to find trailer with:");
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            System.out.println("key: " + entry.getKey() + " / value: " + entry.getValue());
        }
        List<Trailer> trailerList = trailerService.findTrailers(parameters);
        System.out.println("find " + trailerList.size() + " from the query");
        return new WebServiceResponseWrapper<List<Trailer>>(0, "", trailerList);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/query/{id}")
    public WebServiceResponseWrapper getTrailer(@PathVariable("id") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }
        return new WebServiceResponseWrapper<Trailer>(0, "", trailer);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/{id}/delete")
    public WebServiceResponseWrapper deleteTrailer(@PathVariable("id") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }
        try {
            trailerService.deleteByTrailerID(trailerID);
            return new WebServiceResponseWrapper<Trailer>(0, "", trailer);
        }
        catch(GenericException ex) {

            return WebServiceResponseWrapper.raiseError(ex.getCode(), ex.getMessage());
        }
    }
    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/{id}/void")
    public WebServiceResponseWrapper voidTrailer(@PathVariable("id") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }
        try {
            Trailer newTrailer = trailerService.voidByTrailerID(trailerID);
            return new WebServiceResponseWrapper<Trailer>(0, "", newTrailer);
        }
        catch(GenericException ex) {

            return WebServiceResponseWrapper.raiseError(ex.getCode(), ex.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/new")
    public WebServiceResponseWrapper createTrailer(@RequestParam("trailerType") String trailerType,
                                                   @RequestParam(name = "trailerNumber", required = false) String trailerNumber,
                                                   @RequestParam(name = "driver", required = false) String driver,
                                                   @RequestParam(name = "driverTelephone", required = false) String driverTelephone,
                                                   @RequestParam(name = "licensePlate", required = false) String licensePlate,
                                                   @RequestParam(name = "carrier", required = false) Integer carrierID) {

        Trailer newTrailer = null;
        if (carrierID != null) {
            // if carrierID is passed in, make sure it is a valid carrier id
            Carrier carrier = carrierService.findByCarrierId(carrierID);
            if (carrier == null) {
                return WebServiceResponseWrapper.raiseError(10000, "Can't find the carrier by id: " + carrierID);
            }
            newTrailer = trailerService.createTrailer(trailerType,trailerNumber,driver,driverTelephone,licensePlate,carrier);
        }
        else {

            newTrailer = trailerService.createTrailer(trailerType,trailerNumber,driver,driverTelephone,licensePlate);
        }
        return new WebServiceResponseWrapper<Trailer>(0, "", newTrailer);
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/{id}/edit")
    public WebServiceResponseWrapper changeTrailer(@PathVariable("id") int trailerID,
                                                   @RequestParam(name = "trailer_type", required = false) String trailerType,
                                                   @RequestParam(name = "driver", required = false) String driver,
                                                   @RequestParam(name = "driverTelephone", required = false) String driverTelephone,
                                                   @RequestParam(name = "licensePlate", required = false) String licensePlate,
                                                   @RequestParam(name = "carrier", required = false) Integer carrierID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }

        Trailer newTrailer = null;
        if (carrierID != null) {
            // if carrierID is passed in, make sure it is a valid carrier id
            Carrier carrier = carrierService.findByCarrierId(carrierID);
            if (carrier == null) {
                return WebServiceResponseWrapper.raiseError(10000, "Can't find the carrier by id: " + carrierID);
            }
            newTrailer = trailerService.changeTrailer(trailer, trailerType,driver,driverTelephone,licensePlate,carrier);
        }
        else {

            newTrailer = trailerService.changeTrailer(trailer, trailerType,driver,driverTelephone,licensePlate);
        }
        return new WebServiceResponseWrapper<Trailer>(0, "", newTrailer);
    }


    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/{id}/checkin")
    public WebServiceResponseWrapper checkInTrailer(@PathVariable("id") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }

        return new WebServiceResponseWrapper<Trailer>(0, "", trailerService.checkInTrailer(trailer));
    }

    @ResponseBody
    @RequestMapping(value="/ws/common/trailer/{id}/report")
    public WebServiceResponseWrapper displayTrailerReport (@PathVariable("id") int trailerID) {

        Trailer trailer = trailerService.findByTrailerId(trailerID);
        if (trailer == null) {
            return WebServiceResponseWrapper.raiseError(10000, "Can't find the trailer by id: " + trailerID);
        }
        try {
            List<ReportArchive> reportArchiveList = trailerService.printReport(trailer);
            return new WebServiceResponseWrapper<List<ReportArchive>>(0, "", reportArchiveList);
        }
        catch(IOException ioException) {
            return WebServiceResponseWrapper.raiseError(10000, "IOException while print report for : " +
                    "id: " + trailerID + " / number: " + trailer.getTrailerNumber() +
                    ", IOException: " + ioException.getMessage());

        }
        catch(SQLException sqlException) {
            return WebServiceResponseWrapper.raiseError(10000, "IOException while print report for : " +
                    "id: " + trailerID + " / number: " + trailer.getTrailerNumber() +
                    ", SQLException: " + sqlException.getMessage());

        }
        catch(JRException jrException){
            return WebServiceResponseWrapper.raiseError(10000, "IOException while print report for : " +
                    "id: " + trailerID + " / number: " + trailer.getTrailerNumber() +
                    ", JRException: " + jrException.getMessage());

        }

    }

}
