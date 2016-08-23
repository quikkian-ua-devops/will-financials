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
package org.kuali.kfs.module.tem.batch;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad;
import org.kuali.kfs.module.tem.batch.service.PerDiemFileParsingService;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadService;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadValidationService;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class PerDiemTxtInputFileType extends BatchInputFileTypeBase {
    private static Logger LOG = Logger.getLogger(PerDiemTxtInputFileType.class);

    private PerDiemFileParsingService perDiemFileParsingService;
    private DateTimeService dateTimeService;

    private String fileNamePrefix;
    private String deliminator;
    private List<String> perDiemFieldsToPopulate;

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(java.lang.String, java.lang.Object, java.lang.String)
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
        StringBuilder fileName = new StringBuilder();

        fileUserIdentifier = StringUtils.deleteWhitespace(fileUserIdentifier);
        fileUserIdentifier = StringUtils.remove(fileUserIdentifier, TemConstants.FILE_NAME_PART_DELIMITER);

        fileName.append(this.getFileNamePrefix()).append(TemConstants.FILE_NAME_PART_DELIMITER);
        fileName.append(principalName).append(TemConstants.FILE_NAME_PART_DELIMITER);
        fileName.append(fileUserIdentifier).append(TemConstants.FILE_NAME_PART_DELIMITER);

        fileName.append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));

        return fileName.toString();
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return TemConstants.PER_DIEM_INPUT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));

        List<PerDiemForLoad> perDiemList = this.getPerDiemFileParsingService().buildPerDiemsFromFlatFile(reader, this.getDeliminator(), this.getPerDiemFieldsToPopulate());

        PerDiemLoadService perDiemLoadService = SpringContext.getBean(PerDiemLoadService.class);
        perDiemList = perDiemLoadService.updatePerDiem(perDiemList);

        return perDiemList;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {

    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        PerDiemLoadValidationService perDiemLoadValidationService = SpringContext.getBean(PerDiemLoadValidationService.class);
        List<PerDiemForLoad> perDiemList = (List<PerDiemForLoad>) parsedFileContents;

        return perDiemLoadValidationService.validate(perDiemList);
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getAuthorPrincipalName(java.io.File)
     */
    @Override
    public String getAuthorPrincipalName(File file) {
        return StringUtils.substringBetween(file.getName(), this.getFileNamePrefix(), TemConstants.FILE_NAME_PART_DELIMITER);
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return TemKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PER_DIEM_FILE;
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the fileNamePrefix attribute.
     *
     * @return Returns the fileNamePrefix.
     */
    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    /**
     * Sets the fileNamePrefix attribute value.
     *
     * @param fileNamePrefix The fileNamePrefix to set.
     */
    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    /**
     * Gets the perDiemFileParsingService attribute.
     *
     * @return Returns the perDiemFileParsingService.
     */
    public PerDiemFileParsingService getPerDiemFileParsingService() {
        return perDiemFileParsingService;
    }

    /**
     * Sets the perDiemFileParsingService attribute value.
     *
     * @param perDiemFileParsingService The perDiemFileParsingService to set.
     */
    public void setPerDiemFileParsingService(PerDiemFileParsingService perDiemFileParsingService) {
        this.perDiemFileParsingService = perDiemFileParsingService;
    }

    /**
     * Gets the deliminator attribute.
     *
     * @return Returns the deliminator.
     */
    public String getDeliminator() {
        return deliminator;
    }

    /**
     * Sets the deliminator attribute value.
     *
     * @param deliminator The deliminator to set.
     */
    public void setDeliminator(String deliminator) {
        this.deliminator = deliminator;
    }

    /**
     * Gets the perDiemFieldsToPopulate attribute.
     *
     * @return Returns the perDiemFieldsToPopulate.
     */
    public List<String> getPerDiemFieldsToPopulate() {
        return perDiemFieldsToPopulate;
    }

    /**
     * Sets the perDiemFieldsToPopulate attribute value.
     *
     * @param perDiemFieldsToPopulate The perDiemFieldsToPopulate to set.
     */
    public void setPerDiemFieldsToPopulate(List<String> perDiemFieldsToPopulate) {
        this.perDiemFieldsToPopulate = perDiemFieldsToPopulate;
    }
}
