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
<%@ attribute name="invPropertyName" required="true"
	description="Name of form property containing the customer invoice source accounting line."%>
<%@ attribute name="crmPropertyName" required="true"
	description="Name of form property containing the customer credit memo detail."%>
<%@ attribute name="refreshMethod" required="true"
    description="methodToCall value for actionImage"%>
<%@ attribute name="recalculateMethod" required="true"
    description="methodToCall value for actionImage"%>
<%@ attribute name="displayOrangeFlower" required="true"
    description="indicates if display or not an orange flower for this detail line."%>
<%@ attribute name="isInvoiceOpenItemQuantityZero" required="true"
    description="indicates if there is zero open quantity for this detail line."%>
<%@ attribute name="cssClass" required="true"%>
<%@ attribute name="readOnly" required="true" %>
<%@ attribute name="rowHeader" required="true"
	description="The value of the header cell of this row.
              It would be the number of this row's accounting line within its group."%>
<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />
<c:set var="customerCreditMemoDetailAttributes" value="${DataDictionary.CustomerCreditMemoDetail.attributes}" />
<c:set var="salesTaxEnabled" value="${(not empty KualiForm.editingMode['salesTaxEnabled'])}" />

<tr>
	<!--  Line Number -->
	<th class="${cssClass}" style="text-align:right" rowspan="4" >
		<c:if test="${displayOrangeFlower}" >
	    	<img src="${ConfigProperties.kr.externalizable.images.url}asterisk_orange.png" alt="changed"/>
	    	&nbsp;
	    </c:if>
		${rowHeader}:

	<!--  Quantity -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemQuantity}"
			property="${invPropertyName}.invoiceItemQuantity"
			readOnly="true" />

	<!--  Item Code -->
	<td class="${cssClass}" style="text-align:center" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemCode}"
			property="${invPropertyName}.invoiceItemCode"
			readOnly="true" />

	<!--  UOM -->
	<td class="${cssClass}" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitOfMeasureCode}"
			property="${invPropertyName}.invoiceItemUnitOfMeasureCode"
			readOnly="true" />

	<!--  Description -->
	<td class="${cssClass}" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}"
			property="${invPropertyName}.invoiceItemDescription"
			readOnly="true" />

	<!--  Unit Price -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitPrice}"
			property="${invPropertyName}.invoiceItemUnitPrice"
			readOnly="true" />

	<!--  Item Amount -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.amount}"
			property="${invPropertyName}.invoiceItemPreTaxAmount"
			readOnly="true" />

	<!--  Tax Amount -->
	<c:if test="${salesTaxEnabled}">
		<td class="${cssClass}" style="text-align:right" >
			<kul:htmlControlAttribute
				attributeEntry="${customerInvoiceDetailAttributes.invoiceItemTaxAmount}"
				property="${invPropertyName}.invoiceItemTaxAmount"
				readOnly="true" />
	</c:if>
	<!--  Total Amount -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.invoiceLineTotalAmount}"
			property="${crmPropertyName}.invoiceLineTotalAmount"
			readOnly="true" />

	<!--  Open Invoice Quantity -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.invoiceOpenItemQuantity}"
			property="${crmPropertyName}.invoiceOpenItemQuantity"
			readOnly="true" />

	<!--  Open Invoice Amount -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.invoiceOpenItemAmount}"
			property="${crmPropertyName}.invoiceOpenItemAmount"
			readOnly="true" />

	<!--  If not readOnly mode -> show the buttons Recalculate/Refresh -->
	<c:if test="${not readOnly}" >
		<!--  Actions -->
		<td rowspan="4"><div align="center" class="middle" >
			<html:submit property="methodToCall.${recalculateMethod}"
	    		value="Recalculate"
	    		title="Recalculate Credit Memo Line Amounts"
	    		alt="Recalculate Credit Memo Line Amounts"
				styleClass="btn btn-default small" />
	    	&nbsp;
			<html:submit property="methodToCall.${refreshMethod}"
	    		value="Refresh"
	    		title="Refresh Credit Memo Line"
	    		alt="Refresh Credit Memo Line"
				styleClass="btn btn-default small" />
		</div>
		</td>
	</c:if>
</tr>
<tr>
	<!--  If not readOnly mode -> make "Quantity" editable -->
	<c:if test="${not readOnly && not isInvoiceOpenItemQuantityZero}" >
		<!--  CRM Quantity -->
		<td class="${cssClass}" style="text-align:right" >
			<kul:htmlControlAttribute
				attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemQuantity}"
				property="${crmPropertyName}.creditMemoItemQuantity"
				readOnly="false" />
		</td>
	</c:if>
	<c:if test="${not readOnly && isInvoiceOpenItemQuantityZero}" >
		<!--  CRM Quantity -->
		<td class="${cssClass}" style="text-align:right" >
			<kul:htmlControlAttribute
				attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemQuantity}"
				property="${crmPropertyName}.creditMemoItemQuantity"
				readOnly="true" />
		</td>
	</c:if>
	<!--  If readOnly mode -> make "Quantity" read only -->
	<c:if test="${readOnly}" >
		<!--  CRM Quantity -->
		<td class="${cssClass}" style="text-align:right" >
			<kul:htmlControlAttribute
				attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemQuantity}"
				property="${crmPropertyName}.creditMemoItemQuantity"
				readOnly="true" />
		</td>
	</c:if>

	<!--  CRM 4 empty columns -->
	<td class="${cssClass}" />
	<td class="${cssClass}" />
	<td class="${cssClass}" />
	<td class="${cssClass}" />

	<!--  If not readOnly mode -> make "Amount" editable -->
	<c:if test="${not readOnly && not isInvoiceOpenItemQuantityZero}" >
		<!--  CRM Item Amount -->
		<td class="${cssClass}" style="text-align:right" >
			<kul:htmlControlAttribute
				attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemTotalAmount}"
				property="${crmPropertyName}.creditMemoItemTotalAmount"
				readOnly="false" />
		</td>
	</c:if>
	<c:if test="${not readOnly && isInvoiceOpenItemQuantityZero}" >
		<!--  CRM Item Amount -->
		<td class="${cssClass}" style="text-align:right" >
			<kul:htmlControlAttribute
				attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemTotalAmount}"
				property="${crmPropertyName}.creditMemoItemTotalAmount"
				readOnly="true" />
		</td>
	</c:if>
	<!--  If readOnly mode -> make "Amount" read only -->
	<c:if test="${readOnly}" >
		<!--  CRM Item Amount -->
		<td class="${cssClass}" style="text-align:right" >
			<kul:htmlControlAttribute
				attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemTotalAmount}"
				property="${crmPropertyName}.creditMemoItemTotalAmount"
				readOnly="true" />
		</td>
	</c:if>

	<!--  CRM Tax Amount -->
	<c:if test="${salesTaxEnabled}">
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemTaxAmount}"
			property="${crmPropertyName}.creditMemoItemTaxAmount"
			readOnly="true" />
	</td>
	</c:if>

	<!--  CRM Total Amount -->
	<td class="${cssClass}" style="text-align:right" colspan="1" >
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.creditMemoLineTotalAmount}"
			property="${crmPropertyName}.creditMemoLineTotalAmount"
			readOnly="true" />
	</td>

	<!--  CRM 2 empty columns -->
	<td class="${cssClass}" />
	<td class="${cssClass}" />
<tr/>

<ar:customerCreditMemoDetailAccountingInfo
	invPropertyName="${invPropertyName}"
	cssClass="${cssClass}" />
