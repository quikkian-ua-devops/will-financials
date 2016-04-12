/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.datadictionary;



/**
 * TransactionalDocumentEntry
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TransactionalDocumentEntry extends DocumentEntry {
    private static final long serialVersionUID = 5746921563371805425L;

    public TransactionalDocumentEntry() {}

    /**
	 * @see DocumentEntry#completeValidation()
	 */
	@Override
	public void completeValidation() {
		super.completeValidation();
        for ( ReferenceDefinition reference : defaultExistenceChecks ) {
            reference.completeValidation(documentClass, null);
        }
	}

	@Override
    public String toString() {
        return "TransactionalDocumentEntry for documentType " + getDocumentTypeName();
    }
}
