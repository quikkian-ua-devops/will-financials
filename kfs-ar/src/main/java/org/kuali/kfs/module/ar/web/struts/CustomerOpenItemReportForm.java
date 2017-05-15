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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.kns.web.struts.form.LookupForm;

/**
 * Form class for Customer open item Report.
 */
public class CustomerOpenItemReportForm extends LookupForm {

    protected String htmlFormAction;

    /**
     * Gets the htmlFormAction.
     *
     * @return Returns the htmlFormAction.
     */
    public String getHtmlFormAction() {
        return htmlFormAction;
    }

    /**
     * Sets the htmlFormAction.
     *
     * @param htmlFormAction The htmlFormAction.
     */
    public void setHtmlFormAction(String htmlFormAction) {
        this.htmlFormAction = htmlFormAction;
    }

    /**
     * Default constructor.
     */
    public CustomerOpenItemReportForm() {
        setHtmlFormAction("arCustomerOpenItemReportLookup");
    }
}
