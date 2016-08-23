<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2016 The Kuali Foundation
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="purchaseOrder" required="false"
              description="A boolean as to whether the document is a Purchase Order."%>
<%@ attribute name="paymentRequest" required="false"
              description="A boolean as to whether the document is a PREQ."%>
<%@ attribute name="detailSectionLabel" required="true"
			  description="The label of the detail section."%>
<%@ attribute name="editableFundingSource" required="false"
			  description="Is fundingsourcecode editable?."%>
<%@ attribute name="tabErrorKey" required="false"
			  description="error map to display"%>
<%@ attribute name="editableAccountDistributionMethod" required="false"
			  description="Is editableAccountDistributionMethod editable?"%>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:if test="${empty editableFundingSource}">
	<c:set var="editableFundingSource" value="false" />
</c:if>

<c:if test="${amendmentEntry}">
	<c:if test="${KualiForm.readOnlyReceivingRequired eq 'true'}">
		<c:set var="readOnlyReceivingRequired" value="true" />
	</c:if>
</c:if>

<c:set var="useTaxIndicatorButton" value="changeusetax" scope="request" />
<c:if test="${KualiForm.document.useTaxIndicator}">
	<c:set var="useTaxIndicatorButton" value="changesalestax" scope="request" />
</c:if>

<c:set var="purapTaxEnabled" value="${(not empty KualiForm.editingMode['purapTaxEnabled'])}" />
<c:set var="contentReadOnly" value="${(not empty KualiForm.editingMode['lockContentEntry'])}" />
<c:set var="internalPurchasingReadOnly" value="${(not empty KualiForm.editingMode['lockInternalPurchasingEntry'])}" />
<c:set var="tabindexOverrideBase" value="10" />
<c:set var="poOutForQuote" value="${KualiForm.document.applicationDocumentStatus eq 'Out for Quote'}" />

<h3><c:out value="${detailSectionLabel}"/> </h3>
<kul:errors keyMatch="document.assignedUserPrincipalName,document.purchaseOrderPreviousIdentifier" displayInDiv="true"/>

<table class="datatable" summary="Detail Section">
    <c:if test="${not paymentRequest}">
	    <tr>
	        <th class="right">
	            <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.organizationCode}" /></label>
	        </th>
	        <td>
	            <kul:htmlControlAttribute attributeEntry="${documentAttributes.chartOfAccountsCode}" property="document.chartOfAccountsCode" readOnly="true" />
	            &nbsp;/&nbsp;<kul:htmlControlAttribute attributeEntry="${documentAttributes.organizationCode}" property="document.organizationCode"  readOnly="true"/>
	            <c:if test="${(fullEntryMode or amendmentEntry) and not (contentReadOnly or internalPurchasingReadOnly)}" >
	                <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization" fieldConversions="organizationCode:document.organizationCode,chartOfAccountsCode:document.chartOfAccountsCode" />
	            </c:if>
	        </td>
	        <th class="right">
	            <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.documentFundingSourceCode}" /></label>
	        </th>
	        <td>
	            <kul:htmlControlAttribute
	                property="document.documentFundingSourceCode"
	                attributeEntry="${documentAttributes.documentFundingSourceCode}"
	                extraReadOnlyProperty="document.fundingSource.fundingSourceDescription"
	                readOnly="${not (fullEntryMode and editableFundingSource)}"
	                tabindexOverride="${tabindexOverrideBase + 5}"/>
	        </td>
	    </tr>
    </c:if>

	<c:if test="${KualiForm.document.enableReceivingDocumentRequiredIndicator or KualiForm.document.enablePaymentRequestPositiveApprovalIndicator}">
		<tr>
		  <c:if test="${KualiForm.document.enableReceivingDocumentRequiredIndicator}">
	        <th class="right">
	            <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.receivingDocumentRequiredIndicator}" /></label>
	        </th>
	        <td>
	            <kul:htmlControlAttribute
	                property="document.receivingDocumentRequiredIndicator"
	                attributeEntry="${documentAttributes.receivingDocumentRequiredIndicator}"
	                readOnly="${paymentRequest or
	                readOnlyReceivingRequired or
	                not(fullEntryMode or amendmentEntry) and
	                not (contentReadOnly or internalPurchasingReadOnly)}"
	                tabindexOverride="${tabindexOverrideBase + 0}"/>
	        </td>
	      </c:if>
		  <c:if test="${not KualiForm.document.enableReceivingDocumentRequiredIndicator}">
		    <th class="right">&nbsp;</th>
		    <td >&nbsp;</td>
		  </c:if>
		  <c:if test="${KualiForm.document.enablePaymentRequestPositiveApprovalIndicator}">
			<th class="right">
			  <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestPositiveApprovalIndicator}" /></label>
			</th>
			<td>
			  <kul:htmlControlAttribute
			      property="document.paymentRequestPositiveApprovalIndicator"
				  attributeEntry="${documentAttributes.paymentRequestPositiveApprovalIndicator}"
				  readOnly="${paymentRequest or not(fullEntryMode or amendmentEntry) and not (contentReadOnly or internalPurchasingReadOnly)}"
			  	  tabindexOverride="${tabindexOverrideBase + 5}"/>
			</td>
		  </c:if>
		  <c:if test="${not KualiForm.document.enablePaymentRequestPositiveApprovalIndicator}">
		    <th class="right">&nbsp;</th>
		    <td>&nbsp;</td>
	      </c:if>
		</tr>
	</c:if>

	<c:if test="${purchaseOrder}">
		<tr>
            <th class="right">
                <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.contractManager}" /></label>
            </th>
            <td>
                <kul:htmlControlAttribute
                    property="document.contractManager.contractManagerName"
                    attributeEntry="${documentAttributes.contractManagerName}"
                    readOnly="true" tabindexOverride="${tabindexOverrideBase + 0}" />
                <c:if test="${preRouteChangeMode}" >
                    <kul:lookup
                        boClassName="org.kuali.kfs.vnd.businessobject.ContractManager"
                        fieldConversions="contractManagerName:document.contractManager.contractManagerName,contractManagerCode:document.contractManagerCode" />
                </c:if>
            </td>
		   	<th class="right">
		        <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderPreviousIdentifier}" /></label>
		    </th>
		    <td>
		       	<kul:htmlControlAttribute
		            property="document.purchaseOrderPreviousIdentifier"
		            attributeEntry="${documentAttributes.purchaseOrderPreviousIdentifier}"
		            readOnly="${not (fullEntryMode or amendmentEntry)}"
		            tabindexOverride="${tabindexOverrideBase + 5}" />
		    </td>
		</tr>
	    <tr>
            <th class="right">
                <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.assignedUserPrincipalName}" /></label>
            </th>
            <td>
             	<kul:htmlControlAttribute
                    property="document.assignedUserPrincipalName"
                    attributeEntry="${documentAttributes.assignedUserPrincipalName}"
                    readOnly="${!fullEntryMode and !amendmentEntry}" tabindexOverride="${tabindexOverrideBase + 0}" />
                <c:if test="${fullEntryMode or amendmentEntry}"  >
                    <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person"
                    	fieldConversions="principalId:document.assignedUserPrincipalId,principalName:document.assignedUserPrincipalName" /></div>
                </c:if>
            </td>
            <th class="right">
                <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderConfirmedIndicator}" /></label>
            </th>
            <td>
                <kul:htmlControlAttribute
                    property="document.purchaseOrderConfirmedIndicator"
                    attributeEntry="${documentAttributes.purchaseOrderConfirmedIndicator}"
                    readOnly="${not (fullEntryMode or amendmentEntry)}"
                    tabindexOverride="${tabindexOverrideBase + 5}" />
            </td>
		</tr>
	</c:if>

    <c:if test="${purapTaxEnabled or purchaseOrder}">
	    <tr>
	        <c:if test="${purapTaxEnabled}">
		        <th class="right">
		            <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.useTaxIndicator}" /></label>
		        </th>
		        <td>
		            <kul:htmlControlAttribute
		                property="document.useTaxIndicator"
		                attributeEntry="${documentAttributes.useTaxIndicator}"
		                readOnly="true"/>&nbsp;
		            <c:if test="${fullEntryMode and paymentRequest}">
		                <html:submit
                                property="methodToCall.changeUseTaxIndicator"
                                alt="Change Use Tax Indicator"
                                title="Change Use Tax Indicator"
                                styleClass="btn btn-default small"
                                tabindex="${tabindexOverrideBase + 0}"
                                value="Change to Use Tax"/>
		            </c:if>
		        </td>
	        </c:if>
	        <c:if test="${not purapTaxEnabled}">
	            <th class="right">&nbsp;</th>
	            <td>&nbsp;</td>
	        </c:if>
			<c:if test="${purchaseOrder}">
	            <th class="right">
	                <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionSource}" /></label>
	            </th>
	            <td>
	                <kul:htmlControlAttribute
	                    property="document.requisitionSource.requisitionSourceDescription"
	                    attributeEntry="${documentAttributes.requisitionSource}"
	                    readOnly="true" />
	            </td>
			</c:if>
	        <c:if test="${not purchaseOrder}">
	            <th class="right">&nbsp;</th>
	            <td>&nbsp;</td>
	        </c:if>
	    </tr>
	  </c:if>

	  <tr>
	      <th class="right">
	          <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.accountDistributionMethod}" /></label>
	      </th>
	      <td>
	          <kul:htmlControlAttribute
	              property="document.accountDistributionMethod"
	              attributeEntry="${documentAttributes.accountDistributionMethod}"
	              readOnly="${editableAccountDistributionMethod or not(fullEntryMode or amendmentEntry)}"
	              tabindexOverride="${tabindexOverrideBase + 0}"/>
	      </td>
          <th class="right">&nbsp;</th>
          <td>&nbsp;</td>
	  </tr>


</table>

<c:if test="${purchaseOrder and preRouteChangeMode and !poOutForQuote and !amendmentEntry}">
	<h3>Status Changes</h3>

	<table cellpadding="0" cellspacing="0" class="datatable" summary="Status Changes Section">
		<tr>
			<th class="right">
	            <label><kul:htmlAttributeLabel attributeEntry="${documentAttributes.statusChange}" /></label>
	        </th>
	        <td>
		        <html:radio title="${documentAttributes.statusChange.label} - None" property="statusChange" value="${PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS}" tabindex="${tabindexOverrideBase + 9}" />&nbsp;None&nbsp;
				<html:radio title="${documentAttributes.statusChange.label} - Department" property="statusChange" value="${PurapConstants.PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT}" tabindex="${tabindexOverrideBase + 9}" />&nbsp;Department&nbsp;
				<html:radio title="${documentAttributes.statusChange.label} - Vendor" property="statusChange" value="${PurapConstants.PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR}" tabindex="${tabindexOverrideBase + 9}" />&nbsp;Vendor&nbsp;
			</td>
		</tr>
	</table>
</c:if>

