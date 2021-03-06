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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<c:set var="disbursementNumberRangeAttributes" value="${DataDictionary.DisbursementNumberRange.attributes}"/>
<c:set var="bankAttributes" value="${DataDictionary.Bank.attributes}"/>
<c:set var="customerProfileAttributes" value="${DataDictionary.CustomerProfile.attributes}"/>
<c:set var="paymentGroupAttributes" value="${DataDictionary.PaymentGroup.attributes}"/>
<c:set var="dummyAttributes" value="${DataDictionary.AttributeReferenceDummy.attributes}"/>

<kul:page headerTitle="Format Disbursements"
          transactionalDocument="false" showDocumentInfo="false" errorKey="foo"
          htmlFormAction="pdp/format" docTitle="Format Disbursements">

    <c:if test="${empty ErrorPropertyList}">
        <table width="100%" border="0">
            <tr>
                <td><kul:errors keyMatch="${Constants.GLOBAL_ERRORS}" errorTitle="Errors Found On Page:"/></td>
            </tr>
        </table>

        <pdp:formatDisbursementRanges
                disbursementNumberRangeAttributes="${disbursementNumberRangeAttributes}"
                bankAttributes="${bankAttributes}"/>
        <pdp:formatOptions paymentGroupAttributes="${paymentGroupAttributes}"/>
        <pdp:formatCustomers
                customerProfileAttributes="${customerProfileAttributes}"
                dummyAttributes="${dummyAttributes}"/>
        <div id="globalbuttons" class="globalbuttons">
            <html:submit
                    styleClass="btn btn-default"
                    property="methodToCall.prepare"
                    title="begin format"
                    alt="begin format"
                    value="Begin Format"/>
            <html:submit
                    styleClass="btn btn-default"
                    property="methodToCall.start"
                    title="reset"
                    alt="reset"
                    value="Reset"/>
            <html:submit
                    styleClass="btn btn-default"
                    property="methodToCall.clear"
                    title="clear"
                    alt="clear"
                    value="Clear"/>
        </div>

        <kul:stickyGlobalButtons bodySelector="main.content"/>
    </c:if>
</kul:page>

