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

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || KualiForm.document.errorCorrected}" />

<kul:tab tabTitle="Credit Card Receipts" defaultOpen="true" tabErrorKey="${KFSConstants.CREDIT_CARD_RECEIPTS_LINE_ERRORS}">
<c:set var="ccrAttributes" value="${DataDictionary.CreditCardDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="20" />

 <div class="tab-container" align=center>
	<table class="datatable standard items" summary="Credit Card Receipts section">
		<tr class="header">
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.financialDocumentCreditCardTypeCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.financialDocumentCreditCardVendorNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.creditCardDepositDate}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.creditCardDepositReferenceNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.creditCardAdvanceDepositAmount}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
        <c:if test="${not readOnly}">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="row"/>
                
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardTypeCode}" 
                	tabindexOverride="${tabindexOverrideBase}"
                	property="newCreditCardReceipt.financialDocumentCreditCardTypeCode" />
                	&nbsp;
                	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardType" fieldConversions="financialDocumentCreditCardTypeCode:newCreditCardReceipt.financialDocumentCreditCardTypeCode" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardVendorNumber}" 
                	tabindexOverride="${tabindexOverrideBase} + 5"
                	property="newCreditCardReceipt.financialDocumentCreditCardVendorNumber" />
                	&nbsp;
                	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardVendor" fieldConversions="financialDocumentCreditCardTypeCode:newCreditCardReceipt.financialDocumentCreditCardTypeCode,financialDocumentCreditCardVendorNumber:newCreditCardReceipt.financialDocumentCreditCardVendorNumber" lookupParameters="newCreditCardReceipt.financialDocumentCreditCardTypeCode:financialDocumentCreditCardTypeCode" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositDate}" datePicker="true"
                	tabindexOverride="${tabindexOverrideBase} + 10"
                	property="newCreditCardReceipt.creditCardDepositDate" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositReferenceNumber}"
                	tabindexOverride="${tabindexOverrideBase} + 15"
                	property="newCreditCardReceipt.creditCardDepositReferenceNumber" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardAdvanceDepositAmount}"
                	tabindexOverride="${tabindexOverrideBase} + 20"
                	property="newCreditCardReceipt.creditCardAdvanceDepositAmount" styleClass="amount" />
                </td>
                <td class="infoline">
                    <html:submit
                            property="methodToCall.addCreditCardReceipt"
                            tabindex="${tabindexOverrideBase} + 25"
                            title="Add a Credit Card Receipt"
                            alt="Add a Credit Card Receipt"
                            styleClass="btn btn-green"
                            value="Add"/>
                </td>
            </tr>
        </c:if>
        <logic:iterate id="creditCardReceipt" name="KualiForm" property="document.creditCardReceipts" indexId="ctr">
            <tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
                <th>
					<c:out value="${ctr + 1}" />
				</th>
				
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardTypeCode}" property="document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode" readOnly="${readOnly}" />
                	<c:if test="${not readOnly}">
	                	&nbsp;
    	            	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardType" fieldConversions="financialDocumentCreditCardTypeCode:document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode" />
                	</c:if>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardVendorNumber}" property="document.creditCardReceipt[${ctr}].financialDocumentCreditCardVendorNumber" readOnly="${readOnly}" />
                	<c:if test="${not readOnly}">
	                	&nbsp;
    	            	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardVendor" fieldConversions="financialDocumentCreditCardTypeCode:document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode,financialDocumentCreditCardVendorNumber:document.creditCardReceipt[${ctr}].financialDocumentCreditCardVendorNumber" lookupParameters="document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode:financialDocumentCreditCardTypeCode" />
    	            </c:if>
                </td>
                <td class="datacell">
                	<c:choose>
                        <c:when test="${readOnly}">
                            <kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositDate}" property="document.creditCardReceipt[${ctr}].creditCardDepositDate" readOnly="true" />
                        </c:when>
                        <c:otherwise>
                            <kul:dateInput attributeEntry="${ccrAttributes.creditCardDepositDate}" property="document.creditCardReceipt[${ctr}].creditCardDepositDate" />
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositReferenceNumber}" property="document.creditCardReceipt[${ctr}].creditCardDepositReferenceNumber" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardAdvanceDepositAmount}" property="document.creditCardReceipt[${ctr}].creditCardAdvanceDepositAmount" readOnly="${readOnly}" styleClass="amount"/>
                </td>
                <c:if test="${not readOnly}">
                    <td class="datacell">
                        <html:submit
                                property="methodToCall.deleteCreditCardReceipt.line${ctr}"
                                title="Delete a Credit Card Receipt"
                                alt="Delete a Credit Card Receipt"
                                styleClass="btn btn-red"
                                value="Delete"/>
                    </td>
                </c:if>
            </tr>
        </logic:iterate>
		<tr>
	 		<td class="total-line" colspan="5">&nbsp;</td>
	  		<td class="total-line" ><strong>Total: ${KualiForm.document.currencyFormattedTotalCreditCardAmount}</strong></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
	</table>
  </div>
</kul:tab>
