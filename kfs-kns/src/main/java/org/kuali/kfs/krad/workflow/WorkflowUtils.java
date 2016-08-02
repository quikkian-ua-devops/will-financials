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
package org.kuali.kfs.krad.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.rule.xmlrouting.WorkflowFunctionResolver;
import org.kuali.rice.kew.rule.xmlrouting.WorkflowNamespaceContext;
import org.kuali.kfs.kns.util.FieldUtils;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.Row;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.w3c.dom.Document;


public final class WorkflowUtils {
    private static final String XPATH_ROUTE_CONTEXT_KEY = "_xpathKey";
    public static final String XSTREAM_SAFE_PREFIX = "wf:xstreamsafe('";
    public static final String XSTREAM_SAFE_SUFFIX = "')";
    public static final String XSTREAM_MATCH_ANYWHERE_PREFIX = "//";
    public static final String XSTREAM_MATCH_RELATIVE_PREFIX = "./";

	private WorkflowUtils() {
		throw new UnsupportedOperationException("do not call");
	}
    
    /**
     *
     * This method sets up the XPath with the correct workflow namespace and resolver initialized. This ensures that the XPath
     * statements can use required workflow functions as part of the XPath statements.
     *
     * @param document - document
     * @return a fully initialized XPath instance that has access to the workflow resolver and namespace.
     *
     */
    public final static XPath getXPath(Document document) {
        XPath xpath = getXPath(RouteContext.getCurrentRouteContext());
        xpath.setNamespaceContext(new WorkflowNamespaceContext());
        WorkflowFunctionResolver resolver = new WorkflowFunctionResolver();
        resolver.setXpath(xpath);
        resolver.setRootNode(document);
        xpath.setXPathFunctionResolver(resolver);
        return xpath;
    }

    public final static XPath getXPath(RouteContext routeContext) {
        if (routeContext == null) {
            return XPathFactory.newInstance().newXPath();
        }
        if (!routeContext.getParameters().containsKey(XPATH_ROUTE_CONTEXT_KEY)) {
            routeContext.getParameters().put(XPATH_ROUTE_CONTEXT_KEY, XPathFactory.newInstance().newXPath());
        }
        return (XPath) routeContext.getParameters().get(XPATH_ROUTE_CONTEXT_KEY);
    }

    /**
     * This method will do a simple XPath.evaluate, while wrapping your xpathExpression with the xstreamSafe function. It assumes a
     * String result, and will return such. If an XPathExpressionException is thrown, this will be re-thrown within a
     * RuntimeException.
     *
     * @param xpath A correctly initialized XPath instance.
     * @param xpathExpression Your XPath Expression that needs to be wrapped in an xstreamSafe wrapper and run.
     * @param item The document contents you will be searching within.
     * @return The string value of the xpath.evaluate().
     */
    public static final String xstreamSafeEval(XPath xpath, String xpathExpression, Object item) {
        String xstreamSafeXPath = xstreamSafeXPath(xpathExpression);
        String evalResult = "";
        try {
            evalResult = xpath.evaluate(xstreamSafeXPath, item);
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException("XPathExpressionException occurred on xpath: " + xstreamSafeXPath, e);
        }
        return evalResult;
    }

    /**
     * This method wraps the passed-in XPath expression in XStream Safe wrappers, so that XStream generated reference links will be
     * handled correctly.
     *
     * @param xpathExpression The XPath Expression you wish to use.
     * @return Your XPath Expression wrapped in the XStreamSafe wrapper.
     */
    public static final String xstreamSafeXPath(String xpathExpression) {
        return new StringBuilder(XSTREAM_SAFE_PREFIX).append(xpathExpression).append(XSTREAM_SAFE_SUFFIX).toString();
    }

    /**
     * This method returns a label from the data dictionary service
     *
     * @param businessObjectClass - class where the label should come from
     * @param attributeName - name of the attribute you need the label for
     * @return the label from the data dictionary for the given Class and attributeName or null if not found
     */
    public static final String getBusinessObjectAttributeLabel(Class businessObjectClass, String attributeName) {
        return KRADServiceLocatorWeb.getDataDictionaryService().getAttributeLabel(businessObjectClass, attributeName);
    }


    /**
     * This method builds a workflow-lookup-screen Row of type TEXT, with no quickfinder/lookup.
     *
     * @param propertyClass The Class of the BO that this row is based on. For example, Account.class for accountNumber.
     * @param boPropertyName The property name on the BO that this row is based on. For example, accountNumber for
     *        Account.accountNumber.
     * @param workflowPropertyKey The workflow-lookup-screen property key. For example, account_nbr for Account.accountNumber. This
     *        key can be anything, but needs to be consistent with what is used for the row/field key on the java attribute, so
     *        everything links up correctly.
     * @return A populated and ready-to-use workflow lookupable.Row.
     */
    public static Row buildTextRow(Class propertyClass, String boPropertyName, String workflowPropertyKey) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("Method parameter 'propertyClass' was passed a NULL value.");
        }
        if (StringUtils.isBlank(boPropertyName)) {
            throw new IllegalArgumentException("Method parameter 'boPropertyName' was passed a NULL or blank value.");
        }
        if (StringUtils.isBlank(workflowPropertyKey)) {
            throw new IllegalArgumentException("Method parameter 'workflowPropertyKey' was passed a NULL or blank value.");
        }
        List<Field> fields = new ArrayList<Field>();
        Field field;
        field = FieldUtils.getPropertyField(propertyClass, boPropertyName, false);
        fields.add(field);
        return new Row(fields);
    }

    /**
     * This method builds a workflow-lookup-screen Row of type TEXT, with the attached lookup icon and functionality.
     *
     * @param propertyClass The Class of the BO that this row is based on. For example, Account.class for accountNumber.
     * @param boPropertyName The property name on the BO that this row is based on. For example, accountNumber for
     *        Account.accountNumber.
     * @param workflowPropertyKey The workflow-lookup-screen property key. For example, account_nbr for Account.accountNumber. This
     *        key can be anything, but needs to be consistent with what is used for the row/field key on the java attribute, so
     *        everything links up correctly.
     * @return A populated and ready-to-use workflow lookupable.Row, which includes both the property field and the lookup icon.
     */
    public static Row buildTextRowWithLookup(Class propertyClass, String boPropertyName, String workflowPropertyKey) {
        return buildTextRowWithLookup(propertyClass, boPropertyName, workflowPropertyKey, null);
    }

    /**
     * This method builds a workflow-lookup-screen Row of type TEXT, with the attached lookup icon and functionality.
     *
     * @param propertyClass The Class of the BO that this row is based on. For example, Account.class for accountNumber.
     * @param boPropertyName The property name on the BO that this row is based on. For example, accountNumber for
     *        Account.accountNumber.
     * @param workflowPropertyKey The workflow-lookup-screen property key. For example, account_nbr for Account.accountNumber. This
     *        key can be anything, but needs to be consistent with what is used for the row/field key on the java attribute, so
     *        everything links up correctly.
     * @param fieldConversionsByBoPropertyName A list of extra field conversions where the key is the business object property name
     *        and the value is the workflow property key
     * @return A populated and ready-to-use workflow lookupable.Row, which includes both the property field and the lookup icon.
     */
    public static Row buildTextRowWithLookup(Class propertyClass, String boPropertyName, String workflowPropertyKey, Map fieldConversionsByBoPropertyName) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("Method parameter 'propertyClass' was passed a NULL value.");
        }
        if (StringUtils.isBlank(boPropertyName)) {
            throw new IllegalArgumentException("Method parameter 'boPropertyName' was passed a NULL or blank value.");
        }
        if (StringUtils.isBlank(workflowPropertyKey)) {
            throw new IllegalArgumentException("Method parameter 'workflowPropertyKey' was passed a NULL or blank value.");
        }
        Field field;
        field = FieldUtils.getPropertyField(propertyClass, boPropertyName, false);

        List<Field> fields = new ArrayList<Field>();
        fields.add(field);
        return new Row(fields);
    }

    /**
     * This method builds a workflow-lookup-screen Row of type DROPDOWN.
     *
     * @param propertyClass The Class of the BO that this row is based on. For example, Account.class for accountNumber.
     * @param boPropertyName The property name on the BO that this row is based on. For example, accountNumber for
     *        Account.accountNumber.
     * @param workflowPropertyKey The workflow-lookup-screen property key. For example, account_nbr for Account.accountNumber. This
     *        key can be anything, but needs to be consistent with what is used for the row/field key on the java attribute, so
     *        everything links up correctly.
     * @param optionMap The map of value, text pairs that will be used to constuct the dropdown list.
     * @return A populated and ready-to-use workflow lookupable.Row.
     */
    public static Row buildDropdownRow(Class propertyClass, String boPropertyName, String workflowPropertyKey, Map<String, String> optionMap, boolean addBlankRow) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("Method parameter 'propertyClass' was passed a NULL value.");
        }
        if (StringUtils.isBlank(boPropertyName)) {
            throw new IllegalArgumentException("Method parameter 'boPropertyName' was passed a NULL or blank value.");
        }
        if (StringUtils.isBlank(workflowPropertyKey)) {
            throw new IllegalArgumentException("Method parameter 'workflowPropertyKey' was passed a NULL or blank value.");
        }
        if (optionMap == null) {
            throw new IllegalArgumentException("Method parameter 'optionMap' was passed a NULL value.");
        }
        List<Field> fields = new ArrayList<Field>();
        Field field;
        field = FieldUtils.getPropertyField(propertyClass, boPropertyName, false);
        fields.add(field);
        return new Row(fields);
    }
}