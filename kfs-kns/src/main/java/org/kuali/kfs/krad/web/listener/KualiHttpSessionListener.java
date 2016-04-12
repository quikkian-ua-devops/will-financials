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
package org.kuali.kfs.krad.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.krad.document.authorization.PessimisticLock;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.GlobalVariables;

import java.util.List;

/**
 * This class is used to handle session timeouts where {@link PessimisticLock} objects should
 * be removed from a document 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiHttpSessionListener implements HttpSessionListener {

    /**
     *  HttpSession hook for additional setup method when sessions are created
     *
     * @param se - the HttpSessionEvent containing the session
     *
     *  @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se) {
        // no operation required at this time
    }

    /**
     * HttpSession hook for additional cleanup when sessions are destroyed
     *
     * @param se - the HttpSessionEvent containing the session
     * 
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        releaseLocks();
    }

    /**
     * Remove any locks that the user has for this session
     */
    private void releaseLocks() {
        String sessionId = GlobalVariables.getUserSession().getKualiSessionId();
        List<PessimisticLock> locks = KRADServiceLocatorWeb.getPessimisticLockService().getPessimisticLocksForSession(sessionId);
        Person user = GlobalVariables.getUserSession().getPerson();

        KRADServiceLocatorWeb.getPessimisticLockService().releaseAllLocksForUser(locks, user);
    }

}

