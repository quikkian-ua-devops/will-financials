/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.batch.service.DataReportService;
import org.kuali.kfs.module.tem.util.MessageUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.BusinessObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataReportServiceImpl implements DataReportService {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataReportServiceImpl.class);

    public final static String REPORT_FILE_NAME_PATTERN = "{0}/{1}_{2}{3}";

    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.tem.batch.service.DataReportService#writeToReport(java.io.PrintStream, org.kuali.kfs.module.tem.businessobject.AgencyStagingData, java.lang.String, org.kuali.kfs.sys.report.BusinessObjectReportHelper)
     */
    @Override
    public <T extends BusinessObject> void writeToReport(PrintStream reportDataStream, T tableData, List<ErrorMessage> errors, BusinessObjectReportHelper reportHelper) {
        String reportEntry = formatMessage(tableData, getMessageAsString(errors), reportHelper);
        reportDataStream.println(reportEntry);
    }

    /**
     * Return a String for the report
     * <p>
     * Using the report helper to display the table header , and then the table data values
     * If the errors string is not empty, additional errors (formated) will be displayed under the table
     *
     * @param tableData
     * @param errors
     * @param reportHelper
     * @return
     */
    private <T extends BusinessObject> String formatMessage(T tableData, String errors, BusinessObjectReportHelper reportHelper) {
        StringBuilder body = new StringBuilder();
        Map<String, String> tableDefinition = new LinkedHashMap<String, String>();
        List<String> propertyList = new ArrayList<String>();
        tableDefinition = reportHelper.getTableDefinition();
        propertyList = reportHelper.getTableCellValues(tableData, false);

        String tableCellFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY);
        String fieldLine = String.format(tableCellFormat, propertyList.toArray());
        //create the table fields
        body.append(fieldLine);
        body.append(BusinessObjectReportHelper.LINE_BREAK);

        if (StringUtils.isNotEmpty(errors)) {
            //append the error messages
            body.append("**** ERROR(S): **** ")
                .append(BusinessObjectReportHelper.LINE_BREAK)
                .append(errors)
                .append(BusinessObjectReportHelper.LINE_BREAK);
        }
        return body.toString();
    }

    /**
     * @param reportDataStream
     * @param fileName
     * @param importBy
     */
    @Override
    public void writeReportHeader(PrintStream reportDataStream, String fileName, String reportHeader, BusinessObjectReportHelper reportHelper) {
        StringBuilder header = new StringBuilder();
        header.append(MessageBuilder.buildMessageWithPlaceHolder(reportHeader, BusinessObjectReportHelper.LINE_BREAK, fileName));
        header.append(BusinessObjectReportHelper.LINE_BREAK);
        header.append(BusinessObjectReportHelper.LINE_BREAK);
        header.append(BusinessObjectReportHelper.LINE_BREAK);

        Map<String, String> tableDefinition = reportHelper.getTableDefinition();
        String tableHeaderFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY);
        header.append(tableHeaderFormat);
        reportDataStream.print(header);
    }

    /**
     * get print stream for report
     */
    @Override
    public PrintStream getReportPrintStream(String directory, String filePrefix) {
        String dateTime = dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentSqlDate());
        String reportFileName = MessageFormat.format(REPORT_FILE_NAME_PATTERN, directory, filePrefix, dateTime, TemConstants.TEXT_FILE_SUFFIX);

        File outputfile = new File(reportFileName);

        try {
            return new PrintStream(outputfile);
        } catch (FileNotFoundException e) {
            String errorMessage = "Cannot find the output file: " + reportFileName;
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.DataReportService#getMessageAsString(java.util.List)
     */
    @Override
    public String getMessageAsString(List<ErrorMessage> errorMessages) {

        List<String> messageList = new ArrayList<String>();
        for (ErrorMessage error : errorMessages) {
            messageList.add(MessageUtils.getErrorMessage(error));
        }
        StrBuilder builder = new StrBuilder();
        builder.appendWithSeparators(messageList, BusinessObjectReportHelper.LINE_BREAK);
        return builder.toString();
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
