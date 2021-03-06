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
<%@ attribute name="assetPayments" type="java.util.List" required="true" description="Payments list" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<%@ attribute name="assetValueObj" type="java.lang.String" required="false" description="Asset object name" %>
<%@ attribute name="assetValue" type="org.kuali.kfs.module.cam.businessobject.Asset" required="false" description="Asset object value" %>

<c:if test="${fn:length(assetPayments) <= CamsConstants.Asset.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY}">
	<c:if test="${assetValueObj==null}">
		<c:set var="assetValueObj" value="document.asset" />
		<c:set var="assetValue" value="${KualiForm.document.asset}" />
	</c:if>
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPayment.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	<c:set var="pos" value="-1" />

	<kul:tab tabTitle="Processed Payments" defaultOpen="${!defaultTabHide}" useCurrentTabIndexAsKey="true">
		<div class="tab-container">
			<table class="standard side-margins">
				<tr class="header">
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
					<th class="grid"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true" /></th>
					<th class="grid right"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true" /></th>
				</tr>
				<c:set var="totalPayments" value="${0}" />
				<c:forEach var="payment" items="${assetPayments}">
					<c:set var="totalPayments" value="${totalPayments + payment.accountChargeAmount}"/>
					<c:set var="pos" value="${pos+1}" />
					<c:set var="posValue" value="${pos}" />
					<tr>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].chartOfAccountsCode" attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true"/></td>
						<td class="grid">
							<kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].accountNumber" attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" readOnlyBody="true">
								<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${assetValue.assetPayments[posValue].chartOfAccountsCode}&amp;accountNumber=${assetValue.assetPayments[posValue].accountNumber}" render="true">
									<html:hidden write="true" property="${assetValueObj}.assetPayments[${pos}].accountNumber" />
								</kul:inquiry>&nbsp;
							</kul:htmlControlAttribute>
						</td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].subAccountNumber" attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialObjectCode" attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialSubObjectCode" attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].projectCode" attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].organizationReferenceId" attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].documentNumber" attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentTypeCode" attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].purchaseOrderNumber" attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].requisitionNumber" attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingDate" attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingYear" attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingPeriodCode" attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true"/></td>
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].transferPaymentCode" attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true"/></td>
						<td class="grid right"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].accountChargeAmount" attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true"/></td>
					</tr>
				</c:forEach>
				<tr>
					<th class="grid right" colspan="15"><kul:htmlAttributeLabel noColon="true" attributeEntry="${assetAttributes.paymentTotalCost}" readOnly="true" /></th>
					<td class="grid right"><fmt:formatNumber value="${totalPayments}" maxFractionDigits="2" minFractionDigits="2"/></td>
				</tr>
			</table>
		</div>
</kul:tab>
</c:if>
<cams:assetPaymentsLookupLink capitalAssetNumber="${assetValue.capitalAssetNumber}"/>
