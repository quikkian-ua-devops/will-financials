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

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>

<script language="JavaScript" type="text/javascript" src="dwr/interface/VendorService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vnd/objectInfo.js"></script>

<c:set var="attributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />
<c:set var="dataCellCssClass" value="datacell" />
<c:set var="capitalAssetInfoName" value="document.capitalAssetInformation" />

<c:set var="totalColumnSpan" value="8"/>
<c:set var="amountReadOnly" value="${readOnly or KualiForm.distributeEqualAmount}" />

<table class="datatable standard side-margins" cellpadding="0" cellspacing="0" summary="Capital Asset Redistribution">
	<tr>
   		<td colspan="3" class="tab-subhead" style="padding-bottom: 20px;">
   			System Control Amount: <c:out value="${KualiForm.systemControlAmount}" />
   		</td>
	   	<c:set var="totalColumnSpan" value="${totalColumnSpan-4}"/>
	   	<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}" >
	   		<c:set var="totalColumnSpan" value="4"/>
	   	</c:if>
	   	<c:if test="${KualiForm.createdAssetsControlAmount == 0.00}" >
	   		<c:set var="totalColumnSpan" value="4"/>
	   	</c:if>
	   	<td colspan="${totalColumnSpan}" class="tab-subhead">
	   		System Control Remainder Amount: <c:out value="${KualiForm.createdAssetsControlAmount}" />
	   	</td>
   		<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}">
	   		<td colspan="1" class="tab-subhead right">
                <html:submit
                        property="methodToCall.redistributeCreateCapitalAssetAmount"
                        title="Redistribute Total Amount for new capital assets"
                        alt="Redistribute Total Amount for new capital assets"
                        styleClass="btn btn-default"
                        value="Redistribute Total Amount"/>
	   		</td>
   		</c:if>
  	</tr>
</table>
<table class="datatable standard" cellpadding="0" cellspacing="0" summary="Capital Asset Information">
	<c:forEach items="${KualiForm.document.capitalAssetInformation}" var="detailLine" varStatus="status">
		<c:set var="distributionAmountCode" value="${detailLine.distributionAmountCode}" />
		<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE}">
			<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_DESCRIPTION}" />
			<c:set var="amountReadOnly" value="true" />
		</c:if>
		<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE}">
			<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_DESCRIPTION}" />
			<c:set var="amountReadOnly" value="${readOnly}" />
		</c:if>

		<c:if test="${detailLine.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR}">
			<tr>
                <td colspan="8">
                    <h3>Capital Asset for Accounting Lines</h3>
                </td>
            </tr>
            <c:if test="${not empty detailLine.capitalAssetAccountsGroupDetails}" >
                <tr>
                    <td colSpan="8"><center><br/>
                        <fp:capitalAssetAccountsGroupDetails
                                capitalAssetAccountsGroupDetails="${detailLine.capitalAssetAccountsGroupDetails}"
                                capitalAssetAccountsGroupDetailsName="${capitalAssetInfoName}[${status.index}].capitalAssetAccountsGroupDetails"
                                readOnly="${readOnly}"
                                capitalAssetAccountsGroupDetailsIndex="${status.index}"/>
                    </td>
                </tr>
            </c:if>

			<tr>
				<td colspan="8">
                    <table class="datatable standard items" style="border-top: 1px solid #c3c3c3;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
                        <tr class="header">
                            <kul:htmlAttributeHeaderCell literalLabel=""/>
                            <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetQuantity}" labelFor="${capitalAssetInfoName}.capitalAssetQuantity"/>
                            <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTypeCode}" labelFor="${capitalAssetInfoName}.capitalAssetTypeCode"/>
                            <kul:htmlAttributeHeaderCell attributeEntry="${attributes.vendorName}" labelFor="${capitalAssetInfoName}.vendorName"/>
                            <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerName}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerName"/>
                            <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerModelNumber}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerModelNumber"/>
                            <kul:htmlAttributeHeaderCell attributeEntry="${attributes.distributionAmountCode}" labelFor="${capitalAssetInfoName}.distributionAmountCode"/>
                            <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetLineAmount}" labelFor="${capitalAssetInfoName}.capitalAssetLineAmount"/>
                        </tr>

                        <tr>
                            <kul:htmlAttributeHeaderCell literalLabel="${detailLine.capitalAssetLineNumber}"/>
                            <fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
                                businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
                                field="capitalAssetQuantity" lookup="false" inquiry="false" />

                            <fp:dataCell dataCellCssClass="${dataCellCssClass}"
                                businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
                                field="capitalAssetTypeCode" lookup="true" inquiry="true"
                                boClassSimpleName="CapitalAssetManagementAssetType" boPackageName="org.kuali.kfs.integration.cam"
                                lookupOrInquiryKeys="capitalAssetTypeCode"
                                businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>

                            <fp:dataCell dataCellCssClass="${dataCellCssClass}"
                                businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
                                field="vendorName" lookup="true" inquiry="true" disabled="true"
                                boClassSimpleName="VendorDetail" boPackageName="org.kuali.kfs.vnd.businessobject"
                                lookupOrInquiryKeys="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier,vendorName"
                                businessObjectValuesMap="${capitalAssetInfo.valuesMap}" />

                            <fp:dataCell dataCellCssClass="${dataCellCssClass}"
                                businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
                                field="capitalAssetManufacturerName" lookup="false" inquiry="false"/>

                            <fp:dataCell dataCellCssClass="${dataCellCssClass}"
                                businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
                                field="capitalAssetManufacturerModelNumber" lookup="false" inquiry="false"/>

                            <td><c:out value="${distributionAmountDescription}"/></td>

                            <fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
                                businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${amountReadOnly}"
                                field="capitalAssetLineAmount" lookup="false" inquiry="false" />

                        </tr>
                        <c:if test="${readOnly}">
                            <c:set var="assetDescription" value="${detailLine.capitalAssetDescription}" />
                            <tr>
                                <th class="grid right" width="5%"><kul:htmlAttributeLabel attributeEntry="${attributes.capitalAssetDescription}" readOnly="true" /></th>
                                <td class="grid" width="95%" colspan="7"><kul:htmlControlAttribute property="${capitalAssetInfoName}[${status.index}].capitalAssetDescription" attributeEntry="${attributes.capitalAssetDescription}" readOnly="true"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${!readOnly}">
                            <tr class="header">
                                <th></th>
                                <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetDescription}" colspan="4"/>
                                <kul:htmlAttributeHeaderCell literalLabel="Actions" colspan="2" addClass="center"/>
                            </tr>
                            <tr>
                                <td></td>
                                <fp:dataCell
                                        dataCellCssClass="${dataCellCssClass}"
                                        businessObjectFormName="${capitalAssetInfoName}[${status.index}]"
                                        attributes="${attributes}"
                                        readOnly="false"
                                        disabled="false"
                                        field="capitalAssetDescription"
                                        lookup="false"
                                        inquiry="false"
                                        colSpan="4"/>
                                <td class="infoline center" colspan="2">
                                    <html:submit
                                            property="methodToCall.insertCapitalAssetInfo.line${status.index}.Anchor"
                                            title="Add the capital Asset Information"
                                            alt="Add the capital Asset Information"
                                            styleClass="btn btn-green"
                                            value="Insert"/>
                                    <html:submit
                                            property="methodToCall.deleteCapitalAssetInfo.line${status.index}.Anchor"
                                            title="Delete the capital Asset Information"
                                            alt="Delete the capital Asset Information"
                                            styleClass="btn btn-red"
                                            value="Delete"/>
                                    <html:submit
                                            property="methodToCall.clearCapitalAssetInfo.line${status.index}.Anchor"
                                            title="Clear the capital Asset Information"
                                            alt="Clear the capital Asset Information"
                                            styleClass="btn btn-default"
                                            value="Clear"/>
                                    <br />
                                    <br />
                                    <html:submit
                                            property="methodToCall.addCapitalAssetTagLocationInfo.line${status.index}.Anchor"
                                            title="Add the capital Asset tag and location"
                                            alt="Add the capital Asset tag and location"
                                            styleClass="btn btn-default"
                                            value="Add Tag/Location"/>
                                </td>
                            </tr>
                        </c:if>

                        <c:if test="${not empty detailLine.capitalAssetInformationDetails}" >
                           <tr>
                               <td colSpan="8">
                                    <fp:capitalAssetInfoDetail
                                            capitalAssetInfoDetails="${detailLine.capitalAssetInformationDetails}"
                                            capitalAssetInfoDetailsName="${capitalAssetInfoName}[${status.index}].capitalAssetInformationDetails"
                                            readOnly="${readOnly}"
                                            capitalAssetInfoIndex="${status.index}"/>
                               </td>
                           </tr>
                        </c:if>
                    </table>
		   		</td>
			</tr>
		</c:if>
	</c:forEach>
</table>
