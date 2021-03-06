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
package org.kuali.kfs.sys.rest.service;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.collections.map.HashedMap;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.rest.ErrorMessage;
import org.kuali.kfs.sys.rest.exception.ApiRequestException;
import org.kuali.kfs.sys.rest.resource.BusinessObjectApiResource;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class SearchParameterServiceTest {

    private BusinessObjectApiResource apiResource;
    private PersistenceStructureService persistenceStructureService;
    private SearchParameterService searchParameterService = new SearchParameterService();

    @Before
    public void setup() {
        apiResource = new BusinessObjectApiResource("sys");
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
        searchParameterService.setPersistenceStructureService(persistenceStructureService);
    }

    @Test
    public void testGetLimit() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "5");

        int limit = searchParameterService.getLimit(Bank.class, params);

        Assert.assertEquals(5, limit);
    }

    @Test
    @PrepareForTest({LookupUtils.class})
    public void testGetLimit_NegativeLimitSpecified() {
        PowerMock.mockStatic(LookupUtils.class);
        EasyMock.expect(LookupUtils.getSearchResultsLimit(Bank.class)).andReturn(200);
        PowerMock.replay(LookupUtils.class);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "-1");

        int limit = searchParameterService.getLimit(Bank.class, params);

        Assert.assertEquals(200, limit);
        PowerMock.verify(LookupUtils.class);
    }

    @Test
    @PrepareForTest({LookupUtils.class})
    public void testGetLimit_BusinessObjectResultsLimitNegative() {
        PowerMock.mockStatic(LookupUtils.class);
        EasyMock.expect(LookupUtils.getSearchResultsLimit(Bank.class)).andReturn(-1);
        EasyMock.expect(LookupUtils.getApplicationSearchResultsLimit()).andReturn(200);
        PowerMock.replay(LookupUtils.class);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        int limit = searchParameterService.getLimit(Bank.class, params);

        Assert.assertEquals(200, limit);
        PowerMock.verify(LookupUtils.class);
    }

    @Test
    public void testGetSortCriteria() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountName");

        String[] sort = searchParameterService.getSortCriteria(Account.class, params, validFields);

        Assert.assertEquals(new String[]{"accountName"}, sort);
    }

    @Test
    public void testGetSortCriteria_NotSpecified() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Account.class)).andReturn(Arrays.asList("chartOfAccountsCode", "accountName"));
        EasyMock.replay(persistenceStructureService);

        String[] sort = searchParameterService.getSortCriteria(Account.class, params, validFields);

        Assert.assertEquals(new String[]{"chartOfAccountsCode","accountName"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_NotSpecified_NoPrimaryKey() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = searchParameterService.getSortCriteria(Account.class, params, validFields);

        Assert.assertEquals(new String[]{"objectId"}, sort);
    }

    @Test
    public void testGetSortCriteria_NotSpecified_NoPrimaryKey_NoObjectId_NoNothing() {
        List<String> validFields = Arrays.asList("accountName", "accountNumber", "chartOfAccountsCode");
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = searchParameterService.getSortCriteria(Account.class, params, validFields);

        Assert.assertEquals(new String[]{"accountName"}, sort);
    }

    @Test
    public void testGetSortCriteria_Descending() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "-accountName");

        String[] sort = searchParameterService.getSortCriteria(Account.class, params, validFields);

        Assert.assertEquals(new String[]{"-accountName"}, sort);
    }

    @Test
    public void testGetSortCriteria_Mutli() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountName,accountNumber");

        String[] sort = searchParameterService.getSortCriteria(Account.class, params, validFields);

        Assert.assertEquals(new String[]{"accountName", "accountNumber"}, sort);
    }

    @Test
    public void testGetSortCriteria_MutliBad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountname,accountnumber");

        try {
            searchParameterService.getSortCriteria(Account.class, params, validFields);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid sort field", "class"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(2, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("accountname", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(1).getMessage());
            Assert.assertEquals("accountnumber", ((List<ErrorMessage>)error.get("details")).get(1).getProperty());
        }

    }

    @Test
    public void testGetSortCriteria_Bad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "class");

        try {
            searchParameterService.getSortCriteria(Account.class, params, validFields);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid sort field", "class"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("class", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }
    }

    @Test
    public void testGetSortCriteria_SortByMaintainableFieldInvalidOjbField() {
        List<String> maitainableFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode", "closed");
        List<String> ojbFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        List<String> validFields = new ArrayList<>(maitainableFields);
        validFields.retainAll(ojbFields);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "closed");

        try {
            searchParameterService.getSortCriteria(Account.class, params, validFields);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid sort field", "closed"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("closed", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }
    }

    @Test
    public void testGetSearchQueryCriteria() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountName", "bob");

        Map<String, String> criteria = searchParameterService.getSearchQueryCriteria(params, validFields);

        Map<String, String> validCriteria = new HashMap<>();
        validCriteria.put("accountName", "bob");
        Assert.assertEquals(validCriteria, criteria);
    }

    @Test
    public void testGetSearchQueryCriteria_Bad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountname", "bob");

        try {
            searchParameterService.getSearchQueryCriteria(params, validFields);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid query parameter name", "accountname"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("accountname", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }
    }

    @Test
    public void testGetSearchQueryCriteria_MultiBad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountname", "bob");
        params.add("accountnumber", "bfdsa5432adfdsaf");

        try {
            searchParameterService.getSearchQueryCriteria(params, validFields);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid query parameter name", "accountname"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(2, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(1).getMessage());
        }
    }

    @Test
    public void testGetIntQueryParameter_Bad() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "a");

        try {
            searchParameterService.getIntQueryParameter("limit", params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("parameter is not a number", "limit"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("parameter is not a number", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("limit", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }
    }

    @Test
    public void testGetIntQueryParameter() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "3");

        int limit = searchParameterService.getIntQueryParameter("limit", params);
        Assert.assertEquals(3, limit);
    }

    @Test
    public void testGetDateQueryParameter_Bad() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("beforeDate", "foo!!");

        try {
            searchParameterService.getDateQueryParameter("beforeDate", params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("parameter is not a valid ISO8601 date", "beforeDate"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("parameter is not a valid Unix time value", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("beforeDate", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }
    }

    @Test
    public void testGetDateQueryParameter() {
        for (Long input : Arrays.asList(1458694800L, 1458721800L, 1458720000L, 1458721800L, 1458721815L, 1397866200L, 1155182400L, 1331337600L )) {
            MultivaluedMap<String, String> params = new MultivaluedMapImpl();
            params.add("beforeDate", String.valueOf(input * 1000L));
            Instant beforeDate = searchParameterService.getDateQueryParameter("beforeDate", params);
            Assert.assertEquals(input, (Long)beforeDate.getEpochSecond());
        }
    }
}
