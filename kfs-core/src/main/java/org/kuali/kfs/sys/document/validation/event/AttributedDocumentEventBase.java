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
package org.kuali.kfs.sys.document.validation.event;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule;

import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract implementation of an attributed document event.
 */
public class AttributedDocumentEventBase extends KualiDocumentEventBase implements AttributedDocumentEvent {
    private Map<String, Object> attributes;
    private Object iterationSubject;

    /**
     * Constructs a AttributedDocumentEventBase
     *
     * @param description
     * @param errorPathPrefix
     */
    protected AttributedDocumentEventBase(String description, String errorPathPrefix) {
        super(description, errorPathPrefix);
        attributes = new HashMap<String, Object>();
    }

    /**
     * @see org.kuali.kfs.krad.rule.event.KualiDocumentEventBase#KualiDocumentEventBase(java.lang.String, java.lang.String, org.kuali.kfs.krad.document.Document)
     */
    public AttributedDocumentEventBase(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
        attributes = new HashMap<String, Object>();
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    /**
     * Gets the iterationSubject attribute.
     *
     * @return Returns the iterationSubject.
     */
    public Object getIterationSubject() {
        return iterationSubject;
    }

    /**
     * Sets the iterationSubject attribute value.
     *
     * @param iterationSubject The iterationSubject to set.
     */
    public void setIterationSubject(Object iterationSubject) {
        this.iterationSubject = iterationSubject;
    }

    /**
     * @see org.kuali.kfs.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AccountingRuleEngineRule.class;
    }

    /**
     * @see org.kuali.kfs.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.kfs.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AccountingRuleEngineRule) rule).validateForEvent(this);
    }
}
