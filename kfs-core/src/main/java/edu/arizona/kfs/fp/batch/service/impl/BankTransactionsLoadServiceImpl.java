package edu.arizona.kfs.fp.batch.service.impl;

import edu.arizona.kfs.fp.batch.dataaccess.impl.CsvBankTransactionsInputFileType;
import edu.arizona.kfs.fp.batch.dataaccess.impl.CsvBankTransactionsValidatedFileType;
import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.fp.batch.service.BankTransactionsLoadService;
import edu.arizona.kfs.fp.batch.service.TransactionPostingService;
import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.fp.businessobject.BankTransactionsFileInfo;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.businessobject.BatchFileUploads;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This service provides methods to load and validate Bank Transaction batch files (so-called tfiles) for the Document Creation batch job.
 * <p>
 * Created by nataliac on 3/15/17.
 */
public class BankTransactionsLoadServiceImpl implements BankTransactionsLoadService {
    private static final Logger LOG = Logger.getLogger(BankTransactionsLoadServiceImpl.class);

    protected CsvBankTransactionsInputFileType batchInputFileType;
    protected CsvBankTransactionsValidatedFileType bankTransactionsValidatedFileType;

    BatchInputFileService batchInputFileService;
    BankParametersAccessService bankParametersAccessService;
    BusinessObjectService businessObjectService;
    TransactionPostingService transactionPostingService;

    /**
     * Validates and parses all tfiles received from the banks in the batch staging area.
     * If there are any errors, they are written to Tfile_error.report.
     *
     * @return True if there are any valid files with no errors, false otherwise.
     */
    public boolean validateBankTransactionFiles() {
        LOG.info("Beginning validations of all available bank input transaction files for Document Creation Job.");
        boolean result = true;

        // create a list of the files to validate
        List<String> filesToValidate = getBatchInputFileService().listInputFileNamesWithDoneFile(getBatchInputFileType());

        // end this step with a false if no files are found
        if (filesToValidate == null || filesToValidate.isEmpty()) {
            LOG.info("End validateBankTransactionFiles() with FALSE since no files were found!");
            return false;
        }

        // make sure Bank Parameters are up to date
        getBankParametersAccessService().clearCached();

        for (String filePath : filesToValidate) {
            LOG.info("validateBankTransactionFiles() Beginning validation of file: " + filePath);
            try {
                CsvBankTransactionsInputFileType inputFile = getBatchInputFileType();
                inputFile.setFileToProcess(filePath);
                inputFile.deleteDoneFile(filePath);
                boolean isFileValid = inputFile.validate( );
                if (!isFileValid) {
                    LOG.error("Errors occurred for " + filePath + "File will not be processed. Please see error report:" + inputFile.getErrorFilePath());
                    result = false;
                } else {
                    inputFile.createValidatedFiles();
                }

            } catch (RuntimeException e) {
                //we swallow the exception so we can continue processing files, the errors should have been reported in the error log
                LOG.error("Caught exception trying to load bank file: " + filePath, e);
                result = false;
            }
        }


        LOG.info("End validateBankTransactionFiles() with result=" + result);
        return result;
    }


    /**
     * Consolidates all the data across multiple bank transaction files in one file, skipping the first row of
     * each file (which should be the validated batchTotal for all the transactions in the file)
     *
     * If there are no files validated available for processing, returns false so next in documentCreationJob step won't execute.
     *
     * @return True if the file consolidation was successful, false otherwise.
     */
    public boolean consolidateBankTransactionFiles() {
        LOG.info("Beginning consolidation of all available bank input transaction files for Document Creation Job.");

        // create a list of the files to process
        List<String> filesToProcess = getBatchInputFileService().listInputFileNamesWithDoneFile(getBankTransactionsValidatedFileType());

        // end this step with a false if no files are found
        if (filesToProcess == null || filesToProcess.isEmpty()) {
            LOG.info("End consolidateBankTransactionFiles() with FALSE since no files were found!");
            return false;
        }

        try {
            getBankTransactionsValidatedFileType().consolidateFiles( filesToProcess );
        }
        catch (Exception e) {
            LOG.error("ERROR in method consolidateBankTransactionFiles. DocumentCreationJob will not continue!",e);
            // instead of rethrowing the exception, try to end in a civilized way returning false so batch won't continue...
            return false;
        }

        LOG.info("Finished consolidateBankTransactionFiles()");
        return true;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankTransactionsLoadService.postTransactionsFromBankFile()
     * @return
     */
    @Transactional
    public boolean postTransactionsFromBankFile(){
        LOG.info("Running postTransactionsFromBankFile in BankTransactionsLoadService");
        CsvBankTransactionsValidatedFileType loadFile = getBankTransactionsValidatedFileType();
        List<String> errorList = new ArrayList<>();
        try {

            // open the validated bankTransactionsFile for processing
            loadFile.openTransactionsFileForProcessing();
            BankTransaction bankTransaction = null;
            while ((bankTransaction = loadFile.readNextBankTransaction(errorList)) != null && errorList.isEmpty()) {
                //post the bank transaction...using TransactionPosting service.
                errorList = getTransactionPostingService().postTransaction(bankTransaction);
            }

            if (!errorList.isEmpty()){
                //if there are any errors, stop processing immediately and log errors
                LOG.error("Error while posting bank transaction at line: "+loadFile.getCurrentLine()+" DocumentCreationJob will not continue.");
                throw new RuntimeException("Error while posting bank transaction at line: "+loadFile.getCurrentLine()+" DocumentCreationJob will not continue.");
            }

            //record processed file to avoid duplicate file uploads
            recordFileUpload(loadFile.getBankFileInfo());

            //create .done file for check recon if needed and reset checkReconFilename Timestamp.
            getTransactionPostingService().finalizeCheckRecon();

            // rename the transaction load file in load directory
            loadFile.renameProcessedFile();
        }catch (Exception e){
            LOG.error("ERROR in method postTransactionsFromBankFile. DocumentCreationJob will not continue!",e);
            throw e;
        } finally {
            loadFile.logErrorsToFile(errorList);
            loadFile.closeOpenedResources();

        }

        LOG.info("Completed postTransactionsFromBankFile in BankTransactionsLoadService");
        return true;
    }


    /**
     * Validate the given BankTransaction against all rules
     *
     * @return list of errors, if any found.
     */
    public List<String> validateBankTransaction(BankTransaction bankTransaction) {
        List<String> errorList = new ArrayList<>();
        if (!hasValidBaiCode(bankTransaction)) {
            errorList.add("ERROR: BankTransaction: " + bankTransaction.toString() + " has an INVALID Bai Code!!!");
        }
        return errorList;
    }

    /**
     * Validate the given BankTransactionsFileInfo that it's not a duplicate upload, the batch total should match the computed total
     * <p>
     *
     * @return list of errors, if any found.
     */
    public List<String> validateBankTransactionsFileInfo(BankTransactionsFileInfo fileInfo) {
        List<String> errorList = new ArrayList<>();
        if (isDuplicateFileUpload(fileInfo)) {
            errorList.add("ERROR: There was a bankTransactionsFile that was already uploaded for this day " + fileInfo.getPostingDate() + " .  " + fileInfo.getFileName() + " will not be processed.");
            LOG.error("ERROR: There was a bankTransactionsFile that was already uploaded for this day  " + fileInfo.getPostingDate() + " . " + fileInfo.getFileName() + " will not be processed.");
        }
        if (!fileInfo.getBatchTotal().equals(fileInfo.getTransactionsTotal())) {
            errorList.add("ERROR: Batch total does not match computed transaction total :" + fileInfo.toString() + " File will not be processed.");
            LOG.error("ERROR: Batch total does not match computed transaction total :" + fileInfo.toString() + " File will not be processed.");
        }
        return errorList;
    }


    /**
     * Check that a BankTransaction has the Bai Code in the allowed list of Bai Code
     *
     * @return true - if transaction has a valid Bai Code
     */
    protected boolean hasValidBaiCode(BankTransaction bankTransaction) {
        Set<Integer> allowedBaiSet = getBankParametersAccessService().getAllowedBaiTypes();
        //convert baiType from Long to Integer before comparison, otherwise it will fail
        Integer transactionBai = bankTransaction.getBaiType().intValue();
        if (allowedBaiSet.contains(transactionBai)) {
            return true;
        }
        LOG.debug("ERROR: BankTransaction: " + bankTransaction.toString() + " has an INVALID Bai Code!!! Allowed BAIs:" + allowedBaiSet.toString());
        return false;
    }


    /**
     * Returns TRUE if any files were already processed for the posting date of this file
     */
    public boolean isDuplicateFileUpload(BankTransactionsFileInfo fileInfo) {
        Timestamp timestampBatchDate = new Timestamp(fileInfo.getPostingDate().getTime());
        return (getTransactionPostingService().isDuplicateBatch(KFSConstants.BankTransactionConstants.TFILE_NAME, timestampBatchDate, KFSConstants.BankTransactionConstants.BANK_TRANSACTIONS_LOAD_BATCH_JOB_NAME));
    }


    /**
     * Records the date for the validated bank transaction file to the db to avoid duplicate uploads.
     */
    public void recordFileUpload(BankTransactionsFileInfo fileInfo) {
        Date today = new Date();
        BatchFileUploads batchFileUploads = new BatchFileUploads();
        batchFileUploads.setFileProcessTimestamp(new Timestamp(today.getTime()));
        batchFileUploads.setBatchFileName(KFSConstants.BankTransactionConstants.TFILE_NAME);
        batchFileUploads.setBatchDate(new Timestamp(fileInfo.getPostingDate().getTime()));
        batchFileUploads.setTransactionCount(new KualiInteger(fileInfo.getTransactionCount()));
        batchFileUploads.setBatchTotalAmount(fileInfo.getBatchTotal());
        batchFileUploads.setSubmiterUserId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        batchFileUploads.setBatchName(KFSConstants.BankTransactionConstants.BANK_TRANSACTIONS_LOAD_BATCH_JOB_NAME);
        getBusinessObjectService().save(batchFileUploads);
    }



    public BatchInputFileService getBatchInputFileService() {
        return batchInputFileService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public BankParametersAccessService getBankParametersAccessService() {
        return bankParametersAccessService;
    }

    public void setBankParametersAccessService(BankParametersAccessService bankParametersAccessService) {
        this.bankParametersAccessService = bankParametersAccessService;
    }

    public CsvBankTransactionsInputFileType getBatchInputFileType() {
        return batchInputFileType;
    }

    public void setBatchInputFileType(CsvBankTransactionsInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }

    public CsvBankTransactionsValidatedFileType getBankTransactionsValidatedFileType() {
        return bankTransactionsValidatedFileType;
    }

    public void setBankTransactionsValidatedFileType(CsvBankTransactionsValidatedFileType bankTransactionsValidatedFileType) {
        this.bankTransactionsValidatedFileType = bankTransactionsValidatedFileType;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    public TransactionPostingService getTransactionPostingService() {
        return transactionPostingService;
    }

    public void setTransactionPostingService(TransactionPostingService transactionPostingService) {
        this.transactionPostingService = transactionPostingService;
    }
}
