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
<script>
    function selectAll(all, styleid) {
        var elms = document.getElementsByTagName("input");
        for (var i = 0; i < elms.length; i++) {
            if (elms[i].id != null && elms[i].id == styleid && !elms[i].disabled) {
                elms[i].checked = all.checked;
            }
        }
    }

    <c:if test="${!empty KualiForm.currDocNumber}">
    var popUpurl = 'camsGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.currDocNumber}';
    window.open(popUpurl, "${KualiForm.currDocNumber}");
    </c:if>

</script>
<kul:page showDocumentInfo="false" htmlFormAction="camsGlLine" renderMultipart="true"
          showTabButtons="true" docTitle="General Ledger Processing"
          transactionalDocument="false" headerDispatch="true" headerTabActive="true"
          sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="start">

    <kul:tabTop tabTitle="Financial Document Capital Asset Info" defaultOpen="true">
        <div class="tab-container">
            <c:set var="CapitalAssetInformationAttributes" value="${DataDictionary.CapitalAssetInformation.attributes}"/>
            <c:set var="CapitalAssetInformationDetailAttributes" value="${DataDictionary.CapitalAssetInformationDetail.attributes}"/>
            <c:if test="${!empty KualiForm.capitalAssetInformation }">
                <c:if test="${KualiForm.capitalAssetInformation.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR}">
                    <h3><c:out value="${KFSConstants.CapitalAssets.CREATE_CAPITAL_ASSETS_TAB_TITLE}"/></h3>
                </c:if>
                <c:if test="${KualiForm.capitalAssetInformation.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR}">
                    <h3><c:out value="${KFSConstants.CapitalAssets.MODIFY_CAPITAL_ASSETS_TAB_TITLE}"/></h3>
                </c:if>
                <table width="100%" class="standard side-margins">
                    <tr>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true"/></th>
                        <td class="grid" width="25%">
                            <html:link target="_blank" href="camsGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.capitalAssetInformation.documentNumber}">
                                <kul:htmlControlAttribute property="capitalAssetInformation.documentNumber" attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true"/>
                            </html:link>
                        </td>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetLineNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" readOnly="true"/></td>
                    </tr>
                    <tr>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true"/></th>
                        <td class="grid" width="25%">
                            <c:if test="${!empty KualiForm.capitalAssetInformation.capitalAssetNumber}">
                                <kul:inquiry boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" keyValues="capitalAssetNumber=${KualiForm.capitalAssetInformation.capitalAssetNumber}" render="true">
                                    <kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true"/>
                                </kul:inquiry>
                            </c:if>
                            &nbsp;
                        </td>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetTypeCode" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true"/></td>
                    </tr>
                    <tr>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.vendorName" attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true"/></td>

                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetQuantity" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true"/></td>
                    </tr>
                    <tr>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerName" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true"/></td>

                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerModelNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true"/></td>
                    </tr>
                    <tr>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetDescription" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true"/></td>
                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineAmount}" readOnly="true"/></th>
                        <td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetLineAmount" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineAmount}" readOnly="true"/></td>
                    </tr>
                    <tr>
                        <c:set var="distributionAmountCode" value="${KualiForm.capitalAssetInformation.distributionAmountCode}"/>
                        <c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE}">
                            <c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_DESCRIPTION}"/>
                        </c:if>
                        <c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE}">
                            <c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_DESCRIPTION}"/>
                        </c:if>

                        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.distributionAmountCode}" readOnly="true"/></th>
                        <td class="grid" width="25%">
                            <div><c:out value="${distributionAmountDescription}"/></div>
                        </td>

                        <td colSpan="2">&nbsp;</td>
                    </tr>
                    <tr>
                        <td colSpan="4">
                            <cams:groupAccountingLinesDetails capitalAssetInformation="${KualiForm.capitalAssetInformation}" capitalAssetPosition="1" showViewButton="false"/>
                        </td>
                    </tr>
                    <tr>
                        <td colSpan="4">
                            <c:if test="${!empty KualiForm.capitalAssetInformation.capitalAssetInformationDetails}">
                                <div class="tab-container" align="center">
                                    <table class="standard center" style="width: 80%;">
                                        <tr>
                                            <td class="tab-subhead" colspan="6"><h3>Capital Asset Tag/Location Details</h3></td>
                                        </tr>
                                        <tr class="header">
                                            <kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.campusCode}" hideRequiredAsterisk="true" scope="col"/>
                                            <kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.buildingCode}" hideRequiredAsterisk="true" scope="col"/>
                                            <kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.buildingRoomNumber}" hideRequiredAsterisk="true" scope="col"/>
                                            <kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.buildingSubRoomNumber}" hideRequiredAsterisk="true" scope="col"/>
                                            <kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetTagNumber}" hideRequiredAsterisk="true" scope="col"/>
                                            <kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetSerialNumber}" hideRequiredAsterisk="true" scope="col"/>
                                        </tr>
                                        <c:forEach var="assetDetail" items="${KualiForm.capitalAssetInformation.capitalAssetInformationDetails}" varStatus="current">
                                            <tr>
                                                <td class="grid" width="15%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].campusCode" attributeEntry="${CapitalAssetInformationDetailAttributes.campusCode}" readOnly="true"/></td>
                                                <td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].buildingCode" attributeEntry="${CapitalAssetInformationDetailAttributes.buildingCode}" readOnly="true"/></td>
                                                <td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].buildingRoomNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.buildingRoomNumber}" readOnly="true"/></td>
                                                <td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].buildingSubRoomNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.buildingSubRoomNumber}" readOnly="true"/></td>
                                                <td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].capitalAssetTagNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetTagNumber}" readOnly="true"/></td>
                                                <td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].capitalAssetSerialNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetSerialNumber}" readOnly="true"/></td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                            </c:if>
                        </td>
                    </tr>
                </table>
            </c:if>
        </div>
        <c:choose>
            <c:when test="${KualiForm.generalLedgerEntry.generalLedgerAccountIdentifier == KualiForm.primaryGlAccountId && KualiForm.generalLedgerEntry.active}">
                <c:set var="allowSubmit" value="true"/>
            </c:when>
            <c:when test="${!KualiForm.generalLedgerEntry.active}">
                <a href="camsGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.generalLedgerEntry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}" target="${KualiForm.generalLedgerEntry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}">
                        ${KualiForm.generalLedgerEntry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}</a>
            </c:when>
            <c:otherwise>
                <c:set var="allowSubmit" value="true"/>
            </c:otherwise>
        </c:choose>
    </kul:tabTop>

    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
            <c:if test="${!empty allowSubmit}">
                <html:submit
                        property="methodToCall.submitAssetGlobal"
                        title="Add Assets"
                        alt="Add Assets"
                        styleClass="btn btn-default"
                        value="Create Asset"/>
                <html:submit
                        property="methodToCall.submitPaymentGlobal"
                        title="Add Payments"
                        alt="Add Payments"
                        styleClass="btn btn-default"
                        value="Apply Payment"/>
            </c:if>
            <html:submit
                    property="methodToCall.reload"
                    title="Reload"
                    alt="Reload"
                    styleClass="btn btn-default"
                    value="Reload"/>
            <html:submit
                    styleClass="btn btn-default"
                    property="methodToCall.cancel"
                    title="Cancel"
                    alt="Cancel"
                    value="Cancel"/>
        </c:if>
    </div>

    <kul:stickyGlobalButtons bodySelector="main.content"/>
</kul:page>
