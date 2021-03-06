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

<%@ page import="org.kuali.kfs.coa.service.AccountService" %>
<%@ page import="org.kuali.kfs.sys.context.SpringContext" %>

<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<c:set var="accountsCanCrossCharts" value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>"/>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>
<c:set var="paymentApplicationDocumentAttributes" value="${DataDictionary['PaymentApplicationDocument'].attributes}"/>
<c:set var="invoiceAttributes" value="${DataDictionary['CustomerInvoiceDocument'].attributes}"/>
<c:set var="invoicePaidAppliedAttributes"
       value="${DataDictionary['InvoicePaidApplied'].attributes}"/>
<c:set var="customerAttributes" value="${DataDictionary['Customer'].attributes}"/>
<c:set var="customerInvoiceDetailAttributes"
       value="${DataDictionary['CustomerInvoiceDetail'].attributes}"/>
<c:set var="hasRelatedCashControlDocument" value="${null != KualiForm.cashControlDocument}"/>
<c:set var="isCustomerSelected"
       value="${!empty KualiForm.document.accountsReceivableDocumentHeader.customerNumber}"/>
<c:set var="invoiceApplications" value="${KualiForm.invoiceApplications}"/>

<kul:documentPage showDocumentInfo="true"
                  documentTypeName="PaymentApplicationDocument"
                  htmlFormAction="arPaymentApplication" renderMultipart="true"
                  showTabButtons="true">

    <sys:hiddenDocumentFields isFinancialDocument="false"/>

    <sys:documentOverview editingMode="${KualiForm.editingMode}"/>

    <ar:paymentApplicationControlInformation isCustomerSelected="${isCustomerSelected}"
                                             hasRelatedCashControlDocument="${hasRelatedCashControlDocument}"
                                             customerAttributes="${customerAttributes}"
                                             customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}"
                                             invoiceAttributes="${invoiceAttributes}" readOnly="${readOnly}"/>

    <ar:paymentApplicationSummaryOfAppliedFunds isCustomerSelected="${isCustomerSelected}"
                                                hasRelatedCashControlDocument="${hasRelatedCashControlDocument}" readOnly="${readOnly}"/>

    <ar:paymentApplicationQuickApplyToInvoice isCustomerSelected="${isCustomerSelected}"
                                              hasRelatedCashControlDocument="${hasRelatedCashControlDocument}"
                                              readOnly="${readOnly}"
                                              customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}"
                                              invoiceAttributes="${invoiceAttributes}"/>

    <ar:paymentApplicationApplyToInvoiceDetail customerAttributes="${customerAttributes}"
                                               customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}"
                                               invoiceAttributes="${invoiceAttributes}" readOnly="${readOnly}"/>

    <ar:paymentApplicationNonAr customerAttributes="${customerAttributes}"
                                isCustomerSelected="${isCustomerSelected}"
                                hasRelatedCashControlDocument="${hasRelatedCashControlDocument}"
                                readOnly="${readOnly}"
                                accountsCanCrossCharts="${accountsCanCrossCharts}"/>
    <ar:paymentApplicationUnappliedTab
            isCustomerSelected="${isCustomerSelected}" readOnly="${readOnly}"
            hasRelatedCashControlDocument="${hasRelatedCashControlDocument}"/>

    <gl:generalLedgerPendingEntries/>

    <kul:notes/>

    <kul:adHocRecipients/>

    <kul:routeLog/>

    <kul:superUserActions/>

    <sys:documentControls transactionalDocument="true"
                          extraButtons="${KualiForm.extraButtons}"/>
</kul:documentPage>
