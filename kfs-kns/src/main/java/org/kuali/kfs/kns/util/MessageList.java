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
package org.kuali.kfs.kns.util;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.krad.util.ErrorMessage;

import java.util.ArrayList;

/**
 * This is a description of what this class does - wliang don't forget to fill this in.
 */
public class MessageList extends ArrayList<ErrorMessage> {
    public void add(String messageKey, String... messageParameters) {
        add(new ErrorMessage(messageKey, messageParameters));
    }

    public ActionMessages toActionMessages() {
        ActionMessages actionMessages = new ActionMessages();
        for (ErrorMessage errorMessage : this) {
            actionMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(errorMessage.getErrorKey(), errorMessage.getMessageParameters()));
        }
        return actionMessages;
    }
}
