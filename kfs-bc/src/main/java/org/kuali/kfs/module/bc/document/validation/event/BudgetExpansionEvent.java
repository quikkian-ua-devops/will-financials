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
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.BudgetExpansionRule;

/**
 * Base class for expansion rule events.
 */
public abstract class BudgetExpansionEvent extends KualiDocumentEventBase {
    private BudgetConstructionDocument budgetConstructionDocument;

    /**
     * Constructs a BudgetExpansionEvent.java.
     */
    public BudgetExpansionEvent(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
        this.budgetConstructionDocument = (BudgetConstructionDocument) document;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return BudgetExpansionRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((BudgetExpansionRule) rule).processExpansionRule(this);
    }

    public abstract Class getExpansionRuleInterfaceClass();

    public abstract boolean invokeExpansionRuleMethod(BusinessRule rule);

    /**
     * Gets the budgetConstructionDocument attribute.
     *
     * @return Returns the budgetConstructionDocument.
     */
    public BudgetConstructionDocument getBudgetConstructionDocument() {
        return budgetConstructionDocument;
    }

    /**
     * Sets the budgetConstructionDocument attribute value.
     *
     * @param budgetConstructionDocument The budgetConstructionDocument to set.
     */
    public void setBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument) {
        this.budgetConstructionDocument = budgetConstructionDocument;
    }

}
