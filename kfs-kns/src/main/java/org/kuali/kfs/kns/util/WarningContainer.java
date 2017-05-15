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
package org.kuali.kfs.kns.util;

import org.kuali.kfs.krad.util.MessageMap;

import java.util.List;
import java.util.Set;

/**
 * An implementation of {@link MessageContainer} that makes warning messages accessible by the JSP layer
 */
public class WarningContainer extends MessageContainer {

    public WarningContainer(MessageMap errorMap) {
        super(errorMap);
    }

    /**
     * This overridden method ...
     *
     * @see MessageContainer#getMessageCount()
     */
    @Override
    public int getMessageCount() {
        return getMessageMap().getWarningCount();
    }


    /**
     * @see MessageContainer#getMessagePropertyNames()
     */
    @Override
    protected Set<String> getMessagePropertyNames() {
        return getMessageMap().getAllPropertiesWithWarnings();
    }

    /**
     * @see MessageContainer#getMessagePropertyList()
     */
    @Override
    public List<String> getMessagePropertyList() {
        return getMessageMap().getPropertiesWithWarnings();
    }

    /**
     * @see MessageContainer#getMessagesForProperty(java.lang.String)
     */
    @Override
    protected List getMessagesForProperty(String propertyName) {
        return getMessageMap().getWarningMessagesForProperty(propertyName);
    }
}
