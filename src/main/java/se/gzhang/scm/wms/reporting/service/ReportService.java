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
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import se.gzhang.scm.wms.authorization.service.UserService;
import se.gzhang.scm.wms.reporting.model.Report;
import se.gzhang.scm.wms.reporting.model.ReportArchive;
import se.gzhang.scm.wms.reporting.repository.ReportRepository;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    DataSource dataSource;

    @Autowired
    UserService userService;

    @Autowired
    ReportArchiveService reportArchiveService;

    @Autowired
    ReportTriggerPointService reportTriggerPointService;

    private final String REPORT_TEMPLATE_DIRECTORY = "static/reports";
    private final String REPORT_ARCHIVE_DIRECTORY = "D:\\tmp\\reporting\\archive";



    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public Report findByReportId(int reportID){
        return reportRepository.findById(reportID);
    }

    public List<Report> findByReportName(String reportName){
        return reportRepository.findByName(reportName);
    }

    public List<ReportArchive> printReport(String triggerPoint, Map<String, Object> reportParameters)
            throws IOException, SQLException, JRException {

        List<ReportArchive> reportArchiveList = new ArrayList<>();

        List<Report> reportList = reportTriggerPointService.findReportsByTriggerPoint(triggerPoint);
        for(Report report : reportList) {
            ReportArchive reportArchive = printReport(report, reportParameters);
            reportArchiveList.add(reportArchive);
        }
        return reportArchiveList;

    }

    public ReportArchive printReport(Report report, Map<String, Object> reportParameters)
        throws IOException, SQLException, JRException {

        // Use ClassPathResource to get the report's template
        ClassPathResource resource = new ClassPathResource(REPORT_TEMPLATE_DIRECTORY + "/" + report.getLayoutFile());
        InputStream reportFileInputStream = resource.getInputStream();

        // Fill the data to the report
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        reportFileInputStream,
                        reportParameters,
                        dataSource.getConnection()
                );

        String[] reportFileInfo;
        switch (report.getReportType()) {
            case PDF:
                reportFileInfo = printPDF(report, jasperPrint);
                break;
            case HTML:
                reportFileInfo = printHTML(report, jasperPrint);
                break;
            default:
                // do nothing
                reportFileInfo = new String[]{"",""};
        }

        return archiveReport(report, reportParameters, reportFileInfo);

    }

    // Export the jasper report to PDF format
    private String[] printPDF(Report report, JasperPrint jasperPrint)
        throws IOException, JRException{
        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        String archivePDFFileName;
        String archivePDFFilePath;

        File pdfFile;
        do {
            archivePDFFileName =  getArchiveFileName(report.getName()) + ".pdf";
            archivePDFFilePath = REPORT_ARCHIVE_DIRECTORY + "\\pdf\\" + archivePDFFileName;

            pdfFile = new File(archivePDFFilePath);
        }
        while(pdfFile.exists());

        pdfFile.createNewFile();

        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(pdfFile));

        SimplePdfReportConfiguration reportConfig
                = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);

        SimplePdfExporterConfiguration exportConfig
                = new SimplePdfExporterConfiguration();
        exportConfig.setMetadataAuthor("GZ");
        // exportConfig.setEncrypted(true);
        exportConfig.setAllowedPermissionsHint("PRINTING");

        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);

        exporter.exportReport();

        return new String[]{archivePDFFilePath, archivePDFFileName};
    }

    // Export the jasper report to HTML format
    private String[] printHTML(Report report, JasperPrint jasperPrint)
            throws IOException, JRException{

        HtmlExporter htmlExporter = new HtmlExporter();

        htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));

        String archiveHTMLFileName;
        String archiveHTMLFilePath;

        File htmlFile;
        do {
            archiveHTMLFileName =  getArchiveFileName(report.getName()) + ".html";
            archiveHTMLFilePath = REPORT_ARCHIVE_DIRECTORY + "\\html\\" + archiveHTMLFileName;

            htmlFile = new File(archiveHTMLFilePath);
        }
        while(htmlFile.exists());

        htmlFile.createNewFile();

        htmlExporter.setExporterOutput(
                new SimpleHtmlExporterOutput(htmlFile));

        htmlExporter.exportReport();

        return new String[]{archiveHTMLFilePath, archiveHTMLFileName};
    }

    // Generate archive file name based on
    // reportName + YYYYMMDDHHmmSS + 5 digit random number
    private String getArchiveFileName(String reportName) {
        // Get random string from UUID, first 5 characters
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String randomString = uuid.substring(0, 5);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        return reportName + "_" +
                simpleDateFormat.format(new Date()) + "_" +
                randomString;

    }

    // archiveFileInfo: String array that always contains only 2 elements.
    //           1. Archive file full path including the file name
    //           2. Archive file name
    private ReportArchive archiveReport(Report report, Map<String, Object> reportParameters, String[] archiveFileInfo) {
        ReportArchive reportArchive = new ReportArchive();
        reportArchive.setFilePath(archiveFileInfo[0]);
        reportArchive.setFileName(archiveFileInfo[1]);
        reportArchive.setPrintedDate(new Date());
        reportArchive.setReport(report);
        reportArchive.setPrintedUser(userService.getCurrentLoginUser());
        if(reportParameters.containsKey("key1")) {
            reportArchive.setKey1(reportParameters.get("key1").toString());
        }
        if(reportParameters.containsKey("key2")) {
            reportArchive.setKey2(reportParameters.get("key2").toString());
        }

        if(reportParameters.containsKey("key3")) {
            reportArchive.setKey3(reportParameters.get("key3").toString());
        }

        if(reportParameters.containsKey("key4")) {
            reportArchive.setKey4(reportParameters.get("key4").toString());
        }

        if(reportParameters.containsKey("key5")) {
            reportArchive.setKey5(reportParameters.get("key5").toString());
        }
        return reportArchiveService.save(reportArchive);
    }

}
