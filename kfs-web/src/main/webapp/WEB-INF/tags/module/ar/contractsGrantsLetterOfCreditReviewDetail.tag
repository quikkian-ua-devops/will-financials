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


<!-- If there are no bills, this section should not be displayed -->

<c:set var="contractsGrantsLetterOfCreditReviewDetailAttributes" value="${DataDictionary.ContractsGrantsLetterOfCreditReviewDetail.attributes}" />

<%@ attribute name="proposalNumberValue" required="true" description="Name of form property containing the customer invoice source accounting line."%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:set var="hideRecalculateButton"
	value="${KualiForm.editingMode['hideRecalculateButton']}" scope="request" />
	
<c:set var="disableAmountToDraw"
	value="${KualiForm.editingMode['disableAmountToDraw']}" scope="request" />

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable">

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardDocumentNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.agencyNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.customerNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" width="5%" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBeginningDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardEndingDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.letterOfCreditAmount}" useShortLabel="false" hideRequiredAsterisk="true"
		align="center" />

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountAvailableToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />

	<logic:iterate indexId="ctr" name="KualiForm" property="document.headerReviewDetails" id="headerReviewDetail">

		<c:if test="${KualiForm.document.headerReviewDetails[ctr].proposalNumber == proposalNumberValue }">

			<tr>

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardDocumentNumber}"
						property="document.headerReviewDetails[${ctr}].awardDocumentNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.agencyNumber}"
						property="document.headerReviewDetails[${ctr}].agencyNumber" readOnly="true" /></td>
				<td class="datacell" width="5%"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.customerNumber}"
						property="document.headerReviewDetails[${ctr}].customerNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBeginningDate}"
						property="document.headerReviewDetails[${ctr}].awardBeginningDate" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardEndingDate}"
						property="document.headerReviewDetails[${ctr}].awardEndingDate" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}"
						property="document.headerReviewDetails[${ctr}].awardBudgetAmount" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.letterOfCreditAmount}"
						property="document.headerReviewDetails[${ctr}].letterOfCreditAmount" readOnly="true" /></td>

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}"
						property="document.headerReviewDetails[${ctr}].claimOnCashBalance" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}"
						property="document.headerReviewDetails[${ctr}].amountToDraw" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountAvailableToDraw}"
						property="document.headerReviewDetails[${ctr}].amountAvailableToDraw" readOnly="true" /></td>


			</tr>

			<%-- generate unique tab key from invPropertyName --%>
			<c:set var="invPropertyName" value="document.headerReviewDetails[${ctr}]" />
			<c:set var="lineNumber" value="${ctr }" />
		</c:if>
	</logic:iterate>
</table>

<c:set var="tabKey" value="${kfunc:generateTabKey(invPropertyName)}" />
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />

<%-- default to closed --%>
<c:choose>
	<c:when test="${empty currentTab}">
		<c:set var="isOpen" value="false" />
	</c:when>
	<c:when test="${!empty currentTab}">
		<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
	</c:when>
</c:choose>

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<td class="tab-subhead" style="border-right: none;">Accounts <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<html:submit
						property="methodToCall.toggleTab.tab${tabKey}"
						alt="hide"
						title="toggle"
						styleClass="btn btn-default small"
						styleId="tab-${tabKey}-imageToggle"
						onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}'); "
						value="Hide"/>
			</c:if> <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
				<html:submit
						property="methodToCall.toggleTab.tab${tabKey}"
						alt="show"
						title="toggle"
						styleClass="btn btn-default small"
						styleId="tab-${tabKey}-imageToggle"
						onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}'); "
						value="Show"/>
			</c:if><c:if test="${hideRecalculateButton != 'true' && hideRecalculateButton != 'TRUE'}">
				<html:submit
						property="methodToCall.recalculateAmountToDraw.line${lineNumber}"
						title="Recalculate"
						alt="Recalculate"
						styleClass="btn btn-default small"
						value="Recalculate"/>
		</c:if>
		</td>
	</tr>
</table>


<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	<div style="display: block;" id="tab-${tabKey}-div" class="accountsInfo">
</c:if>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	<div style="display: none;" id="tab-${tabKey}-div" class="accountsInfo">
</c:if>

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable" border="0">

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountDescription}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.chartOfAccountsCode}" useShortLabel="false"
		hideRequiredAsterisk="true" width="5%" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountExpirationDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
		
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />		
	
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.fundsNotDrawn}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />


	<logic:iterate indexId="ctr" name="KualiForm" property="document.accountReviewDetails" id="accountReviewDetail">
		<c:if test="${KualiForm.document.accountReviewDetails[ctr].proposalNumber == proposalNumberValue }">

			<tr>

				</td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountDescription}"
						property="document.accountReviewDetails[${ctr}].accountDescription" readOnly="true" /></td>
				<td class="datacell" width="5%"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.chartOfAccountsCode}"
						property="document.accountReviewDetails[${ctr}].chartOfAccountsCode" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountNumber}"
						property="document.accountReviewDetails[${ctr}].accountNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountExpirationDate}"
						property="document.accountReviewDetails[${ctr}].accountExpirationDate" readOnly="true" />
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}"
						property="document.accountReviewDetails[${ctr}].awardBudgetAmount" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}"
						property="document.accountReviewDetails[${ctr}].claimOnCashBalance" readOnly="true" /></td>
						
				<c:choose>
					<c:when test="${disableAmountToDraw}">
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}"
								property="document.accountReviewDetails[${ctr}].amountToDraw" readOnly="true" /></td>
					</c:when>
					<c:otherwise>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}"
								property="document.accountReviewDetails[${ctr}].amountToDraw" readOnly="${readOnly}" /></td>
					</c:otherwise>
				</c:choose>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.fundsNotDrawn}"
						property="document.accountReviewDetails[${ctr}].fundsNotDrawn" readOnly="true" /></td>
			</tr>
		</c:if>
	</logic:iterate>
</table>


