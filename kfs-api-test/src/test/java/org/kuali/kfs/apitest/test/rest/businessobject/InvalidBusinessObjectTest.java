/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.apitest.test.rest.businessobject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvalidBusinessObjectTest {
    private static String BAD_API = "/api/v1/business-object/vnd/vendor-headers/xxx";
    private static String GOOD_API = "/api/v1/business-object/vnd/vendor-headers/830E61147605C23CE0404F8189D82CFD";
    private static String BAD_SEARCH_API = "/api/v1/business-object/vnd/vendor-headers?objectId=xxx";
    private static String GOOD_SEARCH_API = "/api/v1/business-object/vnd/vendor-headers?objectId=830E61147605C23CE0404F8189D82CFD";

    @Test
    public void invalidBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidSearchBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(), results);
        Assert.assertEquals(new ArrayList(Arrays.asList("vendorHeaderGeneratedIdentifier")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(0, searchResults.get(Constants.Search.TOTAL_COUNT));
        Map<String, String> query = new HashMap<String, String>();
        query.put("objectId", "xxx");
        Assert.assertEquals(query, searchResults.get(Constants.Search.QUERY));
    }

    @Test
    public void validSearchBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }
}