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
package org.kuali.kfs.kns.document.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatter;

/**
 * This class represents the authorization restrictions (or lack of) for a given field.
 */

/**
 * This is a description of what this class does - zjzhou don't forget to fill this in.
 *
 */
public class FieldRestriction {

    private String fieldName;
    private boolean editable;
    private boolean viewable;
    private boolean masked;
    private boolean partiallyMasked;
    private MaskFormatter maskFormatter;
    private boolean shouldBeEncrypted;

    /**
     * Constructs a FieldAuthorization.java.
     */
    public FieldRestriction() {
        editable = true;
        viewable = true;
    }

    /**
     *
     * Constructs a FieldAuthorization.java.
     *
     * @param fieldName - name of field to represent
     * @param canEdit - true if the field is editable in this context, false otherwise
     * @param canView - true if thie field is viewable in this context, false otherwise
     *
     */
    public FieldRestriction(String fieldName, boolean canEdit, boolean canView) {
        this.fieldName = fieldName;
        setEditable(canEdit); // using setters here to run impossible combinations check
        setViewable(canView);
    }

    /**
     *
     * Constructs a FieldAuthorization.java.
     *
     * @param fieldName - name of the field to represent
     * @param fieldAuthorizationFlag - Field.HIDDEN, Field.READONLY, or Field.EDITABLE
     */
    public FieldRestriction(String fieldName, String fieldAuthorizationFlag) {
        // if an invalid flag is passed in, the choke on it
        if (!fieldAuthorizationFlag.equals(Field.EDITABLE) && !fieldAuthorizationFlag.equals(Field.READONLY)
            && !fieldAuthorizationFlag.equals(Field.HIDDEN) && !fieldAuthorizationFlag.equals(Field.MASKED)
            && !fieldAuthorizationFlag.equals(Field.PARTIALLY_MASKED)) {
            throw new IllegalArgumentException("The only allowable values are " +
                "Field.HIDDEN, Field.READONLY, Field.EDITABLE, Field.MASKED and Field.PARTIALLY_MASKED");
        }

        this.fieldName = fieldName;

        if (fieldAuthorizationFlag.equals(Field.EDITABLE)) {
            this.editable = true;
            this.viewable = true;
        } else if (fieldAuthorizationFlag.equals(Field.READONLY)) {
            this.editable = false;
            this.viewable = true;
        } else if (fieldAuthorizationFlag.equals(Field.HIDDEN)) {
            this.editable = false;
            this.viewable = false;
        } else if (fieldAuthorizationFlag.equals(Field.MASKED)) {
            this.masked = true;
            this.viewable = true;
            this.editable = false;
        } else if (fieldAuthorizationFlag.equals(Field.PARTIALLY_MASKED)) {
            this.partiallyMasked = true;
            this.viewable = true;
            this.editable = false;
        }
    }

    /**
     *
     * This method returns the correct flag from the Kuali Field object, that corresponds to the particular combination of editable
     * and viewable set on this object.
     *
     * @return Field.HIDDEN, Field.READONLY, or Field.EDITABLE
     *
     */
    public String getKualiFieldDisplayFlag() {

        if (!editable && !viewable) {
            return Field.HIDDEN;
        }
        if (!editable && viewable) {
            return Field.READONLY;
        } else {
            return Field.EDITABLE;
        }

    }

    /**
     *
     * This method returns true if the FieldAuthorization is some kind of restriction, and returns false if it is an editable field.
     *
     * @return boolean
     *
     */
    public boolean isRestricted() {
        if (!editable || !viewable) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * This method returns true if this authorization prohibits Viewing and Editing, resulting in a hidden field.
     *
     * @return boolean
     *
     */
    public boolean isHidden() {
        if (!editable && !viewable) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * This method returns true if this authorization prohibits Editing but not Viewing, resulting in a ReadOnly field.
     *
     * @return boolean
     *
     */
    public boolean isReadOnly() {
        if (!editable && viewable) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the editable attribute.
     *
     * @return Returns the editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the editable attribute value.
     *
     * Note that if editable is being set to true, and the internal value of viewable is false, viewable will be flipped to true, to
     * avoid impossible combinations of flags.
     *
     * @param editable The editable to set.
     */
    public void setEditable(boolean editable) {
        if (editable && !this.viewable) {
            this.viewable = true;
        }
        this.editable = editable;
    }

    /**
     * Gets the fieldName attribute.
     *
     * @return Returns the fieldName.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the fieldName attribute value.
     *
     * @param fieldName The fieldName to set.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the viewable attribute.
     *
     * @return Returns the viewable.
     */
    public boolean isViewable() {
        return viewable;
    }

    /**
     * Sets the viewable attribute value.
     *
     * Note that if viewable is being set to false, and the internal value of editable is true, then editable will be silently
     * flipped to false. This is done to avoid impossible combinations of authorization flags.
     *
     * @param viewable The viewable to set.
     */
    public void setViewable(boolean viewable) {
        if (!viewable && this.editable) {
            this.editable = false;
        }
        this.viewable = viewable;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.fieldName);
        sb.append(" [");
        if (this.editable) {
            sb.append("editable");
        } else {
            sb.append("not editable");
        }
        sb.append(",");
        if (this.viewable) {
            sb.append("viewable");
        } else {
            sb.append("not viewable");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                FieldRestriction other = (FieldRestriction) obj;

                if (StringUtils.equals(this.fieldName, other.getFieldName())) {
                    if (this.editable == other.isEditable() && this.viewable == other.isViewable()) {
                        equal = true;
                    }
                }
            }
        }

        return equal;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @return the masked
     */
    public boolean isMasked() {
        return this.masked;
    }

    /**
     * @return the partiallyMasked
     */
    public boolean isPartiallyMasked() {
        return this.partiallyMasked;
    }


    /**
     * @return the shouldBeEncrypted
     */
    public boolean isShouldBeEncrypted() {
        return this.shouldBeEncrypted;
    }

    /**
     * @param shouldBeEncrypted the shouldBeEncrypted to set
     */
    public void setShouldBeEncrypted(boolean shouldBeEncrypted) {
        this.shouldBeEncrypted = shouldBeEncrypted;
    }

    /**
     * @return the maskFormatter
     */
    public MaskFormatter getMaskFormatter() {
        return this.maskFormatter;
    }

    /**
     * @param maskFormatter the maskFormatter to set
     */
    public void setMaskFormatter(MaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }


}
