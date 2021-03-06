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

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CsrfValidator}
 */
public class CsrfValidatorTest {

    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void testValidateCsrf_NonUpdateHttpMethods() throws Exception {

        String sessionToken = CsrfValidator.getSessionToken(request);
        assertNull(sessionToken);

        // check GET
        request.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        sessionToken = CsrfValidator.getSessionToken(request);
        assertNotNull(sessionToken);

        // check OPTIONS
        request.setMethod("OPTIONS");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // let's also verify that the session token doesn't change since it should be the same session
        String sessionToken2 = CsrfValidator.getSessionToken(request);
        assertEquals(sessionToken, sessionToken2);

        // check HEAD
        request.setMethod("HEAD");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // verify that if we use a new session, we get a different session token
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        request2.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request2, response2));

        assertNotEquals(CsrfValidator.getSessionToken(request), CsrfValidator.getSessionToken(request2));

    }

    @Test
    public void testValidateCsrf_Valid() {
        // first we run a GET to establish the CSRF token
        request.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // next let's do a POST and make sure we send the same token, and it should be valid
        String sessionToken = CsrfValidator.getSessionToken(request);
        request.setMethod("POST");
        request.setParameter(CsrfValidator.CSRF_PARAMETER, sessionToken);
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // and just verify we still have the same session token after the POST
        assertEquals(sessionToken, CsrfValidator.getSessionToken(request));
    }

    /**
     * Tests the situation where a POST is made against a session without a csrf token.
     */
    @Test
    public void testValidateCsrf_Invalid_EmptySession() {
        // do a POST with no parameter, it should fail
        request.setMethod("POST");
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // now do a POST with a parameter, still should fail because the session has no csrf token
        request.setMethod("POST");
        request.setParameter(CsrfValidator.CSRF_PARAMETER, UUID.randomUUID().toString());
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());
    }

    /**
     * Tests the situation where a POST is made against a session that has a csrf token but the tokens don't match
     */
    @Test
    public void testValidateCsrf_Invalid_TokenMismatch() {
        // first we run a GET to establish the CSRF token
        request.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // do a POST with no parameter, it should fail
        request.setMethod("POST");
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());

        // reset the response
        response = new MockHttpServletResponse();

        // now do the POST with a parameter, it should fail because the csrf tokens won't match
        request.setParameter(CsrfValidator.CSRF_PARAMETER, UUID.randomUUID().toString());
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());
    }


}
