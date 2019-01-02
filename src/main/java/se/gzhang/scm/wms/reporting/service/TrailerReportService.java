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

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.reporting.model.ReportArchive;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Service
public class TrailerReportService {

    @Autowired
    ReportService reportService;

    // print receiving document. It supposed to be able to accept
    // the trailerID as the only parameter and print the receiving document
    // for the whole trailer
    public List<ReportArchive> printReceivingDocument(Trailer trailer)
            throws IOException, SQLException, JRException {
        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("trailerID", trailer.getId());
        reportParameters.put("key1", trailer.getId());
        reportParameters.put("key2", trailer.getTrailerNumber());
        reportParameters.put("key3", trailer.getLicensePlate());
        reportParameters.put("key4", trailer.getDriver());
        reportParameters.put("key5", (trailer.getCarrier() != null ? trailer.getCarrier().getName() : ""));
        return reportService.printReport("RECEIVING_TRAILER_DOCUMENT", reportParameters);
    }

}
