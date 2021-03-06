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
package org.kuali.kfs.module.purap.document.web.struts;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.web.ui.ExtraButton;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PurchasingPaymentDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Struts Action Form for Purchasing and Accounts Payable documents.
 */
public class PurchasingAccountsPayableFormBase extends KualiAccountingDocumentFormBase {

    protected transient List<SummaryAccount> summaryAccounts;
    protected boolean readOnlyAccountDistributionMethod;

    /**
     * Constructs a PurchasingAccountsPayableFormBase instance and initializes summary accounts.
     */
    public PurchasingAccountsPayableFormBase() {
        super();
        clearSummaryAccounts();
        setupAccountDistributionMethod();
    }

    /**
     * retrieves the system parameter value for account distribution method and determines
     * if the drop-down box on the form should be read only or not.
     */
    protected void setupAccountDistributionMethod() {
        String defaultDistributionMethod = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.DISTRIBUTION_METHOD_FOR_ACCOUNTING_LINES);

        if (PurapConstants.AccountDistributionMethodCodes.PROPORTIONAL_CODE.equalsIgnoreCase(defaultDistributionMethod) || PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(defaultDistributionMethod)) {
            this.setReadOnlyAccountDistributionMethod(true);
        } else {
            this.setReadOnlyAccountDistributionMethod(false);
        }
    }

    /**
     * Updates the summaryAccounts that are contained in the form. Currently we are only calling this on load and when
     * refreshAccountSummary is called.
     */
    public void refreshAccountSummmary() {
        clearSummaryAccounts();
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) getDocument();
        summaryAccounts.addAll(SpringContext.getBean(PurapAccountingService.class).generateSummaryAccounts(purapDocument));
    }

    /**
     * Initializes summary accounts.
     */
    public void clearSummaryAccounts() {
        summaryAccounts = new ArrayList<SummaryAccount>();
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getBaselineSourceAccountingLines()
     */
    public List getBaselineSourceAccountingLines() {
        List<AccountingLine> accounts = new ArrayList<AccountingLine>();
        if (ObjectUtils.isNull(accounts) || accounts.isEmpty()) {
            accounts = new ArrayList<AccountingLine>();
            for (PurApItem item : ((PurchasingAccountsPayableDocument) getDocument()).getItems()) {
                List<PurApAccountingLine> lines = item.getBaselineSourceAccountingLines();
                for (PurApAccountingLine line : lines) {
                    accounts.add(line);
                }

            }
        }
        return accounts;
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        PurchasingAccountsPayableDocument purapDoc = (PurchasingAccountsPayableDocument) this.getDocument();

        //fix document item/account references if necessary
        purapDoc.fixItemReferences();
    }

    public List<SummaryAccount> getSummaryAccounts() {
        if (summaryAccounts == null) {
            refreshAccountSummmary();
        }
        return summaryAccounts;
    }

    public void setSummaryAccounts(List<SummaryAccount> summaryAccounts) {
        this.summaryAccounts = summaryAccounts;
    }

    /**
     * KRAD Conversion: Performs customization of an extra button.
     * <p>
     * No data dictionary is involved.
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }

    /**
     * This method builds the url for the disbursement info on the purap documents.
     *
     * @return the disbursement info url
     */
    public String getDisbursementInfoUrl() {
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String orgCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        String subUnitCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KRADConstants.DOC_FORM_KEY, "88888888");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PurchasingPaymentDetail.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_UNIT_CODE, orgCode);
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, subUnitCode);

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);

        return lookupUrl;
    }

    /**
     * overridden to make sure accounting lines on items are repopulated
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populateAccountingLinesForResponse(java.lang.String, java.util.Map)
     */
    @Override
    protected void populateAccountingLinesForResponse(String methodToCall, Map parameterMap) {
        super.populateAccountingLinesForResponse(methodToCall, parameterMap);

        populateItemAccountingLines(parameterMap);
    }

    /**
     * Populates accounting lines for each item on the Purchasing AP document
     *
     * @param parameterMap the map of parameters
     */
    protected void populateItemAccountingLines(Map parameterMap) {
        int itemCount = 0;
        for (PurApItem item : ((PurchasingAccountsPayableDocument) getDocument()).getItems()) {
            populateAccountingLine(item.getNewSourceLine(), KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.ITEM + "[" + itemCount + "]." + KFSPropertyConstants.NEW_SOURCE_LINE, parameterMap);

            int sourceLineCount = 0;
            for (PurApAccountingLine purApLine : item.getSourceAccountingLines()) {
                populateAccountingLine(purApLine, KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.ITEM + "[" + itemCount + "]." + KFSPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + sourceLineCount + "]", parameterMap);
                sourceLineCount += 1;
            }
        }
    }

    /**
     * Gets the readOnlyAccountDistributionMethod attribute.
     *
     * @return Returns the readOnlyAccountDistributionMethod
     */

    public boolean isReadOnlyAccountDistributionMethod() {
        return readOnlyAccountDistributionMethod;
    }

    /**
     * Sets the readOnlyAccountDistributionMethod attribute.
     *
     * @param readOnlyAccountDistributionMethod The readOnlyAccountDistributionMethod to set.
     */
    public void setReadOnlyAccountDistributionMethod(boolean readOnlyAccountDistributionMethod) {
        this.readOnlyAccountDistributionMethod = readOnlyAccountDistributionMethod;
    }
}
