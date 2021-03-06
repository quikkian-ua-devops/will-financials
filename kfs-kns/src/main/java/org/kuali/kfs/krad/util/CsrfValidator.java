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
package org.kuali.kfs.krad.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.util.UUID;

/**
 * Simple utility class that will validate the given request to determine if it has any required CSRF information,
 * setting appropriate response errors if not.
 */
public class CsrfValidator {

    private static final Logger LOG = Logger.getLogger(CsrfValidator.class);

    public static final String CSRF_PARAMETER = "csrfToken";
    public static final String CSRF_SESSION_TOKEN = "csrfSessionToken";

    /**
     * Applies CSRF protection for any HTTP method other than GET, HEAD, or OPTIONS.
     *
     * @param request  the http request to check
     * @param response the http response associated with the given request
     * @return true if the request validated successfully, false otherwise. If false is returned, calling code should
     * act immediately to terminate any additional work performed on the response.
     */
    public static boolean validateCsrf(HttpServletRequest request, HttpServletResponse response) {
        if (HttpMethod.GET.equals(request.getMethod()) ||
            HttpMethod.HEAD.equals(request.getMethod()) ||
            HttpMethod.OPTIONS.equals(request.getMethod())) {
            // if it's a GET and there's not already a CSRF token, then we need to generate and place a CSRF token
            placeSessionToken(request);
        } else {
            String givenCsrf = getRequestToken(request);
            String actualCsrf = getSessionToken(request);
            if (actualCsrf == null) {
                LOG.error("CSRF check failed because no CSRF token has been established on the session");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            } else if (!StringUtils.equals(givenCsrf, actualCsrf)) {
                LOG.error("CSRF check failed, actual value was: " + actualCsrf + ", given value was: " + givenCsrf + ", requested URL was: " + request.getRequestURL());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
        return true;
    }


    /**
     * Retrieve the CSRF token that is associated with the session for the given request, or null if the session has none.
     *
     * @param request the request to check the session for the CSRF token
     * @return the CSRF token on the request's session, or null if the session has none
     */
    public static String getSessionToken(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(CSRF_SESSION_TOKEN);
    }

    /**
     * Retrieve the CSRF token parameter that is on the given request, or null if the request has none.
     *
     * @param request the request to check for the CSRF token parameter
     * @return the CSRF token parameter on the request, or null if the request has none
     */
    public static String getRequestToken(HttpServletRequest request) {
        return request.getParameter(CSRF_PARAMETER);
    }

    /**
     * If the session associated with the given request has no CSRF token, this method will generate that token and
     * add it as an attribute on the session. If the session already has a CSRF token, this method will do nothing.
     *
     * @param request the request with the session on which to place the session token if needed
     */
    private static void placeSessionToken(HttpServletRequest request) {
        if (getSessionToken(request) == null) {
            request.getSession().setAttribute(CSRF_SESSION_TOKEN, UUID.randomUUID().toString());
        }
    }

}
