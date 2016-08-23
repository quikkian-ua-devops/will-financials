/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.kns.web.ui.ExtraButton;
import org.kuali.kfs.sys.KFSConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for Form class for Contracts & Grants Report Lookups.
 */
public class ContractsGrantsReportLookupForm extends LookupForm {

    protected String htmlFormAction;
    protected boolean displayActionsForRow;

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = new ArrayList<ExtraButton>();

        // Print button
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty("methodToCall.print");
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_genprintfile.gif");
        printButton.setExtraButtonAltText("Print");
        buttons.add(printButton);
        return buttons;
    }

    /**
     * @return htmlFormAction
     */
    public String getHtmlFormAction() {
        return htmlFormAction;
    }

    /**
     * @param htmlFormAction
     */
    public void setHtmlFormAction(String htmlFormAction) {
        this.htmlFormAction = htmlFormAction;
    }

    /**
     * @return true if actions should be shown in the lookup results
     */
    public boolean isDisplayActionsForRow() {
        return displayActionsForRow;
    }

    /**
     * Sets whether actions should be shown in the lookup results
     *
     * @param displayActionsForRow true if actions should be displayed, false otherwise
     */
    public void setDisplayActionsForRow(boolean displayActionsForRow) {
        this.displayActionsForRow = displayActionsForRow;
    }
}
