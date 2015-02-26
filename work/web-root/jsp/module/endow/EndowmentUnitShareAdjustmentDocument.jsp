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

<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	
<kul:documentPage showDocumentInfo="true"
	documentTypeName="EndowmentUnitShareAdjustmentDocument"
	htmlFormAction="endowEndowmentUnitShareAdjustmentDocument" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<endow:endowmentDocumentOverview editingMode="${KualiForm.editingMode}" 
	                                 endowDocAttributes="${DataDictionary.EndowmentUnitShareAdjustmentDocument.attributes}" />
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />
     
    <endow:endowmentTransactionalDocumentDetails
         documentAttributes="${DataDictionary.EndowmentUnitShareAdjustmentDocument.attributes}"
         readOnly="${readOnly}"
         subTypeReadOnly="true"
         tabTitle="Unit/Share Adjustment Details"
         headingTitle="Unit/Share Adjustment Details"
         summaryTitle="Unit/Share Adjustment Details" />

    <endow:endowmentSecurityDetailsSection showTarget="false" showSource="true" showRegistrationCode="true" openTabByDefault="true" showLabels="false"/>      

    <endow:endowmentTransactionLinesSourceSection hasSource="true" hasTarget="false" hasUnits="true" isTransAmntReadOnly="true"/>                 
   
    <endow:endowmentTransactionLinesTargetSection hasSource="false" hasTarget="true" hasUnits="true" isTransAmntReadOnly="true"/>                 

    <endow:endowmentTaxLotLine 
    	documentAttributes="${DataDictionary.EndowmentTransactionTaxLotLine.attributes}" 
    	isSource="true"
    	isTarget="true"
    	displayGainLoss="false"
    	displayHoldingCost="false"
    	showSourceDeleteButton="true"
    	showTargetDeleteButton="true"
    	readOnly="${readOnly}"/>                
        
	<kul:notes /> 
	
	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
	