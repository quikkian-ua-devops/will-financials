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
package org.kuali.kfs.fp.document.web.struts;


/**
 * form for <code>YearEndTransferOfFunds</code>. behaves the same as the TOF. the only functional difference between the YearEnd
 * version and the non-yearEnd version of a document is the glpe's generation.
 *
 * @see org.kuali.kfs.fp.document.web.struts.TransferOfFundsForm
 */
public class YearEndTransferOfFundsForm extends TransferOfFundsForm {

    public YearEndTransferOfFundsForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "YETF";
    }
    // empty do not change. see above.
}
