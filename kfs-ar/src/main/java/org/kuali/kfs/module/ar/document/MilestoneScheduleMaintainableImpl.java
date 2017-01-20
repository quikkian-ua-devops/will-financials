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
package org.kuali.kfs.module.ar.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.maintenance.Maintainable;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.Row;
import org.kuali.kfs.kns.web.ui.Section;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.document.service.MilestoneScheduleMaintenanceService;
import org.kuali.kfs.module.ar.document.validation.impl.MilestoneScheduleRuleUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;

import java.util.List;
import java.util.Map;

import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT;
import static org.kuali.kfs.sys.KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT;

/**
 * Methods for the Milestone Schedule maintenance document UI.
 */
public class MilestoneScheduleMaintainableImpl extends FinancialSystemMaintainable {
    private static volatile MilestoneScheduleMaintenanceService milestoneScheduleMaintenanceService;

    /**
     * This method is called to check if the award already has milestones set, and to validate on refresh
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     * org.kuali.rice.kns.document.MaintenanceDocument)
     */

    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        if (StringUtils.equals(ArConstants.AWARD_LOOKUP_IMPL, (String) fieldValues.get(KFSConstants.REFRESH_CALLER))) {
            if (MilestoneScheduleRuleUtil.checkIfMilestonesExists(getMilestoneSchedule())) {
                String pathToMaintainable = DOCUMENT + "." + NEW_MAINTAINABLE_OBJECT;
                GlobalVariables.getMessageMap().addToErrorPath(pathToMaintainable);
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PROPOSAL_NUMBER, ArKeyConstants.ERROR_AWARD_MILESTONE_SCHEDULE_EXISTS, new String[]{getMilestoneSchedule().getProposalNumber().toString()});
                GlobalVariables.getMessageMap().removeFromErrorPath(pathToMaintainable);
            }
        } else {
            super.refresh(refreshCaller, fieldValues, document);
        }
    }

    /**
     * Not to copy over the Milestones billed and milestoneIdentifier values to prevent
     * bad data and PK issues when saving new Milestones.
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);

        // clear out Bill IDs so new ones will get generated for these bills
        // reset billed indicator in case bill we're copying from was already billed
        List<Milestone> milestones = getMilestoneSchedule().getMilestones();
        if (ObjectUtils.isNotNull(milestones)) {
            for (Milestone milestone : milestones) {
                milestone.setBilled(false);
                milestone.setMilestoneIdentifier(null);
            }
        }
    }

    /**
     * This method is called for refreshing the Agency before display to show the full name in case the agency number was changed by
     * hand before any submit that causes a redisplay.
     */
    @Override
    public void processAfterRetrieve() {
        MilestoneSchedule milestoneSchedule = getMilestoneSchedule();
        milestoneSchedule.refreshNonUpdateableReferences();
        super.processAfterRetrieve();
    }

    /**
     * Override the getSections method on this maintainable so that the active field can be set to read-only when
     * a CINV doc has been created with this Milestone Schedule and Milestones
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        MilestoneSchedule milestoneSchedule = (MilestoneSchedule) document.getNewMaintainableObject().getBusinessObject();
        String proposalNumber = milestoneSchedule.getProposalNumber();

        for (Section section : sections) {
            String sectionId = section.getSectionId();
            if (sectionId.equalsIgnoreCase(ArConstants.MILESTONES_SECTION)) {
                prepareMilestonesTab(section, proposalNumber);
            }
        }

        return sections;
    }

    /**
     * Sets the Milestone in the passed in section to be readonly if it has been copied to a CG Invoice doc.
     *
     * @param section        Milestone section to review and possibly set readonly
     * @param proposalNumber used to look for CG Invoice docs
     */
    protected void prepareMilestonesTab(Section section, String proposalNumber) {
        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {
                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {
                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {
                            // a record is no longer editable if the bill has been copied to a CINV doc
                            if (ObjectUtils.getNestedAttributePrimitive(containerRowfield.getPropertyName()).matches(ArPropertyConstants.MilestoneFields.MILESTONE_IDENTIFIER)) {
                                String milestoneId = containerRowfield.getPropertyValue();
                                if (StringUtils.isNotEmpty(milestoneId)) {
                                    if (getMilestoneScheduleMaintenanceService().hasMilestoneBeenCopiedToInvoice(proposalNumber, milestoneId)) {
                                        for (Field rowfield : row.getFields()) {
                                            if (rowfield.getCONTAINER().equalsIgnoreCase(rowfield.getFieldType())) {
                                                for (Row fieldContainerRow : rowfield.getContainerRows()) {
                                                    for (Field fieldContainerRowField : fieldContainerRow.getFields()) {
                                                        fieldContainerRowField.setReadOnly(true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the underlying Milestone Schedule.
     *
     * @return
     */
    public MilestoneSchedule getMilestoneSchedule() {
        return (MilestoneSchedule) getBusinessObject();
    }

    public MilestoneScheduleMaintenanceService getMilestoneScheduleMaintenanceService() {
        if (milestoneScheduleMaintenanceService == null) {
            milestoneScheduleMaintenanceService = SpringContext.getBean(MilestoneScheduleMaintenanceService.class);
        }
        return milestoneScheduleMaintenanceService;
    }
}
