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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemAccountingLineTotalsValidation extends GenericValidation {
    protected AccountingDistributionService accountingDistributionService;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelDocument travelDocument = (TravelDocument) event.getDocument();

        List<AccountingDistribution> distributions = getAccountingDistributionService().buildDistributionFrom(travelDocument);
        KualiDecimal totalRemaining = KualiDecimal.ZERO;
        Map<AccountingLineDistributionKey, KualiDecimal> amounts = new HashMap<AccountingLineDistributionKey, KualiDecimal>();
        Map<AccountingLineDistributionKey, KualiDecimal> finalAmounts = new HashMap<AccountingLineDistributionKey, KualiDecimal>();
        Map<AccountingLineDistributionKey, Integer> lineIndexes = new HashMap<AccountingLineDistributionKey, Integer>();

        KualiDecimal distributionsTotal = KualiDecimal.ZERO;
        for (final AccountingDistribution dist : distributions) {
            distributionsTotal = distributionsTotal.add(dist.getSubTotal());
            AccountingLineDistributionKey key = new AccountingLineDistributionKey(dist.getObjectCode(), dist.getCardType());
            if (amounts.containsKey(key)) {
                KualiDecimal tempAmount = dist.getSubTotal().add(amounts.get(key));
                amounts.put(key, tempAmount);
                finalAmounts.put(key, tempAmount);
            } else {
                amounts.put(key, dist.getSubTotal());
                finalAmounts.put(key, dist.getSubTotal());
            }
        }

        if (travelDocument.getSourceAccountingLines() != null && !travelDocument.getSourceAccountingLines().isEmpty()) {
            List errorPath = GlobalVariables.getMessageMap().getErrorPath();
            GlobalVariables.getMessageMap().clearErrorPath();

            KualiDecimal accountingLineTotal = KualiDecimal.ZERO;

            //check the current accounting line
            for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>) travelDocument.getSourceAccountingLines()) {
                accountingLineTotal = accountingLineTotal.add(line.getAmount());
                AccountingLineDistributionKey key = new AccountingLineDistributionKey(line.getFinancialObjectCode(), line.getCardType());
                if (amounts.containsKey(key)) {
                    if (amounts.get(key).isGreaterEqual(line.getAmount())) { // the current accounting line does not max out this distribution, so just subtract the amount of the line from its matching distribution bucket
                        KualiDecimal tempAmount = amounts.get(key).subtract(line.getAmount());
                        amounts.put(key, tempAmount);
                        lineIndexes.put(key, line.getSequenceNumber());
                    } else { // the source accounting lines for this object code and card type have a greater total than the distribution
                        GlobalVariables.getMessageMap().putError(KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + (line.getSequenceNumber().intValue() - 1) + "]." + TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE_TOTAL, key.getFinancialObjectCode(), key.getCardType(), finalAmounts.get(key).toString());
                        rulePassed = false;
                    }
                } else {
                    if (!(event.getDocument() instanceof TravelAuthorizationDocument)) { // the accounting line charges to an object code that there is no distribution for.  That's weird...and an error.
                        GlobalVariables.getMessageMap().putError(KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + (line.getSequenceNumber().intValue() - 1) + "]." + TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE, line.getFinancialObjectCode(), line.getCardType());
                        rulePassed = false;
                    }
                }
            }

            if (rulePassed) {
                if ((travelDocument.getExpenseLimit() == null || travelDocument.getExpenseLimit().equals(KualiDecimal.ZERO)) || (distributionsTotal.isLessThan(travelDocument.getExpenseLimit()))) { // there's no expense limit or the expense total is less than the expense limit - make sure that the document has emptied out all of its buckets
                    for (AccountingLineDistributionKey key : amounts.keySet()) {
                        KualiDecimal tempAmount = amounts.get(key);
                        if (!tempAmount.isZero()) { // the accounting lines for this distribution's object code + card type are less than the amount to distribute...so error
                            String errorKey = (lineIndexes.containsKey(key)) ?
                                KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + (lineIndexes.get(key).intValue() - 1) + "]." + TravelAuthorizationFields.FIN_OBJ_CD
                                :
                                TemPropertyConstants.NEW_SOURCE_ACCTG_LINE + "." + TravelAuthorizationFields.FIN_OBJ_CD;
                            GlobalVariables.getMessageMap().putError(errorKey, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE_TOTAL, key.getFinancialObjectCode(), key.getCardType(), finalAmounts.get(key).toString());
                            rulePassed = false;
                        }
                    }
                } else { // there is an expense limit and the expenses on the document exceeded it...so just make sure that the expense limit is maxed out
                    if (accountingLineTotal.isLessThan(travelDocument.getExpenseLimit())) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE + "." + KFSPropertyConstants.AMOUNT, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE_EXPENSE_LIMIT, distributionsTotal.toString(), travelDocument.getExpenseLimit().toString(), accountingLineTotal.toString());
                        rulePassed = false;
                    } else if (accountingLineTotal.isGreaterThan(travelDocument.getExpenseLimit())) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE + "." + KFSPropertyConstants.AMOUNT, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_EXPENSE_LIMIT_EXCEEDED, accountingLineTotal.toString(), travelDocument.getExpenseLimit().toString());
                        rulePassed = false;
                    }
                }
            }
            GlobalVariables.getMessageMap().getErrorPath().addAll(errorPath);
        }

        GlobalVariables.getMessageMap().clearErrorPath();
        return rulePassed;
    }

    public AccountingDistributionService getAccountingDistributionService() {
        return accountingDistributionService;
    }

    public void setAccountingDistributionService(AccountingDistributionService accountingDistributionService) {
        this.accountingDistributionService = accountingDistributionService;
    }
}
