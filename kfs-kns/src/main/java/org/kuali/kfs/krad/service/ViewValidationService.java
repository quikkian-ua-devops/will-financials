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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.view.ViewModel;


/**
 * Validation service for KRAD views
 */
public interface ViewValidationService {

    /**
     * This is the main validation method that should be used when validating Views
     * Validates the view based on the model passed in, this will correctly use previousView by default
     * as it automatically contains the generated data the validation requires.
     *
     * @param model
     * @return DictionaryValidationResult that contains any errors/messages if any, messages will have already
     * been added to the MessageMap
     */
    public DictionaryValidationResult validateView(ViewModel model);

    /**
     * Additional validation method when you want to explicitly define the View being validated.  Note
     * that the view must have the correct binding information on its InputFields already generated by
     * its lifecycle for this method to be used correctly.
     *
     * @param view
     * @param model
     * @return DictionaryValidationResult that contains any errors/messages if any,, messages will have already
     * been added to the MessageMap
     */
    public DictionaryValidationResult validateView(View view, ViewModel model);

}
