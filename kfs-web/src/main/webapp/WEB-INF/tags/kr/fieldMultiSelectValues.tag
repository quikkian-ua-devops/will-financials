<%--

    The Kuali Financial System, a comprehensive financial management system for higher education.

    Copyright 2005-2017 Kuali, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="field" required="true" type="org.kuali.kfs.kns.web.ui.Field" description="The field to render option lines for." %>

<c:forEach items="${field.fieldValidValues}" var="select">
	<c:set var="propertySelected" value="${false}"/>
	<c:forEach items="${field.propertyValues}" var="propertyValue">
		<c:if test="${propertyValue eq select.key}">
			<c:set var="propertySelected" value="${true}"/>
		</c:if>
    </c:forEach>
    <option ${propertySelected ? 'selected="selected"' : ''}
            value='<c:out value="${select.key}"/>'><%--${select.htmlSpacePadding}--%><c:out value="${select.value}" /></option>
</c:forEach>
