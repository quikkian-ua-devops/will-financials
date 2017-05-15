/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.batch.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.bo.AdHocRoutePerson;
import org.kuali.kfs.krad.bo.AdHocRouteRecipient;
import org.kuali.kfs.krad.bo.AdHocRouteWorkgroup;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.batch.service.InvoiceRecurrenceService;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lockbox Iterators are sorted by processedInvoiceDate and batchSequenceNumber.
 * Potentially there could be many batches on the same date.
 * For each set of records with the same processedInvoiceDate and batchSequenceNumber,
 * there will be one Cash-Control document. Each record within this set will create one Application document.
 */

@Transactional
public class InvoiceRecurrenceServiceImpl implements InvoiceRecurrenceService {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceServiceImpl.class);
    private DocumentService documentService;
    private DateTimeService dateTimeService;
    private BusinessObjectService boService;

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public boolean processInvoiceRecurrence() throws WorkflowException {

        Collection<InvoiceRecurrence> recurrences = getAllActiveInvoiceRecurrences();
        CustomerInvoiceDocument customerInvoiceDocument = new CustomerInvoiceDocument();
        for (InvoiceRecurrence invoiceRecurrence : recurrences) {

            /* Get some dates and calendars  */
            Date currentDate = getDateTimeService().getCurrentSqlDate();
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(getDateTimeService().getCurrentTimestamp());

            Date currentMonthProcessDate;
            Calendar currentMonthProcessCalendar = Calendar.getInstance();

            Date nextProcessDate;
            Calendar nextProcessCalendar = Calendar.getInstance();

            Date lastProcessDate;
            Calendar lastProcessCalendar = Calendar.getInstance();

            Date beginDate = invoiceRecurrence.getDocumentRecurrenceBeginDate();
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.setTime(new Timestamp(invoiceRecurrence.getDocumentRecurrenceBeginDate().getTime()));

            Date endDate = invoiceRecurrence.getDocumentRecurrenceEndDate();
            Date lastCreateDate = invoiceRecurrence.getDocumentLastCreateDate();
            String intervalCode = invoiceRecurrence.getDocumentRecurrenceIntervalCode();
            Integer totalRecurrenceNumber = invoiceRecurrence.getDocumentTotalRecurrenceNumber();


            /* Calculate currentMonthProcessDate*/
            currentMonthProcessCalendar = currentCalendar;
            int day = beginCalendar.get(Calendar.DAY_OF_MONTH);
            currentMonthProcessCalendar.set(Calendar.DAY_OF_MONTH, day);
            currentMonthProcessDate = KfsDateUtils.convertToSqlDate(currentMonthProcessCalendar.getTime());

            /* Calculate the nextProcessDate */
            if (currentDate.after(currentMonthProcessDate)) {
                nextProcessCalendar = currentMonthProcessCalendar;
                nextProcessCalendar.add(Calendar.MONTH, 1);
            } else {
                /* currentDate is less than or equal to currentMonthProcessDate
                 * so the nextProcessDate is equal to the currentMonthProcessDate */
                nextProcessCalendar = currentMonthProcessCalendar;
            }
            nextProcessDate = KfsDateUtils.convertToSqlDate(nextProcessCalendar.getTime());

            /* Calculate the lastProcessDate by subtracting one month from nextProcessingDate */
            lastProcessCalendar = nextProcessCalendar;
            lastProcessCalendar.add(Calendar.MONTH, -1);
            lastProcessDate = KfsDateUtils.convertToSqlDate(lastProcessCalendar.getTime());
            if (lastProcessDate.before(beginDate)) {
                lastProcessCalendar.clear();
            }
            lastProcessDate = KfsDateUtils.convertToSqlDate(lastProcessCalendar.getTime());
             /* if nextProcessDate is equal to currentDate create INV document */
            if (nextProcessDate.equals(currentDate)) {
                /* copy INV document to a new INV document */
                String initiator = invoiceRecurrence.getDocumentInitiatorUserPersonUserIdentifier();
                GlobalVariables.setUserSession(new UserSession(initiator));

                customerInvoiceDocument = (CustomerInvoiceDocument) getDocumentService().getByDocumentHeaderId(invoiceRecurrence.getInvoiceNumber());
                customerInvoiceDocument.toCopy();
                List<AdHocRouteRecipient> adHocRouteRecipients = new ArrayList<AdHocRouteRecipient>();
                adHocRouteRecipients.add(buildApprovePersonRecipient(initiator));
                getDocumentService().routeDocument(customerInvoiceDocument, "This is a recurred Customer Invoice", adHocRouteRecipients);
                invoiceRecurrence.setDocumentLastCreateDate(currentDate);
                boService.save(invoiceRecurrence);

            }

            /* if nextProcessDate is greater than currentDate BUT less than or equal to endDate */
            if (nextProcessDate.after(currentDate) && (!nextProcessDate.after(endDate))) {
                if ((ObjectUtils.isNotNull(lastCreateDate) && lastProcessDate.after(lastCreateDate)) ||
                    (ObjectUtils.isNull(lastCreateDate) && beginDate.before(currentDate))) {
                    /* copy INV document to a new INV document */
                    String initiator = invoiceRecurrence.getDocumentInitiatorUserPersonUserIdentifier();
                    GlobalVariables.setUserSession(new UserSession(initiator));

                    customerInvoiceDocument = (CustomerInvoiceDocument) getDocumentService().getByDocumentHeaderId(invoiceRecurrence.getInvoiceNumber());
                    customerInvoiceDocument.toCopy();
                    List<AdHocRouteRecipient> adHocRouteRecipients = new ArrayList<AdHocRouteRecipient>();
                    adHocRouteRecipients.add(buildApprovePersonRecipient(initiator));
                    getDocumentService().routeDocument(customerInvoiceDocument, "This is a recurred Customer Invoice", adHocRouteRecipients);
                    invoiceRecurrence.setDocumentLastCreateDate(currentDate);
                    boService.save(invoiceRecurrence);
                }
            }

            /* Check if this is the last recurrence. If yes, inactivate the INVR and send an FYI to the initiator and workgroup.  */
            if (!nextProcessDate.before(endDate)) {
                /* Change the active indicator to 'N' and send an FYI */
                String initiator = invoiceRecurrence.getDocumentInitiatorUserPersonUserIdentifier();
                GlobalVariables.setUserSession(new UserSession(initiator));

                MaintenanceDocument newMaintDoc = (MaintenanceDocument) getDocumentService().getNewDocument(getInvoiceRecurrenceMaintenanceDocumentTypeName());
                newMaintDoc.getOldMaintainableObject().setBusinessObject(invoiceRecurrence);
                InvoiceRecurrence newInvoiceRecurrence = invoiceRecurrence;
                newInvoiceRecurrence.setActive(false);
                newMaintDoc.getDocumentHeader().setDocumentDescription("Generated by Batch process");
                newMaintDoc.getDocumentHeader().setExplanation("Inactivated by the Batch process");
                newMaintDoc.getNewMaintainableObject().setBusinessObject(newInvoiceRecurrence);
                newMaintDoc.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_EDIT_ACTION);
                List<AdHocRouteRecipient> adHocRouteRecipients = new ArrayList<AdHocRouteRecipient>();
                adHocRouteRecipients.add(buildFyiPersonRecipient(initiator));
                getDocumentService().routeDocument(newMaintDoc, null, adHocRouteRecipients);
                newInvoiceRecurrence.setDocumentLastCreateDate(currentDate);
                boService.save(newInvoiceRecurrence);
            }
        }
        return true;

    }

    /**
     * @return returns all active invoice recurrences
     */
    protected Collection<InvoiceRecurrence> getAllActiveInvoiceRecurrences() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        final Collection<InvoiceRecurrence> recurrences = boService.findMatchingOrderBy(InvoiceRecurrence.class, fieldValues, "invoiceNumber", true);
        return recurrences;
    }

    protected String getInvoiceRecurrenceMaintenanceDocumentTypeName() {
        return "INVR";
    }

    /**
     * This method builds a FYI recipient.
     *
     * @param userId
     * @return
     */
    protected AdHocRouteRecipient buildFyiPersonRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KewApiConstants.ACTION_REQUEST_FYI_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }

    /**
     * This method builds a recipient for Approval.
     *
     * @param userId
     * @return
     */
    protected AdHocRouteRecipient buildApprovePersonRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KewApiConstants.ACTION_REQUEST_APPROVE_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }

    /**
     * This method builds a FYI workgroup recipient.
     *
     * @param userId
     * @return
     */
    protected AdHocRouteRecipient buildFyiWorkgroupRecipient(String workgroupId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRouteWorkgroup();
        adHocRouteRecipient.setActionRequested(KewApiConstants.ACTION_REQUEST_FYI_REQ);
        adHocRouteRecipient.setId(workgroupId);
        return adHocRouteRecipient;
    }

    /**
     * This method builds a workgroup recipient for Approval.
     *
     * @param userId
     * @return
     */
    protected AdHocRouteRecipient buildApproveWorkgroupRecipient(String workgroupId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRouteWorkgroup();
        adHocRouteRecipient.setActionRequested(KewApiConstants.ACTION_REQUEST_APPROVE_REQ);
        adHocRouteRecipient.setId(workgroupId);
        return adHocRouteRecipient;
    }

    public void setBusinessObjectService(BusinessObjectService boService) {
        this.boService = boService;
    }
}
