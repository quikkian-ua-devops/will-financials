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
package org.kuali.kfs.coa.dataaccess;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.rice.kim.api.identity.Person;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * This interface defines what methods of data retrieval should be allowed for {@link org.kuali.kfs.coa.businessobject.Account}, and
 * {@link org.kuali.kfs.coa.businessobject.AccountDelegate}. It also defines a method for checking if a given User is responsible
 * for an Account
 */
public interface AccountDao {

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getPrimaryDelegationByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     * java.lang.String)
     */
    public List getPrimaryDelegationByExample(AccountDelegate delegateExample, Date currentSqlDate, String totalDollarAmount);

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getSecondaryDelegationsByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     * java.lang.String)
     */
    public List getSecondaryDelegationsByExample(AccountDelegate delegateExample, Date currentSqlDate, String totalDollarAmount);

    /**
     * fetch the AccountResponsibility objects that the user has associated with them
     *
     * @param kualiUser
     * @param currentDate current date
     * @return a list of AccountResponsibility objects
     */
    public List getAccountsThatUserIsResponsibleFor(Person kualiUser, java.util.Date currentDate);

    /**
     * This method should determine if the given user has any responsibilities on the given account
     *
     * @param person         the user to check responsibilities for
     * @param account        the account to check responsibilities on
     * @param currentSqlDate current Sql date
     * @return true if user is somehow responsible for account, false if otherwise
     */
    public boolean determineUserResponsibilityOnAccount(Person person, Account account, Date currentSqlDate);

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     *
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts();

    /**
     * Retrieves all active accounts from the database where the given principal is the fiscal officer
     *
     * @param principalId    the principal id of the fiscal officer
     * @param currentSqlDate current sql date
     * @return an Iterator of active Accounts
     */
    public abstract Iterator<Account> getActiveAccountsForFiscalOfficer(String principalId, Date currentSqlDate);

    /**
     * Retrieves all expired accounts from the database where the given principal is the fiscal officer
     *
     * @param principalId    the principal id of the fiscal officer
     * @param currentSqlDate current Sql Date
     * @return an Iterator of expired Accounts
     */
    public abstract Iterator<Account> getExpiredAccountsForFiscalOfficer(String principalId, Date currentSqlDate);

    /**
     * Retrieves all active accounts from the database where the given principal is the account supervisor
     *
     * @param principalId    the principal id of the account supervisor
     * @param currentSalDate
     * @return an Iterator of active Accounts
     */
    public abstract Iterator<Account> getActiveAccountsForAccountSupervisor(String principalId, Date currentSalDate);

    /**
     * Retrieves all active accounts from the database where the given principal is the account supervisor
     *
     * @param principalId    the principal id of the account supervisor
     * @param currentSqlDate current Sql Date
     * @return an Iterator of expired Accounts
     */
    public abstract Iterator<Account> getExpiredAccountsForAccountSupervisor(String principalId, Date currentSqlDate);

    /**
     * Determines if the given principal is the fiscal officer of any non-closed account
     *
     * @param principalId the principal to check for the fiscal officer role
     * @return true if the principal is a fiscal officer for any non-closed account, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormFiscalOfficer(String principalId);

    /**
     * Determines if the given principal is the account supervisor of any non-closed account
     *
     * @param principalId the principal to check for the account supervisor role
     * @return true if the principal is a account supervisor for any non-closed account, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormAccountSupervisor(String principalId);

    /**
     * Determines if the given principal is the account manager of any non-closed account
     *
     * @param principalId the principal to check for the account manager role
     * @return true if the principal is a account manager for any non-closed account, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormAccountManager(String principalId);

    public Collection<Account> getAccountsForAccountNumber(String accountNumber);
}
