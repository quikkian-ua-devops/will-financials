<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for customer credit memo document fields." %>
              
<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />           
              
<kul:tab tabTitle="Items" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DETAIL_ERRORS}">
    <div class="tab-container" align=center>		
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Items">
            <tr>
                <td colspan="11" class="subhead">Invoice Items</td>
            </tr>
			<tr>
			    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemQuantity}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemCode}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitOfMeasureCode}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitPrice}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.amount}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemTaxAmount}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell literalLabel="Total Amount" />
				<kul:htmlAttributeHeaderCell literalLabel="Open Invoice Amount" />				
			    <kul:htmlAttributeHeaderCell literalLabel="Actions" />
			</tr>
			<%--
			<logic:iterate
				id="customerCreditMemoDetail"
				name="KualiForm"
				property="document.customerCreditMemoDetails"
				indexId="ctr">
						<ar:customerCreditMemoDetail
							invPropertyName="document.invoice.sourceAccountingLines[${ctr}]"
							crmPropertyName="document.customerCreditMemoDetails[${ctr}]" 
			        		customerCreditMemoDetailAttributes="${DataDictionary.CustomerCreditMemoDetail.attributes}"
			        		cssClass="datacell" />
			</logic:iterate> --%>
			
			<tr>
				<td class="total-line" colspan="6">
					<strong>Credit Memo Total:</strong>
				</td>
				<td class="total-line"></td>
				<td class="total-line"></td>
				<td class="total-line"></td>
				<td />
				<td><div align="center" valign="middle" >
					<html:image property=""
	    						src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif"
	    						title="Recalculate Credit Memo Line Amounts"
	    						alt="Recalculate Credit Memo Line Amounts"
	                            styleClass="tinybutton" />
	                &nbsp;
					<html:image property=""
	    						src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
	    						title="Refresh Credit Memo Line"
	    						alt="Refresh Credit Memo Line"
	                            styleClass="tinybutton" />
	            </div>     
				</td>
			</tr> 
    	</table>
    </div>
</kul:tab>