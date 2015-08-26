/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.inquiry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableMilestoneSchedule;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.integration.ar.AccountsReceivablePredeterminedBillingSchedule;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsBillingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.kns.datadictionary.InquirySectionDefinition;
import org.kuali.kfs.kns.inquiry.InquiryRestrictions;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.kns.web.ui.Section;
import org.kuali.kfs.kns.web.ui.SectionBridge;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.util.UrlFactory;

/**
 * Used for wiring up {@link Award} for inquiries.
 */
public class AwardInquirableImpl extends KfsInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardInquirableImpl.class);

    /**
     * Helper method to build an inquiry URLs for MilestoneSchedule or PredeterminedBillingSchedule links.
     *
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {

        if (StringUtils.equals(CGPropertyConstants.AwardFields.MILESTONE_SCHEDULE_INQUIRY_TITLE, attributeName)) {
            return buildInquiryUrl(businessObject, AccountsReceivableMilestoneSchedule.class);
        } else if (StringUtils.equals(CGPropertyConstants.AwardFields.PREDETERMINED_BILLING_SCHEDULE_INQUIRY_TITLE, attributeName)) {
            return buildInquiryUrl(businessObject, AccountsReceivablePredeterminedBillingSchedule.class);
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

    /**
     * Build the inquiry URL for the business object passed in as a parameter.
     *
     * @param businessObject
     * @param inquiryHref
     */
    protected AnchorHtmlData buildInquiryUrl(BusinessObject businessObject, Class businessObjectClass) {
        AnchorHtmlData inquiryHref = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);

        String baseUrl = KRADConstants.INQUIRY_ACTION;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObjectClass.getName());
        parameters.put(KFSPropertyConstants.PROPOSAL_NUMBER, ObjectUtils.getPropertyValue(businessObject, KFSPropertyConstants.PROPOSAL_NUMBER).toString());

        inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));

        return inquiryHref;
    }

    /**
     * Only show the Schedule link if CGB is enabled and for the appropriate Billing Frequency
     * (Milestone Schedule for Milestone billing, Predetermined Billing Schedule for PDBS billing, or none).
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     *
     * KRAD Conversion: Inquirable performs conditional display/hiding of the sections on the inquiry
     * But all field/section definitions are in data dictionary for bo Asset.
     */
    @Override
    public List<Section> getSections(BusinessObject businessObject) {
        List<Section> sections = new ArrayList<Section>();
        if (getBusinessObjectClass() == null) {
            LOG.error("Business object class not set in inquirable.");
            throw new RuntimeException("Business object class not set in inquirable.");
        }

        Award award = (Award) businessObject;

        InquiryRestrictions inquiryRestrictions = KNSServiceLocator.getBusinessObjectAuthorizationService()
                .getInquiryRestrictions(businessObject, GlobalVariables.getUserSession().getPerson());

        Collection<InquirySectionDefinition> inquirySections = getBusinessObjectDictionaryService().getInquirySections(
                getBusinessObjectClass());
        Collection<?> sectionIdsToIgnore = getSectionIdsToIgnore();
        for (Iterator<InquirySectionDefinition> iter = inquirySections.iterator(); iter.hasNext();) {
            InquirySectionDefinition inquirySection = iter.next();
            String sectionId = inquirySection.getId();
            if (!inquiryRestrictions.isHiddenSectionId(sectionId) && !sectionIdsToIgnore.contains(sectionId)) {
                if (StringUtils.equals(sectionId, CGPropertyConstants.SectionId.AWARD_PREDETERMINED_BILLING_SCHEDULE_SECTION_ID)) {
                    if (StringUtils.equals(award.getBillingFrequencyCode(), CGConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) &&
                        SpringContext.getBean(AccountsReceivableModuleBillingService.class).hasPredeterminedBillingSchedule(award.getProposalNumber())) {
                            Section section = SectionBridge.toSection(this, inquirySection, businessObject, inquiryRestrictions);
                            sections.add(section);
                    }
                } else if (StringUtils.equals(sectionId, CGPropertyConstants.SectionId.AWARD_MILESTONE_SCHEDULE_SECTION_ID)) {
                    if (StringUtils.equals(award.getBillingFrequencyCode(), CGConstants.MILESTONE_BILLING_SCHEDULE_CODE) &&
                        SpringContext.getBean(AccountsReceivableModuleBillingService.class).hasMilestoneSchedule(award.getProposalNumber())) {
                            Section section = SectionBridge.toSection(this, inquirySection, businessObject, inquiryRestrictions);
                            sections.add(section);
                    }
                } else {
                    Section section = SectionBridge.toSection(this, inquirySection, businessObject, inquiryRestrictions);
                    sections.add(section);
                }
            }
        }

        return sections;
    }

    /**
     * If the Contracts & Grants Billing (CGB) enhancement is disabled, we don't want to
     * process sections only related to CGB.
     *
     * @return Collection of section ids to ignore
     */
    protected Collection<?> getSectionIdsToIgnore() {
        if (!SpringContext.getBean(AccountsReceivableModuleBillingService.class).isContractsGrantsBillingEnhancementActive()) {
            return SpringContext.getBean(ContractsAndGrantsBillingService.class).getAwardContractsGrantsBillingSectionIds();
        } else {
            return CollectionUtils.EMPTY_COLLECTION;
        }
    }

}

