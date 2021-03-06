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
package org.kuali.kfs.module.ar.document.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.fp.document.authorization.FinancialProcessingAccountingLineAuthorizer;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kim.api.identity.Person;

import java.util.Map;
import java.util.Set;

public class CustomerInvoiceDocumentSourceLinesAuthorizer extends FinancialProcessingAccountingLineAuthorizer {

    private static final String RECALCULATE_METHOD_NAME = "recalculateSourceLine";
    private static final String RECALCULATE_LABEL = "Recalculate Source Accounting Line";
    private static final String RECALCULATE_BUTTON_LABEL = "Recalculate";
    private static final String DISCOUNT_METHOD_NAME = "discountSourceLine";
    private static final String DISCOUNT_LABEL = "Discount a Source Accounting Line";
    private static final String DISCOUNT_BUTTON_LABEL = "Discount";
    private static final String REFRESH_METHOD_NAME = "refreshNewSourceLine";
    private static final String REFRESH_LABEL = "Refresh New Source Line";
    private static final String REFRESH_BUTTON_LABEL = "Refresh";
    private static final String REFRESH_BUTTON_ICON = "fa fa-refresh";
    private static final String DEFAULT_BUTTON_STYLE = "btn btn-default";
    private static final String ICON_BUTTON_STYLE = "btn clean";


    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getActionMap(org.kuali.kfs.sys.businessobject.AccountingLine,
     * java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        Map<String, AccountingLineViewAction> actionMap = super.getActionMap(accountingLineRenderingContext, accountingLinePropertyName, accountingLineIndex, groupTitle);

        CustomerInvoiceDetail invoiceLine = (CustomerInvoiceDetail) accountingLineRenderingContext.getAccountingLine();

        // show the Refresh button on the New Line Actions
        if (isNewLine(accountingLineIndex)) {
            actionMap.put(REFRESH_METHOD_NAME, new AccountingLineViewAction(REFRESH_METHOD_NAME, REFRESH_LABEL, ICON_BUTTON_STYLE, REFRESH_BUTTON_LABEL, REFRESH_BUTTON_ICON));
        } else {
            // always add the Recalculate button if its in edit mode
            String groupName = super.getActionInfixForExtantAccountingLine(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName);
            String methodName = methodName(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, RECALCULATE_METHOD_NAME);
            actionMap.put(methodName, new AccountingLineViewAction(methodName, RECALCULATE_LABEL, DEFAULT_BUTTON_STYLE, RECALCULATE_BUTTON_LABEL));

            // only add the Discount button if its not a Discount Line or a Discount Line Parent
            if (showDiscountButton(invoiceLine)) {
                methodName = methodName(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, DISCOUNT_METHOD_NAME);
                actionMap.put(methodName, new AccountingLineViewAction(methodName, DISCOUNT_LABEL, DEFAULT_BUTTON_STYLE, DISCOUNT_BUTTON_LABEL));
            }
        }

        return actionMap;
    }

    private boolean showDiscountButton(CustomerInvoiceDetail invoiceLine) {
        return (!invoiceLine.isDiscountLine() && !invoiceLine.isDiscountLineParent());
    }

    private String methodName(AccountingLine line, String accountingLineProperty, Integer accountingLineIndex, String methodName) {
        String infix = super.getActionInfixForExtantAccountingLine(line, accountingLineProperty);
        return methodName + ".line" + accountingLineIndex.toString() + ".anchoraccounting" + infix + "Anchor";
    }

    private boolean isNewLine(Integer accountingLineIndex) {
        return (accountingLineIndex == null || accountingLineIndex.intValue() < 0);
    }

    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set<String> blocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        boolean enableTax = parameterService.getParameterValueAsBoolean("KFS-AR", "Document", ArConstants.ENABLE_SALES_TAX_IND);
        if (!enableTax) {
            blocks.add("invoiceItemTaxAmount");
            blocks.add("taxableIndicator");
        }
        return blocks;
    }

    /**
     * Overridden to make:
     * 1. chart and account number read only for discount lines
     * 2. invoice item description and amount editable for recurring invoices
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineFieldModifyability(org.kuali.kfs.sys.document.AccountingDocument,
     * org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.web.AccountingLineViewField, java.util.Map)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        boolean canModify = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);

        if (canModify) {
            boolean discountLineFlag = ((CustomerInvoiceDetail) accountingLine).isDiscountLine();
            if (discountLineFlag) {
                if (StringUtils.equals(fieldName, getChartPropertyName()) || StringUtils.equals(fieldName, getAccountNumberPropertyName()))
                    canModify = false;
            }
        }

        return canModify;
    }

    /**
     * @return the property name of the chart field, which will be set to read only for discount lines
     */
    protected String getChartPropertyName() {
        return "chartOfAccountsCode";
    }

    /**
     * @return the property name of the account number field, which will be set to read only for discount lines
     */
    protected String getAccountNumberPropertyName() {
        return "accountNumber";
    }

    /**
     * @return the property name of the invoice item description field, which will be set editable for recurring invoices
     */
    protected String getItemDescriptionPropertyName() {
        return "invoiceItemDescription";
    }

    /**
     * @return the property name of the amount field, which will be set editable for recurring invoices
     */
    protected String getAmountPropertyName() {
        return "amount";
    }
}
