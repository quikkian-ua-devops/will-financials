<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2017 Kuali, Inc.
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

<%@ attribute name="itemInProcessProperty" required="true" %>
<%@ attribute name="creatingItemInProcess" required="false" %>

<c:set var="itemInProcessAttributes" value="${DataDictionary.CashieringItemInProcess.attributes}" />
<tr>
  <c:if test="${!creatingItemInProcess}"><td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemIdentifier" attributeEntry="${itemInProcessAttributes.itemIdentifier}" /></td></c:if>
  <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemAmount" attributeEntry="${itemInProcessAttributes.itemAmount}" /></td>
  <c:if test="${!creatingItemInProcess}">
    <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemReducedAmount" attributeEntry="${itemInProcessAttributes.itemReducedAmount}" /></td>
    <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemRemainingAmount" attributeEntry="${itemInProcessAttributes.itemRemainingAmount}" /></td>
    <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.currentPayment" attributeEntry="${itemInProcessAttributes.currentPayment}" /></td>
  </c:if>
  <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemOpenDate" attributeEntry="${itemInProcessAttributes.itemOpenDate}" /></td>
  <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemDescription" attributeEntry="${itemInProcessAttributes.itemDescription}" /></td>
</tr>
