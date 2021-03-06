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
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}"/>
<c:set var="organizationAttributes" value="${DataDictionary.Organization.attributes}"/>
<c:set var="assetPaymentAssetDetailAttributes" value="${DataDictionary.AssetPaymentAssetDetail.attributes}"/>
<c:set var="totalHistoricalAmount" value="${KualiForm.document.assetsTotalHistoricalCost}"/>
<c:set var="globalTotalAllocated" value="${0.00}"/>
<c:set var="globalTotalHistoricalCost" value="${0.00}"/>
<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>

<c:set var="assetAllocationLabel" value="${KualiForm.allocationLabel}"/>
<c:set var="isAllocationEditable" value="${KualiForm.allocationEditable}"/>
<c:set var="isAllocationEditablePct" value="${KualiForm.allocationEditablePct}"/>
<c:set var="sourceLineCount" value="${KualiForm.sourceLineCount}"/>


<logic:iterate id="assetPaymentAssetDetail" name="KualiForm" property="document.assetPaymentAssetDetail" indexId="ctr">
    <c:set var="capitalAssetNumber" value="${KualiForm.document.assetPaymentAssetDetail[ctr].capitalAssetNumber}"/>
    <c:set var="assetObject" value="document.assetPaymentAssetDetail[${ctr}].asset"/>
    <c:set var="assetValue" value="${KualiForm.document.assetPaymentAssetDetail[ctr].asset}"/>
    <c:set var="assetPayments" value="${KualiForm.document.assetPaymentAssetDetail[ctr].asset.assetPayments}"/>
    <c:set var="assetPaymentsAssetDetail" value="${KualiForm.document.assetPaymentAssetDetail[ctr]}"/>

    <c:set var="totalAllocated" value="${0.00}"/>
    <c:set var="newTotal" value="${KualiForm.document.assetPaymentAssetDetail[ctr].newTotal}"/>
    <c:set var="previousCost" value="${KualiForm.document.assetPaymentAssetDetail[ctr].previousTotalCostAmount}"/>

    <c:set var="totalAllocated" value="${KualiForm.document.assetPaymentAssetDetail[ctr].allocatedAmount}"/>
    <c:if test="${totalAllocated != 0.00 }">
        <fmt:formatNumber var="sTotalAllocated" value="${totalAllocated }" maxFractionDigits="2" minFractionDigits="2" type="number"/>
        <fmt:parseNumber value="${sTotalAllocated}" type="number" var="totalAllocated"/>
    </c:if>

    <table class="standard side-margins">
        <tr class="header">
            <kul:htmlAttributeHeaderCell width="10%" attributeEntry="${assetAttributes.capitalAssetNumber}"/>
            <kul:htmlAttributeHeaderCell width="42%" attributeEntry="${assetAttributes.capitalAssetDescription}"/>
            <kul:htmlAttributeHeaderCell width="10%" attributeEntry="${organizationAttributes.organizationCode}"/>
            <kul:htmlAttributeHeaderCell width="10%" attributeEntry="${assetPaymentAssetDetailAttributes.previousTotalCostAmount}" addClass="right"/>

            <th class="grid right" width="10%">${assetAllocationLabel}</th>
            <th class="grid right" width="10%">New Total</th>
            <th class="grid" width="8%"><c:if test="${!viewOnly}">Actions</c:if></th>
        </tr>

        <tr>
            <td class="grid">
                <kul:htmlControlAttribute
                        property="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber"
                        attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true"
                        readOnlyBody="true">
                    <kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.Asset"
                                 keyValues="capitalAssetNumber=${KualiForm.document.assetPaymentAssetDetail[ctr].capitalAssetNumber}"
                                 render="true">
                        <html:hidden write="true"
                                     property="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber"/>
                    </kul:inquiry>&nbsp;
                </kul:htmlControlAttribute>
            </td>

            <td class="grid">
                <kul:htmlControlAttribute property="${assetObject}.capitalAssetDescription"
                                          attributeEntry="${assetAttributes.capitalAssetDescription}"
                                          readOnly="true"/>
            </td>

            <td class="grid">
                ${KualiForm.document.assetPaymentAssetDetail[ctr].asset.organizationOwnerAccount.organizationCode}
            </td>

            <td class="grid right">
                <kul:htmlControlAttribute
                        property="document.assetPaymentAssetDetail[${ctr}].previousTotalCostAmount"
                        attributeEntry="${assetPaymentAssetDetailAttributes.previousTotalCostAmount}"
                        readOnly="true"/>
            </td>

            <td class="grid right">
                <c:if test="${isAllocationEditable}">
                    <c:if test="${isAllocationEditablePct}">
                        <kul:htmlControlAttribute
                                property="document.assetPaymentAssetDetail[${ctr}].allocatedUserValuePct"
                                attributeEntry="${assetPaymentAssetDetailAttributes.allocatedUserValuePct}"
                                styleClass="amount"
                                readOnly="${viewOnly && (sourceLineCount > 0)}"/>
                    </c:if>
                    <c:if test="${!isAllocationEditablePct}">
                        <kul:htmlControlAttribute
                                property="document.assetPaymentAssetDetail[${ctr}].allocatedUserValue"
                                attributeEntry="${assetPaymentAssetDetailAttributes.allocatedUserValue}"
                                styleClass="amount"
                                readOnly="${viewOnly && (sourceLineCount > 0)}"/>
                    </c:if>
                </c:if>
                <c:if test="${!isAllocationEditable}">
                    <kul:htmlControlAttribute
                            property="document.assetPaymentAssetDetail[${ctr}].allocatedAmount"
                            attributeEntry="${assetPaymentAssetDetailAttributes.allocatedAmount}"
                            readOnly="true"/>
                </c:if>
            </td>

            <td class="grid right">${newTotal}</td>

            <td class="datacell nowrap">
                <c:if test="${!viewOnly}">
                        <c:if test="${isAllocationEditable && !readOnly}">
                            <html:submit
                                    property="methodToCall.updateAssetPaymentAssetDetail.line${ctr}"
                                    styleClass="btn btn-default"
                                    alt="Update Allocations"
                                    title="Update Allocations"
                                    value="Update View"/>
                        </c:if>
                        <html:submit
                                property="methodToCall.deleteAssetPaymentAssetDetail.line${ctr}"
                                styleClass="btn btn-red"
                                alt="Delete asset"
                                title="Delete asset"
                                value="Delete"/>
                </c:if>
            </td>
        </tr>
    </table>

    <c:set var="globalTotalAllocated" value="${globalTotalAllocated + totalAllocated}"/>
    <c:set var="globalTotalHistoricalCost" value="${globalTotalHistoricalCost + previousCost}"/>

    <div style="margin: 0 15px;">
        <kul:tab tabTitle="Asset Information" defaultOpen="false" useCurrentTabIndexAsKey="true"
                 tabErrorKey="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber*">
            <div class="tab-container" align="center" id="tab-AssetInformation-div">

                <table class="standard side-margins" summary="Asset">
                    <tr>
                        <th class="grid right" width="25%">
                            <kul:htmlAttributeLabel
                                attributeEntry="${assetAttributes.organizationOwnerAccountNumber}"
                                readOnly="true"/>
                        </th>
                        <td class="grid" width="25%">
                            <kul:htmlControlAttribute
                                    property="${assetObject}.organizationOwnerAccountNumber"
                                    attributeEntry="${assetAttributes.organizationOwnerAccountNumber}"
                                    readOnly="true" readOnlyBody="true">
                                <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account"
                                             keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}&amp;accountNumber=${assetValue.organizationOwnerAccountNumber}"
                                             render="true">
                                    <html:hidden write="true"
                                                 property="${assetObject}.organizationOwnerAccountNumber"/>
                                </kul:inquiry>&nbsp;
                            </kul:htmlControlAttribute>
                        </td>

                        <th class="grid right" width="25%">
                            <kul:htmlAttributeLabel
                                attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}"
                                readOnly="true"/>
                        </th>
                        <td class="grid" width="25%">
                            <kul:htmlControlAttribute
                                    property="${assetObject}.organizationOwnerChartOfAccountsCode"
                                    attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}"
                                    readOnly="true" readOnlyBody="true">
                                <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart"
                                             keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}"
                                             render="true">
                                    <html:hidden write="true"
                                                 property="${assetObject}.organizationOwnerChartOfAccountsCode"/>
                                </kul:inquiry>&nbsp;
                            </kul:htmlControlAttribute>
                        </td>
                    </tr>

                    <tr>
                        <th class="grid right" width="25%">
                            <kul:htmlAttributeLabel
                                attributeEntry="${assetAttributes.capitalAssetTypeCode}"
                                readOnly="true"/>
                        </th>
                        <td class="grid" width="25%">
                            <kul:htmlControlAttribute property="${assetObject}.capitalAssetTypeCode"
                                                      attributeEntry="${assetAttributes.capitalAssetTypeCode}"
                                                      readOnly="true" readOnlyBody="true">
                                <kul:inquiry
                                        boClassName="org.kuali.kfs.module.cam.businessobject.AssetType"
                                        keyValues="capitalAssetTypeCode=${assetValue.capitalAssetTypeCode}"
                                        render="true">
                                    <html:hidden write="true"
                                                 property="${assetObject}.capitalAssetTypeCode"/>
                                </kul:inquiry>&nbsp;
                            </kul:htmlControlAttribute>
                        </td>
                        <th class="grid right" width="25%">
                            <kul:htmlAttributeLabel
                                attributeEntry="${assetAttributes.vendorName}"
                                readOnly="true"/>
                        </th>
                        <td class="grid" width="25%">
                            <kul:htmlControlAttribute
                                property="${assetObject}.vendorName"
                                attributeEntry="${assetAttributes.vendorName}"
                                readOnly="true"/>
                        </td>
                    </tr>

                    <tr>
                        <th class="grid right" width="25%">
                            <kul:htmlAttributeLabel
                                attributeEntry="${assetAttributes.capitalAssetInServiceDate}"
                                readOnly="true"/>
                        </th>
                        <td class="grid" width="25%">
                            <kul:htmlControlAttribute
                                property="${assetObject}.capitalAssetInServiceDate"
                                attributeEntry="${assetAttributes.capitalAssetInServiceDate}"
                                readOnly="true"/>
                        </td>
                        <th class="grid right" width="25%">
                            <kul:htmlAttributeLabel
                                attributeEntry="${assetAttributes.manufacturerName}"
                                readOnly="true"/>
                        </th>
                        <td class="grid" width="25%">
                            <kul:htmlControlAttribute
                                property="${assetObject}.manufacturerName"
                                attributeEntry="${assetAttributes.manufacturerName}"
                                readOnly="true"/>
                        </td>
                    </tr>
                    <tr>
                        <th class="grid right" width="25%">
                            <kul:htmlAttributeLabel
                                attributeEntry="${assetAttributes.capitalAssetDescription}"
                                readOnly="true"/>
                        </th>
                        <td class="grid" width="75%" colspan="3">
                            <kul:htmlControlAttribute
                                property="${assetObject}.capitalAssetDescription"
                                attributeEntry="${assetAttributes.capitalAssetDescription}"
                                readOnly="true"/>
                        </td>
                    </tr>
                </table>

                <div style="margin: 0 15px;">
                    <cams:viewPayments defaultTabHide="true" assetPayments="${assetPayments}"
                                   assetValueObj="${assetObject}" assetValue="${assetValue}"/>

                    <cams:viewPaymentInProcess defaultTabHide="true"
                                           assetPaymentDetails="${KualiForm.document.sourceAccountingLines}"
                                           assetPaymentAssetDetail="${assetPaymentsAssetDetail}"
                                           assetPaymentsTotal="${totalAllocated}"
                                           assetPaymentDistribution="${KualiForm.document.assetPaymentDistributor.assetPaymentDistributions}"/>
                </div>
            </div>
        </kul:tab>
    </div>
</logic:iterate>

<c:if test="${fn:length(KualiForm.document.assetPaymentAssetDetail) > 0}">
    <table class="standard side-margins" summary="AssetSummary">
        <tr>
            <kul:htmlAttributeHeaderCell colspan="3" literalLabel="Grand Total:" width="62%" addClass="right"/>
            <th class="grid right" width="10%">
                <fmt:formatNumber
                        value="${globalTotalHistoricalCost}"
                        maxFractionDigits="2"
                        minFractionDigits="2"/>
            </th>
            <th class="grid right" width="10%">
                <fmt:formatNumber value="${globalTotalAllocated}"
                                  maxFractionDigits="2"
                                  minFractionDigits="2"/>
            </th>
            <th class="grid right" width="10%">
                <fmt:formatNumber
                        value="${globalTotalAllocated + globalTotalHistoricalCost}"
                        maxFractionDigits="2"
                        minFractionDigits="2"/>
            </th>
            <th class="grid" width="8%">&nbsp;</th>
        </tr>
    </table>
</c:if>
