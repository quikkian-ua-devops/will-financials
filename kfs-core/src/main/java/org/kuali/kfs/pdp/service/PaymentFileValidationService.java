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
package org.kuali.kfs.pdp.service;

import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;

import java.util.List;

/**
 * Defines validation methods on a payment file.
 */
public interface PaymentFileValidationService {

    /**
     * Performs validations that stop the loading of a payment file.
     *
     * @param paymentFile parsed payment file object
     * @param errorMap    map of errors encountered
     */
    public void doHardEdits(PaymentFileLoad paymentFile, MessageMap errorMap);

    /**
     * Performs warning checks and setting of default values.
     *
     * @param paymentFile parsed payment file object
     * @return <code>List</code> of warning messages
     */
    public List<String> doSoftEdits(PaymentFileLoad paymentFile);
}
