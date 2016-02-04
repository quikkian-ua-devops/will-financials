<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="reportLookupActionName" required="true" description="The controller that the report lookup should post against to rebuild search results." %>

<display:table class="datatable-100"
			    name="${reqSearchResults}"
			    id="row"
			    export="true"
				pagesize="100"
				defaultsort="4"
				defaultorder="descending"
				requestURI="${reportLookupAction}?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">

	<c:forEach items="${row.columns}" var="column">
		<c:choose>
			<c:when test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">
				<display:column class="numbercell"
			sortable="true"
			decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
			title="${column.columnTitle}"
			comparator="${column.comparator}">
					<c:choose>
						<c:when test="${column.propertyURL != \"\"}">
							<a href="<c:out value="${column.propertyURL}"/>"
							   title="${column.propertyValue}"
							   target="blank">
							   <c:out value="${column.propertyValue}" />
							</a>	
						</c:when>
						<c:otherwise>
							<c:out value="${column.propertyValue}" />
						</c:otherwise>
					</c:choose>
				</display:column>
			</c:when>
			<c:otherwise>
				<display:column class="infocell"
					sortable="${column.sortable}"
					decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
					title="${column.columnTitle}"
					comparator="${column.comparator}">
					<c:choose>
						<c:when test="${column.propertyURL != \"\"}">
							<a href="<c:out value="${column.propertyURL}"/>"
							   title="${column.propertyValue}"
							   target="blank">
							   <c:out value="${column.propertyValue}" />
							</a>
						</c:when>
						<c:otherwise>
							<c:out value="${column.propertyValue}" />
						</c:otherwise>
					</c:choose>
				</display:column>
			</c:otherwise>
		</c:choose>	
	</c:forEach>
</display:table>
