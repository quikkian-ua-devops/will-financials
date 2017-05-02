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
package org.kuali.kfs.module.cg.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.dataaccess.CloseDao;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.kfs.module.cg.service.CloseService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.MessageFormat;
import java.util.Collection;

@Transactional
public class CloseServiceImpl implements CloseService {

    protected CloseDao closeDao;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CloseServiceImpl.class);
    protected ConfigurationService configService;

    /**
     * <ul>
     * <li>Get the max proposal_close_number in cg_prpsl_close_t.</li>
     * <li>Get the Close with that max_close_number.</li>got
     * <li>If todays date is the same as the user_initiate_date on that Close, continue. Else, break.</li>
     * <li>Get all proposals with a null closing_date and a submission_date <= the last_closed_date of the Close with the
     * max_proposal_close number.</li>
     * <li>Save the number of proposals that come back.</li>
     * <li>Update each of these proposals setting the close_date to todays date.</li>
     * <li>Get all awards with a null closing_date, an entry_date <= the last_closed_date of the Close with the max_close number and
     * a status_code not equal to 'U'.</li>
     * <li>Save the number of awards that come back.</li>
     * <li>Update each of these awards setting the close_date to todays date.</li>
     * <li>Update the Close with that max_close_number setting the proposal_closed_count to the number of proposals brought back
     * above and the award_closed_count to the number of awards brought back above.</li>
     * <li>Save the Close.</li>
     * </ul>
     *
     * @see org.kuali.kfs.module.cg.service.CloseService#close()
     */
    @Override
    public boolean close() {

        Date today = dateTimeService.getCurrentSqlDateMidnight();
        ProposalAwardCloseDocument max = getMaxApprovedClose(today);

        if (null == max) { // no closes at all. Gotta wait until we get an approved one.
            return true;
        }

        boolean result = true;
        String noteText = null;
        if (max.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames().contains(CGConstants.CGKimApiConstants.UNPROCESSED_ROUTING_NODE_NAME)) {

            try {

                Collection<Proposal> proposals = closeDao.getProposalsToClose(max);
                Long proposalCloseCount = new Long(proposals.size());
                for (Proposal p : proposals) {
                    p.setProposalClosingDate(today);
                    businessObjectService.save(p);
                }

                Collection<Award> awards = closeDao.getAwardsToClose(max);
                Long awardCloseCount = new Long(awards.size());
                for (Award a : awards) {
                    a.setAwardClosingDate(today);
                    businessObjectService.save(a);
                }

                max.setAwardClosedCount(awardCloseCount);
                max.setProposalClosedCount(proposalCloseCount);

                businessObjectService.save(max);
                noteText = configService.getPropertyValueAsString(CGKeyConstants.MESSAGE_CLOSE_JOB_SUCCEEDED);

            } catch (Exception e) {
                String messageProperty = configService.getPropertyValueAsString(CGKeyConstants.ERROR_CLOSE_JOB_FAILED);
                noteText = MessageFormat.format(messageProperty, e.getMessage(), e.getCause().getMessage());
            } finally {
                result = this.addDocumentNoteAfterClosing(max, noteText);
            }
        }
        return result;
    }

    /**
     * @see org.kuali.kfs.module.cg.service.CloseService#getMostRecentClose()
     */
    @Override
    public ProposalAwardCloseDocument getMostRecentClose() {
        Date today = dateTimeService.getCurrentSqlDateMidnight();
        String documentNumber = closeDao.getMostRecentClose(today);
        if (StringUtils.isNotBlank(documentNumber)) {
            try {
                return (ProposalAwardCloseDocument) documentService.getByDocumentHeaderId(documentNumber);
            } catch (WorkflowException we) {
                throw new RuntimeException(we);
            }
        } else {
            return null;
        }

    }

    /**
     * @see org.kuali.kfs.module.cg.service.CloseService#addDocumentNoteAfterClosing(String)
     */
    protected boolean addDocumentNoteAfterClosing(ProposalAwardCloseDocument close, String noteText) {
        try {
            documentService.createNoteFromDocument(close, noteText);
            documentService.approveDocument(close, noteText, null);
        } catch (WorkflowException we) {
            LOG.error("problem during CloseServiceImpl.addDocumentNoteAfterClosing()", we);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.cg.service.CloseService#getMaxApprovedClose(java.sql.Date)
     */
    @Override
    public ProposalAwardCloseDocument getMaxApprovedClose(Date today) {
        String documentNumber = closeDao.getMaxApprovedClose(today);
        if (StringUtils.isNotBlank(documentNumber)) {

            try {
                return (ProposalAwardCloseDocument) documentService.getByDocumentHeaderId(documentNumber);
            } catch (WorkflowException we) {
                throw new RuntimeException(we);
            }
        } else {
            return null;
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setCloseDao(CloseDao closeDao) {
        this.closeDao = closeDao;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }
}
