/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.dataaccess;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

public interface CustomerInvoiceDocumentDao {

    /**
     * Retrieves all Invoice document numbers that meet the following criteria:
     * 1) PrintIndicator = BY_USER
     * 2) PrintDate = null
     * 3) DocHeader.Status = Approved
     * <p>
     * WARNING that all the returned documents lack any workflow wiring.
     *
     * @return List of Invoice Document Numbers that match criteria
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersFromUserQueue();

    /**
     * Retrieves all Invoice document numbers in the system associated with the given
     * Processing Chart and Org, that are approved and ready to print.
     * <p>
     * WARNING that all the returned documents lack any workflow wiring.
     *
     * @param chartOfAccountsCode used for search criteria
     * @param organizationCode    used for search critiera
     * @return List of Invoice Document Numbers that match criteria
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Retrieves all Invoice document numbers in the system associated with the given
     * Billing Chart and Org, that are approved and ready to print.
     * <p>
     * WARNING that all the returned documents lack any workflow wiring.
     *
     * @param chartOfAccountsCode used for search criteria
     * @param organizationCode    used for search critiera
     * @return List of Invoice Document Numbers that match criteria
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Retrieves all Invoice document numbers in the system associated with the given
     * Billing Chart and Org, that are approved but disregards ready to print and print date as this is for Billing Statement generation.
     * <p>
     * WARNING that all the returned documents lack any workflow wiring.
     *
     * @param chartOfAccountsCode used for search criteria
     * @param organizationCode    used for search critiera
     * @return List of Invoice Document Numbers that match criteria
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersForBillingStatementByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Retrieves all Invoice document numbers in the system associated with the given
     * Processing Chart and Org.
     * <p>
     * WARNING that all the returned documents lack any workflow wiring.
     *
     * @param chartOfAccountsCode used for search criteria
     * @param organizationCode    used for search critiera
     * @return List of Invoice Document Numbers that match criteria
     */
    public List<String> getCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Retrieves all Invoice document numbers in the system associated with the given
     * Billing Chart and Org.
     * <p>
     * WARNING that all the returned documents lack any workflow wiring.
     *
     * @param chartOfAccountsCode used for search criteria
     * @param organizationCode    used for search critiera
     * @return List of Invoice Document Numbers that match criteria
     */
    public List<String> getCustomerInvoiceDocumentNumbersByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Retrieves all Open invoices, with outstanding balances.
     *
     * @return Collection of Customer Invoice Documents
     */
    public Collection getAllOpen();

    /**
     * Retrieves all Open invoices from the specified Customer Number.
     *
     * @param customerNumber used for search criteria
     * @return Collection of Customer Invoice Documents
     */
    public Collection getOpenByCustomerNumber(String customerNumber);

    /**
     * Retrieves all Open invoices, by the specified Customer Name (a LIKE customerName* search) and Customer Type Code.
     *
     * @param customerName     used for search criteria
     * @param customerTypeCode used for search criteria
     * @return Collection of Customer Invoice Documents
     */
    public Collection getOpenByCustomerNameByCustomerType(String customerName, String customerTypeCode);

    /**
     * Retrieves all Open invoices, by the specified Customer Name.
     * <p>
     * NOTE - this search uses customerName as a leading substring search,
     * so it will return anything matching a customerName that begins with the
     * value passed in.  ie, a LIKE customerName* search.
     *
     * @param customerName used for search criteria
     * @return Collection of Customer Invoice Documents
     */
    public Collection getOpenByCustomerName(String customerName);

    /**
     * Retrieves all Open invoices, by the specified Customer Type Code.
     *
     * @param customerTypeCode used for search criteria
     * @return Collection of Customer Invoice Documents
     */
    public Collection getOpenByCustomerType(String customerTypeCode);

    /**
     * Retrieves an Invoice for the specified organizationInvoiceNumber.
     *
     * @param organizationInvoiceNumber used for search criteria
     * @return matching CustomerInvoiceDocument
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber);

    /**
     * Retrieves an Invoice for the specified documentNumber.
     *
     * @param documentNumber used for search criteria
     * @return matching CustomerInvoiceDocument
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String documentNumber);

    /**
     * get all customer invoice documents that are open and with the given billing date range
     *
     * @param charts                 the selected charts of accounts
     * @param organizations          the selected organization codes
     * @param invoiceBillingDateFrom the starting invoice billing date of a range. The starting billing date is included
     * @param invoiceBillingDateTo   the ending invoice billing date of a range. The ending billing date is not included
     * @return all customer invoice documents that are open and with the billing date range
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByBilling(List<String> charts, List<String> organizations, Date invoiceBillingDateFrom, Date invoiceBillingDateTo);

    /**
     * get all customer invoice documents that are open and with the given billing date range
     *
     * @param charts                 the selected charts of accounts
     * @param organizations          the selected organization codes
     * @param invoiceBillingDateFrom the starting invoice billing date of a range. The starting billing date is included
     * @param invoiceBillingDateTo   the ending invoice billing date of a range. The ending billing date is not included
     * @return all customer invoice documents that are open and with the billing date range
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByProcessing(List<String> charts, List<String> organizations, Date invoiceBillingDateFrom, Date invoiceBillingDateTo);

    /**
     * get all customer invoice documents that are open and with the given billing date range
     *
     * @param charts                 the selected charts of accounts
     * @param accounts               the selected account numbers
     * @param invoiceBillingDateFrom the starting invoice billing date of a range. The starting billing date is included
     * @param invoiceBillingDateTo   the ending invoice billing date of a range. The ending billing date is not included
     * @return all customer invoice documents that are open and with the billing date range
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByAccounts(List<String> charts, List<String> accounts, Date invoiceBillingDateFrom, Date invoiceBillingDateTo);

    /**
     * get all customer invoice documents that are open and with the given billing date range
     *
     * @param customerTypes          the given customer type codes
     * @param invoiceBillingDateFrom the starting invoice billing date of a range. The starting billing date is included
     * @param invoiceBillingDateTo   the ending invoice billing date of a range. The ending billing date is not included
     * @return all customer invoice documents that are open and with the billing date range
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByCustomerTypes(List<String> customerTypes, Date invoiceDueDateFrom, Date invoiceDueDateTo);
}
