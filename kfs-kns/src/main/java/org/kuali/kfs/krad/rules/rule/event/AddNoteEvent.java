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
package org.kuali.kfs.krad.rules.rule.event;

import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.AddNoteRule;
import org.kuali.kfs.krad.rules.rule.BusinessRule;

/**
 * This class represents the add note event that is part of an eDoc in Kuali. This is triggered when a user presses the add button
 * for a given note or it could happen when another piece of code calls the create note method in the document service.
 */
public final class AddNoteEvent extends KualiDocumentEventBase {
    private Note note;

    /**
     * Constructs an AddNoteEvent with the specified errorPathPrefix and document
     *
     * @param document
     * @param errorPathPrefix
     */
    public AddNoteEvent(String errorPathPrefix, Document document, Note note) {
        super("creating add note event for document " + getDocumentId(document), errorPathPrefix, document);
        this.note = note;
    }

    /**
     * Constructs an AddNoteEvent with the given document
     *
     * @param document
     */
    public AddNoteEvent(Document document, Note note) {
        this("", document, note);
    }

    /**
     * This method retrieves the note associated with this event.
     *
     * @return
     */
    public Note getNote() {
        return note;
    }

    @Override
    public void validate() {
        super.validate();
        if (getNote() == null) {
            throw new IllegalArgumentException("invalid (null) note");
        }
    }

    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return AddNoteRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddNoteRule) rule).processAddNote(getDocument(), getNote());
    }
}
