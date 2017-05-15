<%--

    The Kuali Financial System, a comprehensive financial management system for higher education.

    Copyright 2005-2017 Kuali, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>
<c:set var="requisitionAttributes" value="${DataDictionary.RequisitionDocument.attributes}"/>
<c:set var="requisitionItemAttributes" value="${DataDictionary.RequisitionItem.attributes}"/>
<c:set var="sourceAccountingLineAttributes" value="${DataDictionary.SourceAccountingLine.attributes}"/>
<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}"/>
<c:set var="ContractManagerAssignmentAttributes" value="${DataDictionary.ContractManagerAssignmentDocument.attributes}"/>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>

<kul:tab tabTitle="Assign A Contract Manager" defaultOpen="true" tabErrorKey="${PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS}">
    <div class="tab-container">
        <table class="standard" summary="Assign A Contract Manager">
            <c:if test="${empty KualiForm.document.contractManagerAssignmentDetails}">
                <th align=right valign=middle class="bord-l-b">
                    <div align="center">There are no unassigned requisitions.</div>
                </th>
            </c:if>
            <c:if test="${!empty KualiForm.document.contractManagerAssignmentDetails}">
                <tr class="header" style="border-bottom: 1px solid #ccc;">
                    <kul:htmlAttributeHeaderCell attributeEntry="${purchaseOrderAttributes.contractManagerCode}" forceRequired="true"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.requisitionNumber}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.deliveryCampusCode}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.vendorName}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.generalDescription}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.requisitionTotalAmount}" addClass="right"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.requisitionCreateDate}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.firstItemDescription}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.firstItemCommodityCode}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.firstObjectCode}"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.universityFiscalYear}"/>
                </tr>

                <logic:iterate id="acmDetail" name="KualiForm" property="document.contractManagerAssignmentDetails" indexId="ctr">
                    <tr class="${ctr % 2 == 0 ? 'highlight' : ''}" style="border-bottom: 1px solid #ccc;">
                        <td class="datacell top">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].contractManagerCode"
                                    attributeEntry="${purchaseOrderAttributes.contractManagerCode}"
                                    readOnly="${readOnly}"/>

                            <c:if test="${!readOnly}">
                                <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.ContractManager"
                                            fieldConversions="contractManagerCode:document.contractManagerAssignmentDetail[${ctr}].contractManagerCode"/>
                                <kul:checkErrors keyMatch="document.contractManagerAssignmentDetails[${ctr}].contractManagerCode"/>
                                <c:if test="${hasErrors}">
                                    <kul:fieldShowErrorIcon/>
                                </c:if>
                            </c:if>
                        </td>
                        <td class="datacell top">
                            <c:if test="${!readOnly}">
                                <a href="<c:out value="${acmDetail.requisition.url}" />" target="_BLANK">
                                    <c:out value="${acmDetail.requisitionIdentifier}"/>
                                </a>
                            </c:if>
                            <c:if test="${readOnly}">
                                <c:out value="${acmDetail.requisitionIdentifier}"/>
                            </c:if>
                        </td>
                        <td class="datacell top">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].requisition.deliveryCampusCode"
                                    attributeEntry="${requisitionAttributes.deliveryCampusCode}"
                                    readOnly="true"/>
                        </td>
                        <td class="datacell top" style="min-width: 200px;">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].requisition.vendorName"
                                    attributeEntry="${requisitionAttributes.vendorName}"
                                    readOnly="true"/>
                        </td>
                        <td class="datacell top">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].requisition.documentHeader.documentDescription"
                                    attributeEntry="${requisitionAttributes['documentHeader.documentDescription']}"
                                    readOnly="true"/>
                        </td>
                        <td class="datacell right top">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].requisition.documentHeader.financialDocumentTotalAmount"
                                    attributeEntry="${requisitionAttributes['financialSystemDocumentHeader.financialDocumentTotalAmount']}"
                                    readOnly="true"/>
                        </td>
                        <td class="datacell top">
                            <c:out value="${acmDetail.createDate}"/>
                        </td>
                        <td class="datacell top">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].itemDescription"
                                    attributeEntry="${requisitionItemAttributes.itemDescription}"
                                    readOnly="true"/>
                        </td>
                        <td class="datacell top">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].purchasingCommodityCode"
                                    attributeEntry="${requisitionItemAttributes.purchasingCommodityCode}"
                                    readOnly="true"/>

                            <c:if test="${! empty KualiForm.document.contractManagerAssignmentDetails[ctr].requisition.items[0].commodityCode.commodityDescription}">
                                &nbsp;-&nbsp;
                                <kul:htmlControlAttribute
                                        property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].commodityCode.commodityDescription"
                                        attributeEntry="${requisitionItemAttributes.purchasingCommodityCode}"
                                        readOnly="true"/>
                            </c:if>
                        </td>
                        <td class="datacell top">
                            <c:choose>
                                <c:when test="${!empty acmDetail.requisition.items[0].sourceAccountingLines}">
                                    <kul:htmlControlAttribute
                                            property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].sourceAccountingLines[0].financialObjectCode"
                                            attributeEntry="${sourceAccountingLineAttributes.financialObjectCode}"
                                            readOnly="true"/>

                                </c:when>
                                <c:when test="${empty acmDetail.requisition.items[0].sourceAccountingLines}">
                                    Note: This is bad data! If you are seeing this, you may have a requisition with no account for one item.
                                </c:when>
                            </c:choose>
                        </td>
                        <td class="datacell top">
                            <kul:htmlControlAttribute
                                    property="document.contractManagerAssignmentDetail[${ctr}].requisition.postingYear"
                                    attributeEntry="${requisitionAttributes.postingYear}"
                                    readOnly="true"/>
                        </td>
                    </tr>
                </logic:iterate>
            </c:if>
        </table>
    </div>
</kul:tab>
