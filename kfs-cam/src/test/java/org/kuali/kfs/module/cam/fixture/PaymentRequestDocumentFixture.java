/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.fixture;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.ArrayList;
import java.util.List;

public enum PaymentRequestDocumentFixture {

    REC1 {
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setPurapDocumentIdentifier(311);
            obj.setDocumentNumber("31");
//            obj.setApplicationDocumentStatus(PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setPurchaseOrderIdentifier(21);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(date);
            obj.setInvoiceNumber("1001");
            obj.setVendorInvoiceAmount(new KualiDecimal(19000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(date);
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(timeStamp);
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ1.newRecord());
            return obj;
        }

        ;
    },
    REC2 {
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setPurapDocumentIdentifier(321);
            obj.setDocumentNumber("32");
//            obj.setApplicationDocumentStatus(PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setPurchaseOrderIdentifier(21);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(date);
            obj.setInvoiceNumber("1003");
            obj.setVendorInvoiceAmount(new KualiDecimal(14000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(date);
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(timeStamp);
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ2.newRecord());
            return obj;
        }

        ;
    },
    REC3 {
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setPurapDocumentIdentifier(331);
            obj.setDocumentNumber("33");
//            obj.setApplicationDocumentStatus(PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setPurchaseOrderIdentifier(22);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(date);
            obj.setInvoiceNumber("1001");
            obj.setVendorInvoiceAmount(new KualiDecimal(19000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(date);
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(timeStamp);
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ3.newRecord());
            return obj;
        }

        ;
    },
    REC4 {
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setPurapDocumentIdentifier(341);
            obj.setDocumentNumber("34");
//            obj.setApplicationDocumentStatus(PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setPurchaseOrderIdentifier(22);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(date);
            obj.setInvoiceNumber("1003");
            obj.setVendorInvoiceAmount(new KualiDecimal(14000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(date);
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(timeStamp);
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ4.newRecord());
            return obj;
        }

        ;
    },
    REC5 {
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setPurapDocumentIdentifier(351);
            obj.setDocumentNumber("35");
//            obj.setApplicationDocumentStatus(PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setPurchaseOrderIdentifier(23);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(date);
            obj.setInvoiceNumber("1001");
            obj.setVendorInvoiceAmount(new KualiDecimal(19000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(date);
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(timeStamp);
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ5.newRecord());
            return obj;
        }

        ;
    },
    REC6 {
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setPurapDocumentIdentifier(361);
            obj.setDocumentNumber("36");
//            obj.setApplicationDocumentStatus(PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setPurchaseOrderIdentifier(23);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(date);
            obj.setInvoiceNumber("1003");
            obj.setVendorInvoiceAmount(new KualiDecimal(14000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(date);
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(timeStamp);
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ6.newRecord());
            return obj;
        }

        ;
    };

    public abstract PaymentRequestDocument newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        recs.add(REC4.newRecord());
        recs.add(REC5.newRecord());
        recs.add(REC6.newRecord());
        return recs;
    }
}