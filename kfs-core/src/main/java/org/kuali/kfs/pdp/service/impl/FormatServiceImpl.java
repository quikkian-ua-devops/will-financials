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
package org.kuali.kfs.pdp.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.generic.NumberTool;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.batch.service.ExtractPaymentService;
import org.kuali.kfs.pdp.businessobject.AchAccountNumber;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.DisbursementType;
import org.kuali.kfs.pdp.businessobject.FormatProcess;
import org.kuali.kfs.pdp.businessobject.FormatProcessSummary;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.pdp.businessobject.PaymentChangeCode;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.businessobject.ProcessSummary;
import org.kuali.kfs.pdp.dataaccess.FormatPaymentDao;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.dataaccess.ProcessDao;
import org.kuali.kfs.pdp.service.AchService;
import org.kuali.kfs.pdp.service.FormatService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.pdp.service.impl.exception.FormatException;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.mail.VelocityMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.util.GlobalVariablesUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Transactional
public class FormatServiceImpl implements FormatService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatServiceImpl.class);

    protected PaymentDetailDao paymentDetailDao;
    protected PaymentGroupDao paymentGroupDao;
    protected ProcessDao processDao;
    protected AchService achService;
    protected PendingTransactionService glPendingTransactionService;
    protected ParameterService parameterService;
    protected FormatPaymentDao formatPaymentDao;
    protected SchedulerService schedulerService;
    protected BusinessObjectService businessObjectService;
    protected PaymentGroupService paymentGroupService;
    protected DateTimeService dateTimeService;
    protected ExtractPaymentService extractPaymentService;
    protected PersonService personService;
    protected ConfigurationService kualiConfigurationService;
    protected EmailService emailService;
    protected String emailTemplateUrl;

    public FormatServiceImpl() {
        super();
    }

    @Override
    public FormatSelection getDataForFormat(Person user) {
        String campusCode = user.getCampusCode();

        return getDataForFormat(campusCode);
    }

    @Override
    public FormatSelection getDataForFormat(String campusCode) {
        Date formatStartDate = getFormatProcessStartDate(campusCode);

        // create new FormatSelection object an set the campus code and the start date
        FormatSelection formatSelection = new FormatSelection();
        formatSelection.setCampus(campusCode);
        formatSelection.setStartDate(formatStartDate);

        // if format process not started yet, populate the other data as well
        if (formatStartDate == null) {
            formatSelection.setCustomerList(getAllCustomerProfiles());
            formatSelection.setRangeList(getAllDisbursementNumberRanges());
        }

        return formatSelection;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Date getFormatProcessStartDate(String campus) {
        LOG.debug("getFormatProcessStartDate() started");

        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PHYS_CAMPUS_PROCESS_CODE, campus);
        FormatProcess formatProcess = this.businessObjectService.findByPrimaryKey(FormatProcess.class, primaryKeys);

        if (formatProcess != null) {
            LOG.debug("getFormatProcessStartDate() found");
            return new Date(formatProcess.getBeginFormat().getTime());
        } else {
            LOG.debug("getFormatProcessStartDate() not found");
            return null;
        }
    }

    @Override
    public FormatProcessSummary startFormatProcess(Person user, String campus, List<CustomerProfile> customers, Date paydate, String paymentTypes) {
        LOG.debug("startFormatProcess() started");

        for (CustomerProfile element : customers) {
            LOG.debug("startFormatProcess() Customer: " + element);
        }

        // Create the process
        Date d = new Date();
        PaymentProcess paymentProcess = new PaymentProcess();
        paymentProcess.setCampusCode(campus);
        paymentProcess.setProcessUser(user);
        paymentProcess.setProcessTimestamp(new Timestamp(d.getTime()));

        this.businessObjectService.save(paymentProcess);

        // add an entry in the format process table (to lock the format process)
        FormatProcess formatProcess = new FormatProcess();

        formatProcess.setPhysicalCampusProcessCode(campus);
        formatProcess.setBeginFormat(dateTimeService.getCurrentTimestamp());
        formatProcess.setPaymentProcIdentifier(paymentProcess.getId().intValue());

        this.businessObjectService.save(formatProcess);


        Timestamp now = new Timestamp((new Date()).getTime());
        java.sql.Date sqlDate = new java.sql.Date(paydate.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 59);
        c.set(Calendar.AM_PM, Calendar.PM);
        Timestamp paydateTs = new Timestamp(c.getTime().getTime());

        LOG.debug("startFormatProcess() last update = " + now);
        LOG.debug("startFormatProcess() entered paydate = " + paydate);
        LOG.debug("startFormatProcess() actual paydate = " + paydateTs);

        PaymentStatus format = this.businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.FORMAT);

        List customerIds = new ArrayList();
        for (Iterator iter = customers.iterator(); iter.hasNext(); ) {
            CustomerProfile element = (CustomerProfile) iter.next();
            customerIds.add(element.getId());
        }

        // Mark all of them ready for format
        Iterator groupIterator = formatPaymentDao.markPaymentsForFormat(customerIds, paydateTs, paymentTypes);

        while (groupIterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) groupIterator.next();
            paymentGroup.setLastUpdate(paydateTs);// delete this one
            paymentGroup.setPaymentStatus(format);
            paymentGroup.setProcess(paymentProcess);
            businessObjectService.save(paymentGroup);
        }


        // summarize them
        FormatProcessSummary preFormatProcessSummary = new FormatProcessSummary();
        Iterator<PaymentGroup> iterator = this.paymentGroupService.getByProcess(paymentProcess);

        while (iterator.hasNext()) {
            PaymentGroup paymentGroup = iterator.next();
            preFormatProcessSummary.add(paymentGroup);
        }

        // if no payments found for format clear the format process
        if (preFormatProcessSummary.getProcessSummaryList().size() == 0) {
            LOG.debug("startFormatProcess() No payments to process.  Format process ending");
            clearUnfinishedFormat(paymentProcess.getId().intValue());// ?? maybe call end format process
        }

        return preFormatProcessSummary;
    }


    /**
     * This method gets the maximum number of lines in a note.
     *
     * @return the maximum number of lines in a note
     */
    protected int getMaxNoteLines() {
        String maxLines = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);
        if (StringUtils.isBlank(maxLines)) {
            throw new RuntimeException("System parameter for max note lines is blank");
        }

        return Integer.parseInt(maxLines);
    }

    @Override
    public void performFormat(Integer processId) throws FormatException {
        LOG.debug("performFormat() started");
        Person user = GlobalVariables.getUserSession().getPerson();

        // get the PaymentProcess for the given id
        @SuppressWarnings("rawtypes")
        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PaymentProcess.PAYMENT_PROCESS_ID, processId);
        PaymentProcess paymentProcess = this.businessObjectService.findByPrimaryKey(PaymentProcess.class, primaryKeys);
        if (paymentProcess == null) {
            LOG.error("performFormat() Invalid proc ID " + processId);
            throw new RuntimeException("Invalid proc ID");
        }

        String processCampus = paymentProcess.getCampusCode();
        FormatProcessSummary postFormatProcessSummary = new FormatProcessSummary();

        // step 1 get ACH or Check, Bank info, ACH info, sorting
        Iterator<PaymentGroup> paymentGroupIterator = this.paymentGroupService.getByProcess(paymentProcess);
        while (paymentGroupIterator.hasNext()) {
            PaymentGroup paymentGroup = paymentGroupIterator.next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("performFormat() Step 1 Payment Group ID " + paymentGroup.getId());
            }

            // process payment group data
            boolean groupProcessed = processPaymentGroup(paymentGroup, paymentProcess);
            if (!groupProcessed) {
                LOG.info("Sending failure email to " + user.getEmailAddress());
                sendFailureEmail(user.getEmailAddress(), processId);
                throw new FormatException("Error encountered during format");
            }

            // save payment group
            this.businessObjectService.save(paymentGroup);

            // Add to summary information
            postFormatProcessSummary.add(paymentGroup);
        }

        // step 2 assign disbursement numbers and combine checks into one if possible
        boolean disbursementNumbersAssigned = assignDisbursementNumbersAndCombineChecks(paymentProcess, postFormatProcessSummary);
        if (!disbursementNumbersAssigned) {
            sendFailureEmail(user.getEmailAddress(), processId);
            throw new FormatException("Error encountered during format");
        }

        // step 3 save the summarizing info
        LOG.debug("performFormat() Save summarizing information");
        postFormatProcessSummary.save();

        // step 4 set formatted indicator to true and save in the db
        paymentProcess.setFormattedIndicator(true);
        businessObjectService.save(paymentProcess);

        // step 5 end the format process for this campus
        LOG.debug("performFormat() End the format process for this campus");
        endFormatProcess(processCampus);

        // step 6 tell the extract batch job to start
        LOG.debug("performFormat() Start extract");
        extractChecks();

        LOG.info("Send summary email for processId: " + processId);
        sendSummaryEmail(postFormatProcessSummary);

    }

    /**
     * Send format summary in email
     *
     * @param postFormatProcessSummary
     */
    protected void sendSummaryEmail(FormatProcessSummary postFormatProcessSummary) {
        HashMap<CustomerProfile, List<KualiDecimal>> achSummaryMap = new HashMap<>();
        HashMap<CustomerProfile, List<KualiDecimal>> checkSummaryMap = new HashMap<>();

        KualiInteger formatTotalCount = KualiInteger.ZERO;
        KualiDecimal formatTotalAmount = KualiDecimal.ZERO;
        for (ProcessSummary procSum : postFormatProcessSummary.getProcessSummaryList()) {
            if (PdpConstants.DisbursementTypeCodes.ACH.equals(procSum.getDisbursementType().getCode())) {
                addSummaryToCustomerProfileMap(achSummaryMap, procSum);
            } else if (PdpConstants.DisbursementTypeCodes.CHECK.equals(procSum.getDisbursementType().getCode())) {
                addSummaryToCustomerProfileMap(checkSummaryMap, procSum);
            }

            formatTotalCount = formatTotalCount.add(procSum.getProcessTotalCount());
            formatTotalAmount = formatTotalAmount.add(procSum.getProcessTotalAmount());
        }

        final Map<String, Object> templateVariables = new HashMap<>();

        DateFormatter dateFormatter = new DateFormatter();
        templateVariables.put(KFSConstants.ProcurementCardEmailVariableTemplate.DOC_CREATE_DATE, dateFormatter.formatForPresentation(new Date()));

        // set email subject message
        String processId = postFormatProcessSummary.getProcessId().toString();

        String emailSubject = MessageFormat.format(kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.Format.MESSAGE_PDP_FORMAT_BATCH_EMAIL_SUBJECT), new Object[]{processId});

        templateVariables.put("emailSubject", emailSubject);
        templateVariables.put("achSummaryMap", achSummaryMap);
        templateVariables.put("checkSummaryMap", checkSummaryMap);
        templateVariables.put("formatTotalCount", formatTotalCount);
        templateVariables.put("formatTotalAmount", formatTotalAmount);
        templateVariables.put("numberTool", new NumberTool());

        VelocityMailMessage mailMessage = new VelocityMailMessage();
        mailMessage.setSubject(emailSubject);
        mailMessage.setTemplateUrl(emailTemplateUrl);
        mailMessage.setTemplateVariables(templateVariables);
        getSummaryEmailReceivers().stream().forEach(r -> mailMessage.addToAddress(r));
        if ( mailMessage.getToAddresses().size() == 0 ) {
            mailMessage.addToAddress(emailService.getDefaultToAddress());
        }
        mailMessage.setFromAddress(emailService.getDefaultFromAddress());

        emailService.sendMessage(mailMessage,true);
    }

    protected Collection<String> getSummaryEmailReceivers() {
        return parameterService.getParameterValuesAsString(KFSConstants.CoreModuleNamespaces.PDP, PdpConstants.FormatCheckACHParameters.PDP_FORMAT_CHECK_ACH_STEP, PdpConstants.FormatCheckACHParameters.FORMAT_SUMMARY_TO_EMAIL_ADDRESSES);
    }

    /**
     * Add summary totals to customer profiles map
     *
     * @param summaryByCustomerProfile
     * @param procSum
     * @return
     */
    protected void addSummaryToCustomerProfileMap(Map<CustomerProfile, List<KualiDecimal>> summaryByCustomerProfile, ProcessSummary procSum) {
        // Gate Keeper 1
        if (ObjectUtils.isNull(summaryByCustomerProfile))
            summaryByCustomerProfile = new HashMap<>();

        // Gate Keeper 2
        if (ObjectUtils.isNull(procSum) || ObjectUtils.isNull(procSum.getCustomer())) return;

        CustomerProfile customerProfile = procSum.getCustomer();

        List<KualiDecimal> summary = new ArrayList<>();

        // check if customer profile already exists in the map
        if (summaryByCustomerProfile.containsKey(customerProfile)) {
            summary = summaryByCustomerProfile.get(customerProfile);

            KualiDecimal totCount = summary.get(0).add(procSum.getProcessTotalCount().kualiDecimalValue());
            KualiDecimal totAmount = summary.get(1).add(procSum.getProcessTotalAmount());

            summary.set(0, totCount);
            summary.set(1, totAmount);

            // if no key exists
        } else {
            summary.add(procSum.getProcessTotalCount().kualiDecimalValue());
            summary.add(procSum.getProcessTotalAmount());
        }

        // add summary to customer profile
        summaryByCustomerProfile.put(customerProfile, summary);
    }

    /**
     * This method processes the payment group data.
     *
     * @param paymentGroup
     * @param paymentProcess
     */
    protected boolean processPaymentGroup(PaymentGroup paymentGroup, PaymentProcess paymentProcess) {
        boolean successful = true;

        paymentGroup.setSortValue(paymentGroupService.getSortGroupId(paymentGroup));
        paymentGroup.setPhysCampusProcessCd(paymentProcess.getCampusCode());
        paymentGroup.setProcess(paymentProcess);

        // If any one of the payment details in the group are negative, we always force a check
        boolean noNegativeDetails = true;

        // If any one of the payment details in the group are negative, we always force a check
        List<PaymentDetail> paymentDetailsList = paymentGroup.getPaymentDetails();
        for (PaymentDetail paymentDetail : paymentDetailsList) {
            if (paymentDetail.getNetPaymentAmount().doubleValue() < 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("performFormat() Payment Group " + paymentGroup + " has payment detail net payment amount " + paymentDetail.getNetPaymentAmount());
                    LOG.debug("performFormat() Forcing a Check for Group");
                }
                noNegativeDetails = false;
                break;
            }
        }

        // determine whether payment should be ACH or Check
        CustomerProfile customer = paymentGroup.getBatch().getCustomerProfile();

        PayeeACHAccount payeeAchAccount = null;
        boolean isCheck = true;
        if (PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(paymentGroup.getPayeeIdTypeCd()) || PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(paymentGroup.getPayeeIdTypeCd()) || PdpConstants.PayeeIdTypeCodes.ENTITY.equals(paymentGroup.getPayeeIdTypeCd())) {
            if (StringUtils.isNotBlank(paymentGroup.getPayeeId()) && !paymentGroup.getPymtAttachment() && !paymentGroup.getProcessImmediate() && !paymentGroup.getPymtSpecialHandling() && (customer.getAchTransactionType() != null) && noNegativeDetails) {
                LOG.debug("performFormat() Checking ACH");
                payeeAchAccount = achService.getAchInformation(paymentGroup.getPayeeIdTypeCd(), paymentGroup.getPayeeId(), customer.getAchTransactionType());
                isCheck = (payeeAchAccount == null);
            }
        }

        DisbursementType disbursementType = null;
        if (isCheck) {
            PaymentStatus paymentStatus = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.PENDING_CHECK);
            paymentGroup.setPaymentStatus(paymentStatus);

            disbursementType = businessObjectService.findBySinglePrimaryKey(DisbursementType.class, PdpConstants.DisbursementTypeCodes.CHECK);
            paymentGroup.setDisbursementType(disbursementType);
        } else {
            PaymentStatus paymentStatus = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.PENDING_ACH);
            paymentGroup.setPaymentStatus(paymentStatus);

            disbursementType = businessObjectService.findBySinglePrimaryKey(DisbursementType.class, PdpConstants.DisbursementTypeCodes.ACH);
            paymentGroup.setDisbursementType(disbursementType);

            paymentGroup.setAchBankRoutingNbr(payeeAchAccount.getBankRoutingNumber());
            paymentGroup.setAdviceEmailAddress(payeeAchAccount.getPayeeEmailAddress());
            paymentGroup.setAchAccountType(payeeAchAccount.getBankAccountTypeCode());

            AchAccountNumber achAccountNumber = new AchAccountNumber();
            achAccountNumber.setAchBankAccountNbr(payeeAchAccount.getBankAccountNumber());
            achAccountNumber.setId(paymentGroup.getId());
            paymentGroup.setAchAccountNumber(achAccountNumber);
        }

        // set payment group bank
        successful &= validateAndUpdatePaymentGroupBankCode(paymentGroup, disbursementType, customer);

        return successful;
    }

    /**
     * Verifies a valid bank is set on the payment group. A bank is valid if it is active and supports the given disbursement type.
     * If the payment group already has an assigned bank it will be used unless it is not valid. If the payment group bank is not
     * valid or was not given the bank specified on the customer profile to use for the given disbursement type is used. If this
     * bank is inactive then its continuation bank is used. If not valid bank to use is found an error is added to the global
     * message map.
     *
     * @param paymentGroup     group to set bank on
     * @param disbursementType type of disbursement for given payment group
     * @param customer         customer profile for payment group
     * @return boolean true if a valid bank is set on the payment group, false otherwise
     */
    protected boolean validateAndUpdatePaymentGroupBankCode(PaymentGroup paymentGroup, DisbursementType disbursementType, CustomerProfile customer) {
        boolean bankValid = true;

        String originalBankCode = paymentGroup.getBankCode();
        if (ObjectUtils.isNull(paymentGroup.getBank()) || ((disbursementType.getCode().equals(PdpConstants.DisbursementTypeCodes.ACH) && !paymentGroup.getBank().isBankAchIndicator()) || (disbursementType.getCode().equals(PdpConstants.DisbursementTypeCodes.CHECK) && !paymentGroup.getBank().isBankCheckIndicator())) || !paymentGroup.getBank().isActive()) {
            CustomerBank customerBank = customer.getCustomerBankByDisbursementType(disbursementType.getCode());
            if (ObjectUtils.isNotNull(customerBank) && customerBank.isActive() && ObjectUtils.isNotNull(customerBank.getBank()) && customerBank.getBank().isActive()) {
                paymentGroup.setBankCode(customerBank.getBankCode());
                paymentGroup.setBank(customerBank.getBank());
            } else if (ObjectUtils.isNotNull(customerBank) && ObjectUtils.isNotNull(customerBank.getBank()) && ObjectUtils.isNotNull(customerBank.getBank().getContinuationBank()) && customerBank.getBank().getContinuationBank().isActive()) {
                paymentGroup.setBankCode(customerBank.getBank().getContinuationBank().getBankCode());
                paymentGroup.setBank(customerBank.getBank().getContinuationBank());
            }
        }

        if (ObjectUtils.isNull(paymentGroup.getBank())) {
            LOG.error("performFormat() A bank is needed for " + disbursementType.getName() + " disbursement type for customer: " + customer);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_BANK_MISSING, customer.getCustomerShortName());
            bankValid = false;

            return bankValid;
        }

        // create payment history record if bank was changed
        if (StringUtils.isNotBlank(originalBankCode) && !paymentGroup.getBankCode().equals(originalBankCode)) {
            PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();

            PaymentChangeCode paymentChangeCode = businessObjectService.findBySinglePrimaryKey(PaymentChangeCode.class, PdpConstants.PaymentChangeCodes.BANK_CHNG_CD);
            paymentGroupHistory.setPaymentChange(paymentChangeCode);
            paymentGroupHistory.setOrigBankCode(originalBankCode);

            Bank originalBank = businessObjectService.findBySinglePrimaryKey(Bank.class, originalBankCode);
            paymentGroupHistory.setBank(originalBank);
            paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());

            Person changeUser = personService.getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
            paymentGroupHistory.setChangeUser(changeUser);
            paymentGroupHistory.setPaymentGroup(paymentGroup);
            paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));

            // save payment group history
            businessObjectService.save(paymentGroupHistory);
        }

        return bankValid;
    }

    /**
     * This method assigns disbursement numbers and tries to combine payment groups with disbursement type check if possible.
     *
     * @param paymentProcess
     * @param postFormatProcessSummary
     */
    protected boolean assignDisbursementNumbersAndCombineChecks(PaymentProcess paymentProcess, FormatProcessSummary postFormatProcessSummary) {
        boolean successful = true;

        // keep a map with paymentGroupKey and PaymentInfo (disbursementNumber, noteLines)
        Map<String, PaymentInfo> combinedChecksMap = new HashMap<>();

        Iterator<PaymentGroup> paymentGroupIterator = this.paymentGroupService.getByProcess(paymentProcess);
        int maxNoteLines = getMaxNoteLines();

        while (paymentGroupIterator.hasNext()) {
            PaymentGroup paymentGroup = paymentGroupIterator.next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("performFormat() Payment Group ID " + paymentGroup.getId());
            }

            // Use the customer's profile's campus code to check for disbursement ranges
            String campus = paymentGroup.getBatch().getCustomerProfile().getDefaultPhysicalCampusProcessingCode();
            List<DisbursementNumberRange> disbursementRanges = paymentDetailDao.getDisbursementNumberRanges(campus);

            DisbursementNumberRange range = getRange(disbursementRanges, paymentGroup.getBank(), paymentGroup.getDisbursementType().getCode());

            if (range == null) {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_DISBURSEMENT_MISSING, campus, paymentGroup.getBank().getBankCode(), paymentGroup.getDisbursementType().getCode());
                successful = false;
                return successful;
            }

            if (PdpConstants.DisbursementTypeCodes.CHECK.equals(paymentGroup.getDisbursementType().getCode())) {
                if (paymentGroup.getPymtAttachment().booleanValue() || paymentGroup.getProcessImmediate().booleanValue() || paymentGroup.getPymtSpecialHandling().booleanValue() || (!paymentGroup.getCombineGroups())) {
                    assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
                } else {
                    String paymentGroupKey = paymentGroup.toStringKey();
                    // check if there was another paymentGroup we can combine with
                    if (combinedChecksMap.containsKey(paymentGroupKey)) {
                        PaymentInfo paymentInfo = combinedChecksMap.get(paymentGroupKey);
                        paymentInfo.noteLines = paymentInfo.noteLines.add(new KualiInteger(paymentGroup.getNoteLines()));

                        // if noteLines don't excede the maximum assign the same disbursementNumber
                        if (paymentInfo.noteLines.intValue() <= maxNoteLines) {
                            KualiInteger checkNumber = paymentInfo.disbursementNumber;
                            paymentGroup.setDisbursementNbr(checkNumber);

                            // update payment info for new noteLines value
                            combinedChecksMap.put(paymentGroupKey, paymentInfo);
                        }
                        // it noteLines more than maxNoteLines we remove the old entry and get a new disbursement number
                        else {
                            // remove old entry for this paymentGroupKey
                            combinedChecksMap.remove(paymentGroupKey);

                            // get a new check number and the paymentGroup noteLines
                            KualiInteger checkNumber = assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
                            int noteLines = paymentGroup.getNoteLines();

                            // create new payment info with these two
                            paymentInfo = new PaymentInfo(checkNumber, new KualiInteger(noteLines));

                            // add new entry in the map for this paymentGroupKey
                            combinedChecksMap.put(paymentGroupKey, paymentInfo);

                        }
                    }
                    // if no entry in the map for this payment group we create a new one
                    else {
                        // get a new check number and the paymentGroup noteLines
                        KualiInteger checkNumber = assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
                        int noteLines = paymentGroup.getNoteLines();

                        // create new payment info with these two
                        PaymentInfo paymentInfo = new PaymentInfo(checkNumber, new KualiInteger(noteLines));

                        // add new entry in the map for this paymentGroupKey
                        combinedChecksMap.put(paymentGroupKey, paymentInfo);
                    }
                }
            } else if (PdpConstants.DisbursementTypeCodes.ACH.equals(paymentGroup.getDisbursementType().getCode())) {
                assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
            } else {
                // if it isn't check or ach, we're in trouble
                LOG.error("assignDisbursementNumbers() Payment group " + paymentGroup.getId() + " must be CHCK or ACH.  It is: " + paymentGroup.getDisbursementType());
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, "assignDisbursementNumbers() Payment group " + paymentGroup.getId() + " must be CHCK or ACH.  It is: " + paymentGroup.getDisbursementType());
                throw new IllegalArgumentException("Payment group " + paymentGroup.getId() + " must be Check or ACH");
            }

            this.businessObjectService.save(paymentGroup);

            // Generate a GL entry for CHCK & ACH
            glPendingTransactionService.generatePaymentGeneralLedgerPendingEntry(paymentGroup);

            // Update all the ranges
            LOG.debug("assignDisbursementNumbers() Save ranges");
            for (DisbursementNumberRange element : disbursementRanges) {
                this.businessObjectService.save(element);
            }
        }

        return successful;
    }

    /**
     * This method gets a new disbursement number and sets it on the payment group and process summary.
     *
     * @param campus
     * @param range
     * @param paymentGroup
     * @param postFormatProcessSummary
     * @return
     */
    protected KualiInteger assignDisbursementNumber(String campus, DisbursementNumberRange range, PaymentGroup paymentGroup, FormatProcessSummary postFormatProcessSummary) {
        KualiInteger disbursementNumber = new KualiInteger(1 + range.getLastAssignedDisbNbr().intValue());

        if (disbursementNumber.isGreaterThan(range.getEndDisbursementNbr())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_DISBURSEMENT_EXHAUSTED, campus, paymentGroup.getBank().getBankCode(), paymentGroup.getDisbursementType().getCode());

            throw new FormatException("No more disbursement numbers for bank code " + paymentGroup.getBank().getBankCode() + " and disbursement type code " + paymentGroup.getDisbursementType().getCode());
        }

        paymentGroup.setDisbursementNbr(disbursementNumber);
        range.setLastAssignedDisbNbr(disbursementNumber);

        // Update the summary information
        postFormatProcessSummary.setDisbursementNumber(paymentGroup, disbursementNumber.intValue());

        return disbursementNumber;
    }

    /**
     * runs the extract process.
     */
    protected void extractChecks() {
        LOG.debug("extractChecks() started");

        extractPaymentService.extractChecks();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void clearUnfinishedFormat(Integer processId) {
        LOG.debug("clearUnfinishedFormat() started");

        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PaymentProcess.PAYMENT_PROCESS_ID, processId);
        PaymentProcess paymentProcess = this.businessObjectService.findByPrimaryKey(PaymentProcess.class, primaryKeys);
        if (LOG.isDebugEnabled()) {
            LOG.debug("clearUnfinishedFormat() Process: " + paymentProcess);
        }

        Timestamp now = new Timestamp((new Date()).getTime());

        PaymentStatus openStatus = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.OPEN);

        Iterator groupIterator = formatPaymentDao.unmarkPaymentsForFormat(paymentProcess);

        while (groupIterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) groupIterator.next();
            paymentGroup.setLastUpdate(now);
            paymentGroup.setPaymentStatus(openStatus);
            businessObjectService.save(paymentGroup);
        }

        endFormatProcess(paymentProcess.getCampusCode());
    }

    @Override
    public void resetFormatPayments(Integer processId) {
        LOG.debug("resetFormatPayments() started");
        clearUnfinishedFormat(processId);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void endFormatProcess(String campus) {
        LOG.debug("endFormatProcess() starting");

        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PHYS_CAMPUS_PROCESS_CODE, campus);

        businessObjectService.deleteMatching(FormatProcess.class, primaryKeys);
    }

    @Override
    public List<CustomerProfile> getAllCustomerProfiles() {
        LOG.debug("getAllCustomerProfiles() started");

        Map<String, Object> criteria = new HashMap<>();
        criteria.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        List<CustomerProfile> customerProfileList = (List<CustomerProfile>) businessObjectService.findMatching(CustomerProfile.class, criteria);

        DynamicCollectionComparator.sort(customerProfileList, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE);

        return customerProfileList;
    }

    @Override
    public List<DisbursementNumberRange> getAllDisbursementNumberRanges() {
        LOG.debug("getAllDisbursementNumberRanges() started");

        Map<String, Object> criteria = new HashMap<>();
        criteria.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        List<DisbursementNumberRange> disbursementNumberRangeList = (List<DisbursementNumberRange>)businessObjectService.findMatching(DisbursementNumberRange.class, criteria);
        DynamicCollectionComparator.sort(disbursementNumberRangeList, PdpPropertyConstants.DisbursementNumberRange.DISBURSEMENT_NUMBER_RANGE_PHYS_CAMPUS_PROC_CODE, PdpPropertyConstants.DisbursementNumberRange.DISBURSEMENT_NUMBER_RANGE_TYPE_CODE);

        return disbursementNumberRangeList;
    }

    /**
     * Given the List of disbursement number ranges for the processing campus, finds matches for the bank code and disbursement type
     * code. If more than one match is found, the range with the latest start date (before or equal to today) will be returned.
     *
     * @param ranges               List of disbursement ranges to search (already filtered to processing campus, active, and start date before or
     *                             equal to today)
     * @param bank                 bank code to find range for
     * @param disbursementTypeCode disbursement type code to find range for
     * @return found <code>DisbursementNumberRange</code or null if one was not found
     */
    protected DisbursementNumberRange getRange(List<DisbursementNumberRange> ranges, Bank bank, String disbursementTypeCode) {
        LOG.debug("getRange() Looking for bank = " + bank.getBankCode() + " and disbursement type " + disbursementTypeCode);

        List<DisbursementNumberRange> rangeMatches = new ArrayList<>();
        for (DisbursementNumberRange range : ranges) {
            if (range.getBank().getBankCode().equals(bank.getBankCode()) && range.getDisbursementTypeCode().equals(disbursementTypeCode)) {
                rangeMatches.add(range);
            }
        }

        // if more than one match we need to take the range with the latest start date
        if (rangeMatches.size() > 0) {
            DisbursementNumberRange maxStartDateRange = rangeMatches.get(0);
            for (DisbursementNumberRange range : rangeMatches) {
                if (range.getDisbNbrRangeStartDt().compareTo(maxStartDateRange.getDisbNbrRangeStartDt()) > 0) {
                    maxStartDateRange = range;
                }
            }

            return maxStartDateRange;
        }

        return null;
    }

    protected void sendFailureEmail(String toAddress, int processId) {
        LOG.debug("sendFailureEmail() started");

        BodyMailMessage message = new BodyMailMessage();
        message.setFromAddress(parameterService.getParameterValueAsString(KFSConstants.CoreModuleNamespaces.PDP, KfsParameterConstants.BATCH_COMPONENT, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM));
        message.setSubject("PDP Format Failed for Process ID " + processId);
        if (StringUtils.isBlank(toAddress)) {
            LOG.warn("sendFailureEmail() Reverting to using default to address: "+ emailService.getDefaultToAddress());
            toAddress = emailService.getDefaultToAddress();
        }
        message.setToAddresses(new HashSet());
        message.addToAddress(toAddress);
        StringBuilder msg = new StringBuilder("The PDP format for Process ID " + processId + " has failed. It returned following errors.\n\n");
        List<String> errList = GlobalVariablesUtils.extractGlobalVariableErrors();
        for (String err : errList) {
            msg.append(err + "\n");
        }
        message.setMessage(msg.toString());

        LOG.info("sendFailureEmail)_ Sending failure email to " + toAddress);
        emailService.sendMessage(message,false);
    }

    /**
     * This class holds disbursement number and noteLines info for payment group disbursement number assignment and combine checks.
     */
    protected class PaymentInfo {
        public KualiInteger disbursementNumber;
        public KualiInteger noteLines;
        public PaymentInfo(KualiInteger disbursementNumber, KualiInteger noteLines) {
            this.disbursementNumber = disbursementNumber;
            this.noteLines = noteLines;
        }
    }

    public void setFormatPaymentDao(FormatPaymentDao fpd) {
        formatPaymentDao = fpd;
    }

    public void setGlPendingTransactionService(PendingTransactionService gs) {
        glPendingTransactionService = gs;
    }

    public void setAchService(AchService as) {
        achService = as;
    }

    public void setProcessDao(ProcessDao pd) {
        processDao = pd;
    }

    public void setPaymentGroupDao(PaymentGroupDao pgd) {
        paymentGroupDao = pgd;
    }

    public void setPaymentDetailDao(PaymentDetailDao pdd) {
        paymentDetailDao = pdd;
    }

    public void setSchedulerService(SchedulerService ss) {
        schedulerService = ss;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService bos) {
        this.businessObjectService = bos;
    }

    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setExtractPaymentService(ExtractPaymentService extractPaymentService) {
        this.extractPaymentService = extractPaymentService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setEmailTemplateUrl(String emailTemplateUrl) {
        this.emailTemplateUrl = emailTemplateUrl;
    }
}
