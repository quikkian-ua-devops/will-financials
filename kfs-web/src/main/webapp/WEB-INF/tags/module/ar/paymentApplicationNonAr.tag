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
<%@ attribute name="hasRelatedCashControlDocument" required="true"
    description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true"
    description="If document is in read only mode"%>
<%@ attribute name="isCustomerSelected" required="true"
    description="Whether or not the customer is set" %>
<%@ attribute name="customerAttributes" required="true"
    description="Attributes of Customer according to the data dictionary" %>
<%@ attribute name="accountsCanCrossCharts" required="false"
    description="Whether or not accounts can cross charts" %>

<c:set var="nonInvoicedAttributes" value="${DataDictionary['NonInvoiced'].attributes}" scope="request" />
<c:set var="nonInvoicedAddLine" value="${KualiForm.nonInvoicedAddLine}" scope="request" />

    <kul:tab tabTitle="Non-AR" defaultOpen="true"
        tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.NON_AR_TAB}">

        <div class="tab-container" align="center">
            <table class="standard">
                <tr class="header">
                    <kul:htmlAttributeHeaderCell literalLabel=" "/>
                    <kul:htmlAttributeHeaderCell
	                    attributeEntry="${nonInvoicedAttributes.chartOfAccountsCode}" />
                    <kul:htmlAttributeHeaderCell
	                    attributeEntry="${nonInvoicedAttributes.accountNumber}" />
                    <kul:htmlAttributeHeaderCell
						attributeEntry="${nonInvoicedAttributes.subAccountNumber}" />
                    <kul:htmlAttributeHeaderCell
						attributeEntry="${nonInvoicedAttributes.financialObjectCode}" />
                    <kul:htmlAttributeHeaderCell
						attributeEntry="${nonInvoicedAttributes.financialSubObjectCode}" />
                    <kul:htmlAttributeHeaderCell
						attributeEntry="${nonInvoicedAttributes.projectCode}" />
                    <kul:htmlAttributeHeaderCell
						attributeEntry="${nonInvoicedAttributes.financialDocumentLineAmount}" />
                    <kul:htmlAttributeHeaderCell literalLabel="Action"/>
                </tr>
				<c:if test="${readOnly ne true}">
	                <tr>
	                    <td>&nbsp;</td>
	                    <td>
			                <c:if test="${!accountsCanCrossCharts}">
	    		            	<span id="nonInvoicedAddLine.chartOfAccountsCode.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.chartOfAccountsCode"/></span>
	            		    </c:if>
							<c:if test="${accountsCanCrossCharts}">
	                        <kul:htmlControlAttribute
	                            onblur="loadChartInfo(this.name, 'nonInvoicedAddLine.chart.finChartOfAccountDescription')"
	                            attributeEntry="${nonInvoicedAttributes.chartOfAccountsCode}"
	                            property="nonInvoicedAddLine.chartOfAccountsCode"/>
	            		    </c:if>
	                        <div id="nonInvoicedAddLine.chart.finChartOfAccountDescription.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.chartOfAccounts.finChartOfAccountDescription"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadAccountInfo(this.name, 'nonInvoicedAddLine.account.accountName')"
	                            attributeEntry="${nonInvoicedAttributes.accountNumber}"
	                            property="nonInvoicedAddLine.accountNumber"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account"
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.accountNumber:accountNumber"
	                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,accountNumber:nonInvoicedAddLine.accountNumber" />
	                        <div id="nonInvoicedAddLine.account.accountName.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.account.accountName"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadSubAccountInfo(this.name, 'nonInvoicedAddLine.subAccount.subAccountName')"
	                            attributeEntry="${nonInvoicedAttributes.subAccountNumber}"
	                            property="nonInvoicedAddLine.subAccountNumber"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubAccount"
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.accountNumber:accountNumber,nonInvoicedAddLine.subAccountNumber:subAccountNumber"
	                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,accountNumber:nonInvoicedAddLine.accountNumber,subAccountNumber:nonInvoicedAddLine.subAccountNumber" />
	                        <div id="nonInvoicedAddLine.subAccount.subAccountName.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.subAccount.subAccountName"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadObjectInfo('${KualiForm.document.postingYear}', '', '', this.name, 'nonInvoicedAddLine.objectCode.financialObjectCodeName')"
	                            attributeEntry="${nonInvoicedAttributes.financialObjectCode}"
	                            property="nonInvoicedAddLine.financialObjectCode"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ObjectCode"
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.financialObjectCode:financialObjectCode,nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode"
	                            fieldConversions="financialObjectCode:nonInvoicedAddLine.financialObjectCode,chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode" />
	                        <div id="nonInvoicedAddLine.objectCode.financialObjectCodeName.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.financialObject.financialObjectCodeName"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadSubObjectInfo('${KualiForm.document.postingYear}', this.name, 'nonInvoicedAddLine.subObjectCode.financialSubObjectCodeName')"
	                            attributeEntry="${nonInvoicedAttributes.financialSubObjectCode}"
	                            property="nonInvoicedAddLine.financialSubObjectCode"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubObjectCode"
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.financialSubObjectCode:financialSubObjectCode,nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.financialObjectCode:financialObjectCode,nonInvoicedAddLine.accountNumber:accountNumber"
	                            fieldConversions="financialSubObjectCode:nonInvoicedAddLine.financialSubObjectCode,chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,financialObjectCode:nonInvoicedAddLine.financialObjectCode,accountNumber:nonInvoicedAddLine.accountNumber" />
	                        <div id="nonInvoicedAddLine.subObjectCode.financialSubObjectCodeName.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.financialSubObject.financialSubObjectCodeName"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadProjectInfo(this.name, 'nonInvoicedAddLine.project.projectDescription')"
	                            attributeEntry="${nonInvoicedAttributes.projectCode}"
	                            property="nonInvoicedAddLine.projectCode"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ProjectCode"
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.projectCode:code"
	                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,code:nonInvoicedAddLine.projectCode" />
	                        <div id="nonInvoicedAddLine.project.projectDescription.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.project.projectDescription"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                        	styleClass="amount"
	                            attributeEntry="${nonInvoicedAttributes.financialDocumentLineAmount}"
	                            property="nonInvoicedAddLine.financialDocumentLineAmount"/>
	                    </td>
	                    <td>
	                    	<html:submit
                                    property="methodToCall.applyAllAmounts"
                                    alt="Add"
                                    title="Add"
                                    styleClass="btn btn-green"
                                    value="Add"/>
		                </td>
	                </tr>
                </c:if>
                <logic:iterate id="nonInvoiced" name="KualiForm" property="paymentApplicationDocument.nonInvoiceds" indexId="ctr">
                    <tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
                        <td>
                            <c:out value="${nonInvoiced.financialDocumentLineNumber}" />
                        </td>
                        <td>
			                <c:if test="${!accountsCanCrossCharts}">
	    		            	<span id="paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccountsCode.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccountsCode"/></span>
	            		    </c:if>
							<c:if test="${accountsCanCrossCharts}">
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.chartOfAccountsCode}"
                                readOnly="${readOnly}"
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccountsCode"
                                onblur="loadChartInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].chart.finChartOfAccountDescription')"
                                onchange="loadChartInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].chart.finChartOfAccountDescription')" />
	            		    </c:if>
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].chart.finChartOfAccountDescription.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccounts.finChartOfAccountDescription"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.accountNumber}"
                                readOnly="${readOnly}"
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].accountNumber"
                                onblur="loadAccountInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].account.accountName')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].account.accountName.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].account.accountName"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.subAccountNumber}"
                                readOnly="${readOnly}"
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].subAccountNumber"
                                onblur="loadSubAccountInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].subAccount.subAccountName')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].subAccount.subAccountName.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].subAccount.subAccountName"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.financialObjectCode}"
                                readOnly="${readOnly}"
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].financialObjectCode"
                                onblur="loadObjectInfo('${KualiForm.document.postingYear}', '', '', this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].objectCode.financialObjectCodeName')"
                                onchange="loadObjectInfo('${paymentApplicationDocument.postingYear}', '', '', this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].objectCode.financialObjectCodeName')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].objectCode.financialObjectCodeName.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].financialObject.financialObjectCodeName"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.financialSubObjectCode}"
                                readOnly="${readOnly}"
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].financialSubObjectCode"
                                onblur="loadSubObjectInfo('${KualiForm.document.postingYear}', this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].subObjectCode.financialSubObjectCodeName')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].subObjectCode.financialSubObjectCodeName.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].financialSubObject.financialSubObjectCodeName"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.projectCode}"
                                readOnly="${readOnly}"
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].projectCode"
                                onblur="loadProjectInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].project.projectDescription')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].project.projectDescription.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].project.projectDescription"/></div>
                        </td>
                        <td style="text-align: right;">
                            <kul:htmlControlAttribute
                            	styleClass="amount"
                                attributeEntry="${nonInvoicedAttributes.financialDocumentLineAmount}"
                                readOnly="${readOnly}"
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].financialDocumentLineAmount"/>
                        </td>
                        <c:choose>
                        <c:when test="${readOnly ne true}">
                            <td>
	                    	    <html:submit
                                        property="methodToCall.deleteNonArLine.line${nonInvoiced.financialDocumentLineNumber}"
                                        alt="Delete"
                                        title="Delete"
                                        styleClass="btn btn-red"
                                        value="Delete"/>
                            </td>
                        </c:when>
                        <c:otherwise>
                        	<td>&nbsp;</td>
                        </c:otherwise>
                        </c:choose>
                    </tr>
                </logic:iterate>
                <tr>
                    <th colspan='6'>&nbsp;</th>
                    <kul:htmlAttributeHeaderCell literalLabel="Non-AR Total"/>
                    <td style="text-align: right;">
                    	${KualiForm.nonArTotal}
                    </td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </div>
    </kul:tab>
