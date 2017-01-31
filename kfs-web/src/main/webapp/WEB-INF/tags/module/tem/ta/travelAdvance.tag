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
<%@ attribute name="travelAdvanceProperty" required="true" type="java.lang.String" description="Name of the property which holds the path to the travel advance to render."%>
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="travelAdvanceAttributes" value="${DataDictionary.TravelAdvance.attributes}" />
<c:set var="advanceAttributes" value="${DataDictionary.AdvancePaymentReason.attributes}" />
<c:set var="docType" value="${KualiForm.document.dataDictionaryEntry.documentTypeName }" />
<c:set var="policyDisabled" value="${!KualiForm.waitingOnTraveler && !fullEntryMode}" />
<c:set var="advancePolicyMode" value="${KualiForm.editingMode['advancePolicyEntry']}" scope="request"/>



<table cellpadding="0" cellspacing="0" class="datatable" summary="Travel Advance Section">
	<tr>
		<th class="right">
            <kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}" />
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}"
				property="${travelAdvanceProperty}.travelAdvanceRequested"
				readOnly="${!fullEntryMode && !conversionRateEntryMode}" />
		</td>
	</tr>
	<tr>
		<th class="right">
            <kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arCustomerId}" />
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.arCustomerId}"
				property="${travelAdvanceProperty}.arCustomerId" readOnly="true" />
		</td>
	</tr>
	<tr>
		<th class="right">
            <kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}" />
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}"
				property="${travelAdvanceProperty}.arInvoiceDocNumber"
				readOnly="true" />
		</td>
	</tr>
	<tr>
		<th class="right">
            <kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.dueDate}" />
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.dueDate}"
				property="${travelAdvanceProperty}.dueDate" datePicker="true"
				readOnly="${!fullEntryMode}" />
		</td>
	</tr>
	<tr>
		<th class="right">
            <kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}" />
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}"
				property="${travelAdvanceProperty}.advancePaymentReasonCode"
				readOnly="${!fullEntryMode}" />
		</td>
	</tr>
	<tr>
		<th class="right">
            <kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}" />
		</th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}"
				property="${travelAdvanceProperty}.travelAdvancePolicy"
				readOnly="${!advancePolicyMode}" />${KualiForm.policyURL}</td>
	</tr>
	<tr>
		<th class="right">
            <kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.additionalJustification}" />
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.additionalJustification}"
				property="${travelAdvanceProperty}.additionalJustification"
				readOnly="${!fullEntryMode}" />
		</td>
	</tr>
	<c:if test="${clearAdvanceMode}">
		<tr>
			<td class="datacell" colspan="2">
				<html:submit property="methodToCall.clearAdvance" title="Clear Advance" alt="Clear Advance" styleClass="btn btn-default" value="Clear" />
			</td>
		</tr>
	</c:if>
</table>
