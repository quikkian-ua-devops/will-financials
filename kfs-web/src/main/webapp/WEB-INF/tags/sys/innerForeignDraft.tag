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

<c:set var="wireTransAttributes" value="${DataDictionary.PaymentSourceWireTransfer.attributes}" />

<table class="datatable standard side-margins" summary="Foreign Draft Section" cellpadding="0">
	<tbody>
        <c:if test="${!fullEntryMode&&!frnEntryMode}">
            <tr>
			    <td>
                    <c:if test="${KualiForm.document.wireTransfer.foreignCurrencyTypeCode=='C'}">
                        Payment amount is stated in U.S. dollars; convert to foreign currency
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>
                    <c:if test="${KualiForm.document.wireTransfer.foreignCurrencyTypeCode=='F'}">
                        Payment amount is stated in foreign currency
                    </c:if>
                </td>
            </tr>
        </c:if>
        <c:if test="${fullEntryMode||frnEntryMode}">
            <tr>
                <td>
                    <html:radio
                            styleId="us-currency"
                            property="document.wireTransfer.foreignCurrencyTypeCode"
                            value="C"/>

                    <label for="us-currency">
                        Payment amount is stated in U.S. dollars; convert to foreign currency
                    </label>
                </td>
            </tr>
            <tr>
                <td>
                    <html:radio
                            styleId="foreign-currency"
                            property="document.wireTransfer.foreignCurrencyTypeCode"
                            value="F"/>
                    <label for="foreign-currency">
                        Payment amount is stated in foreign currency
                    </label>
                </td>
            </tr>
        </c:if>
        <tr>
            <td>
                <kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.currencyTypeName}"/>
                <kul:htmlControlAttribute
                        attributeEntry="${wireTransAttributes.currencyTypeName}"
                        property="document.wireTransfer.foreignCurrencyTypeName"
                        readOnly="${!fullEntryMode&&!frnEntryMode}"/>
			</td>
		</tr>
	</tbody>
</table>
