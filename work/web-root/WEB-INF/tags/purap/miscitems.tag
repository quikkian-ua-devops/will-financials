<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="overrideTitle" required="false"
	description="The title to be used for this section." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="showAmount" required="false"
    type="java.lang.Boolean"
    description="show the amount if true else percent" %>

<c:if test="${empty overrideTitle}">
	<c:set var="overrideTitle" value="Misc Items"/>
</c:if>

<c:set var="amendmentEntry"
	value="${(!empty KualiForm.editingMode['amendmentEntry'])}" />

<tr>
	<td colspan="10" class="subhead"><span class="subhead-left"><c:out value="${overrideTitle}" /></span></td>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell colspan="5"
		attributeEntry="${itemAttributes.itemTypeCode}" />
	<kul:htmlAttributeHeaderCell colspan="2"
		attributeEntry="${itemAttributes.extendedPrice}" />
	<kul:htmlAttributeHeaderCell colspan="2"
		attributeEntry="${itemAttributes.itemDescription}" />
</tr>

<logic:iterate indexId="ctr" name="KualiForm" property="document.items"
	id="itemLine">
	<%-- to ensure order this should pull out items from APC instead of this--%>
	<c:if test="${itemLine.itemType.itemTypeAboveTheLineIndicator != true}">
		<tr>
			<td colspan="10" class="tab-subhead" style="border-right: none;">
			<kul:htmlControlAttribute
				attributeEntry="${itemAttributes.itemTypeCode}"
				property="document.item[${ctr}].itemType.itemTypeDescription"
				readOnly="${true}" /> <!-- TODO need the show/hide? --></td>
		</tr>
		<tr>
			<td class="infoline" colspan="5">
			    <html:hidden property="document.item[${ctr}].itemIdentifier" /> 
			    <html:hidden property="document.item[${ctr}].purapDocumentIdentifier" />
			    <html:hidden property="document.item[${ctr}].versionNumber" /> 
			    <html:hidden property="document.item[${ctr}].itemTypeCode" /> 
			    <html:hidden property="document.item[${ctr}].itemType.itemTypeCode" /> 
			    <html:hidden property="document.item[${ctr}].itemType.itemTypeDescription" /> 
			    <html:hidden property="document.item[${ctr}].itemType.active" /> 
			    <html:hidden property="document.item[${ctr}].itemType.quantityBasedGeneralLedgerIndicator" />
 			    <html:hidden property="document.item[${ctr}].itemType.itemTypeAboveTheLineIndicator" />
			    <div align="right">
			        <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemType.itemTypeDescription" readOnly="${true}" />:&nbsp;
			    </div>
			</td>
			<td class="infoline" colspan="2">
			<div align="right"><kul:htmlControlAttribute
				attributeEntry="${itemAttributes.itemUnitPrice}"
				property="document.item[${ctr}].itemUnitPrice"
				readOnly="${not (fullEntryMode or amendmentEntry)}" styleClass="amount" /></div>
			</td>
			<td class="infoline" colspan="2"><kul:htmlControlAttribute
				attributeEntry="${itemAttributes.itemDescription}"
				property="document.item[${ctr}].itemDescription"
				readOnly="${not (fullEntryMode or amendmentEntry)}" /></td>
		</tr>

		<c:if test="${amendmentEntry}">
			<purap:puraccountingLineCams
				editingMode="${KualiForm.accountingLineEditingMode}"
				editableAccounts="${KualiForm.editableAccounts}"
				sourceAccountingLinesOnly="true" optionalFields="accountLinePercent"
				extraHiddenFields=",accountIdentifier,itemIdentifier"
				accountingLineAttributes="${accountingLineAttributes}"
				accountPrefix="document.item[${ctr}]." hideTotalLine="true"
				hideFields="amount" accountingAddLineIndex="${ctr}"
				suppressCams="${true}" overrideTitle="Item Accounting Lines" />
		</c:if>
		<c:if test="${!amendmentEntry}">
			<c:set var="optionalFields" value="accountLinePercent" />
			<c:set var="extraHiddenFields" value=",accountIdentifier,itemIdentifier" />
			<c:set var="hideFields" value="amount" />
			<c:if test="${showAmount}">
				<c:set var="optionalFields" value="" />
				<c:set var="extraHiddenFields" value=",accountIdentifier,itemIdentifier,accountLinePercent" />
				<c:set var="hideFields" value="" />
			</c:if>
			<purap:puraccountingLineCams editingMode="${KualiForm.editingMode}"
				editableAccounts="${KualiForm.editableAccounts}"
				sourceAccountingLinesOnly="true" optionalFields="${optionalFields}"
				extraHiddenFields="${extraHiddenFields}"
				accountingLineAttributes="${accountingLineAttributes}"
				accountPrefix="document.item[${ctr}]." hideTotalLine="true"
				hideFields="${hideFields}" accountingAddLineIndex="${ctr}"
				suppressCams="${true}" overrideTitle="Item Accounting Lines" />
		</c:if>
	</c:if>
</logic:iterate>

