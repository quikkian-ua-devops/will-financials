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
package org.kuali.kfs.coa.identity;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class OrgReviewRoleInquirableImpl extends KualiInquirableImpl {

    protected static final String ORG_REVIEW_ROLE_INQUIRY_TITLE = "orgReviewRoleInquiryTitle";
    protected static final String ORG_REVIEW_ROLE_MEMBER_ID = "orgReviewRoleMemberId";

    private static OrgReviewRoleService orgReviewRoleService;

    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        OrgReviewRole orr = new OrgReviewRole();
        String roleMemberId = (String) fieldValues.get(ORG_REVIEW_ROLE_MEMBER_ID);
        if (StringUtils.isNotBlank(roleMemberId)) {
            if (Boolean.valueOf((String) fieldValues.get(OrgReviewRole.DELEGATE_FIELD_NAME))) {
                getOrgReviewRoleService().populateOrgReviewRoleFromDelegationMember(orr, null, roleMemberId);
            } else {
                getOrgReviewRoleService().populateOrgReviewRoleFromRoleMember(orr, roleMemberId);
            }
        }
        return orr;
    }

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (StringUtils.equals(attributeName, ORG_REVIEW_ROLE_INQUIRY_TITLE)) {
            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, OrgReviewRole.class.getName());
            if (StringUtils.isNotBlank(((OrgReviewRole) businessObject).getDelegationMemberId())) {
                parameters.put(ORG_REVIEW_ROLE_MEMBER_ID, ((OrgReviewRole) businessObject).getDelegationMemberId());
                parameters.put(OrgReviewRole.DELEGATE_FIELD_NAME, "true");
            } else if (StringUtils.isNotBlank(((OrgReviewRole) businessObject).getRoleMemberId())) {
                parameters.put(ORG_REVIEW_ROLE_MEMBER_ID, ((OrgReviewRole) businessObject).getRoleMemberId());
                parameters.put(OrgReviewRole.DELEGATE_FIELD_NAME, "false");
            }
            return getHyperLink(OrgReviewRole.class, Collections.EMPTY_MAP, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
        } else {
            return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
        }
    }

    protected OrgReviewRoleService getOrgReviewRoleService() {
        if (orgReviewRoleService == null) {
            orgReviewRoleService = SpringContext.getBean(OrgReviewRoleService.class);
        }
        return orgReviewRoleService;
    }
}
