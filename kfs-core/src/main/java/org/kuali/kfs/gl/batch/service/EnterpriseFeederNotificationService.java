/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.gl.batch.service.impl.EnterpriseFeederStatus;
import org.kuali.kfs.sys.Message;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * A service that is used to provide notification about the status of an enterprise feed. The implementation may use a variety of
 * other services to perform notification, ranging from simply logging data to sending emails, etc.
 */
public interface EnterpriseFeederNotificationService {
    /**
     * Performs notification about the status of the upload (i.e. feeding) of a single file set (i.e. done file, data file, and
     * recon file).
     *
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                          feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                          value.
     * @param event             The event/status of the upload of the file set
     * @param doneFile          The done file
     * @param dataFile          The data file
     * @param reconFile         The recon file
     * @param errorMessages     Any error messages for which to provide notification
     */
    public void notifyFileFeedStatus(String feederProcessName, EnterpriseFeederStatus status, File doneFile, File dataFile, File reconFile, List<Message> errorMessages);


    /**
     * Performs notification about the status of the upload (i.e. feeding) of a single file set (i.e. done file, data file, and
     * recon file). This method is useful when the file sets are not <b>NOTE:</b> the CALLER MUST CLOSE all of the input streams
     * that are passed in. In addition, the input streams may be used by implementations of this method, and no assumption about the
     * state of the input streams should be made after this method returns.
     *
     * @param feederProcessName    The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                             feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                             value.
     * @param event                The event/status of the upload of the file set
     * @param doneFileDescription  The description of the done file to be output during notification
     * @param doneFileContents     An input stream for the contents of the done file. If the implementation does not require the
     *                             contents of the file, then <code>null</code> may be passed in.
     * @param dataFileDescription  The description of the done file to be output during notification
     * @param dataFileContents     An input stream for the contents of the data file. If the implementation does not require the
     *                             contents of the file, then <code>null</code> may be passed in.
     * @param reconFileDescription The description of the done file to be output during notification
     * @param reconFileContents    An input stream for the contents of the recon file. If the implementation does not require the
     *                             contents of the file, then <code>null</code> may be passed in.
     * @param errorMessages        Any error messages for which to provide notification
     */
    public void notifyFileFeedStatus(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, InputStream doneFileContents, String dataFileDescription, InputStream dataFileContents, String reconFileDescription, InputStream reconFileContents, List<Message> errorMessages);

    /**
     * Generates the status message that would be generated by a call to notifyFileFeedStatus with the same parameters.
     *
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                          feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                          value.
     * @param event             The event/status of the upload of the file set
     * @param doneFile          The done file
     * @param dataFile          The data file
     * @param reconFile         The recon file
     * @param errorMessages     Any error messages for which to provide notification
     */
    public String getFileFeedStatusMessage(String feederProcessName, EnterpriseFeederStatus status, File doneFile, File dataFile, File reconFile, List<Message> errorMessages);

    /**
     * Generates the status message that would be generated by a call to notifyFileFeedStatus with the same parameters. <b>NOTE:</b>
     * the CALLER MUST CLOSE all of the input streams that are passed in. In addition, the input streams may be used by
     * implementations of this method, and no assumption about the state of the input streams should be made after this method
     * returns.
     *
     * @param feederProcessName    The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                             feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                             value.
     * @param event                The event/status of the upload of the file set
     * @param doneFileDescription  The description of the done file to be output during notification
     * @param doneFileContents     An input stream for the contents of the done file. If the implementation does not require the
     *                             contents of the file, then <code>null</code> may be passed in.
     * @param dataFileDescription  The description of the done file to be output during notification
     * @param dataFileContents     An input stream for the contents of the data file. If the implementation does not require the
     *                             contents of the file, then <code>null</code> may be passed in.
     * @param reconFileDescription The description of the done file to be output during notification
     * @param reconFileContents    An input stream for the contents of the recon file. If the implementation does not require the
     *                             contents of the file, then <code>null</code> may be passed in.
     * @param errorMessages        Any error messages for which to provide notification
     */
    public String getFileFeedStatusMessage(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, InputStream doneFileContents, String dataFileDescription, InputStream dataFileContents, String reconFileDescription, InputStream reconFileContents, List<Message> errorMessages);
}
