package edu.arizona.kfs.sys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.api.KewApiConstants;

public class KFSConstants extends org.kuali.kfs.sys.KFSConstants {

    public static final String XML_EXTENSION = ".xml";
    public static final String MULTI_VALUE_SEPERATION_CHARACTER = ";";
    public static final String PIPE = "|";
    public static final String EQUALS = "=";
    public static final char QUOTE_CHAR = '"';
    public static final char DOT_CHAR = '.';
    public static final char COMMA_CHAR = ',';

    public static final String INVOICE_NUMBER = "Invoice Number";
    public static final String DUPLICATE_INVOICE_QUESTION_ID = "DVDuplicateInvoice";
    public static final String SYSTEM_USER_NAME = "SYSTEM_USER_NAME";

    public static final String GL_ENTRY_IMPORTING = "glEntryImporting";
    public static final String DOC_FORM_KEY_VALUE_88888888 = "88888888";
    public static final String NULL_STRING = "null";
    public static final String PATH_SEPERATOR = "/";
    public static final String BLANK_ACCOUNT = "-------";
    public static final String BLANK_SUBACCOUNT = "-----";
    public static final String BLANK_SUBOBJECT = "---";
    public static final String BLANK_PROJECT_CODE = "----------";
    public static final String STAGING_DIRECTORY_KEY = "staging.directory";
    public static final String DONE_FILE_EXTENSION = ".done";
    public static final String THREE_CHAR_MASK = "XXX";


    // Docuware Constants
    public static final String VIEW_DOCUWARE = "viewImages";
    public static final String DOCUWARE_TABLE_PARAMETER = "DOCUWARE_TABLE_PARAMETER";
    public static final String DOCUWARE_TABLE = "table";
    public static final String DOCUWARE_IDVALUE = "idvalue";
    
    //Shipping Constants   
    public static final String SHIP_FILE_TYPE_IDENTIFIER = "shippingInputFileType";
    public static final String SHIPPING_FILE_NAME = "extract_for_shipping";
    public static final String SHIPPING_INVOICE_ORIGIN_CODE = "SH";
    public static final String SHIPPING_FILENAME_DELIMITER = "_";
    
    public static final String DOCUWARE_DV_DOC_TYPE = "DV";
    public static final String DOCUWARE_PREQ_DOC_TYPE = "PREQ";
    
    //PDP Batch
    public static final String DEPRECATED_PDP_LOAD_FEDERAL_RESERVE_BANK_DATA_STEP_JOB = "This job is deprecated; not intended to be used in UA implementation.";

    public static class CreateAndUpdateNotePrefixes {
        public static final String ADD = "Add";
        public static final String CHANGE = "Change";
    }
    
    // General Error Correction Constants
    public static final String GEC_ENTRY_OBJ_ID = "OBJ_ID";
    public static final Set<String> GEC_ACTIVE_ROUTE_STATUS_CODES;
    static {
        GEC_ACTIVE_ROUTE_STATUS_CODES = new HashSet<String>();
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_INITIATED_CD);
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_PROCESSED_CD);
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_SAVED_CD);
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_FINAL_CD);
    }

    public static class SysKimApiConstants {
        public static final String ACCOUNT_SUPERVISOR_KIM_ROLE_NAME = "Account Supervisor";
        public static final String CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR = "Contracts & Grants Project Director";
        public static final String FISCAL_OFFICER_KIM_ROLE_NAME = "Fiscal Officer";
        public static final String FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME = "Fiscal Officer Primary Delegate";
        public static final String FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME = "Fiscal Officer Secondary Delegate";
        public static final String AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME = "Award Project Director";
        public static final String ACTIVE_FACULTY_OR_STAFF_KIM_ROLE_NAME = "Active Faculty or Staff";
        public static final String ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME = "Active Professional Employee";
        public static final String ACTIVE_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME = "Active Employee & Financial System User";
        public static final String ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME = "Active Professional Employee & Financial System User";
        public static final String CHART_MANAGER_KIM_ROLE_NAME = "UA Chart Manager";
        public static final String ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE = CoreModuleNamespaces.KFS;
        public static final String ACCOUNTING_REVIEWER_ROLE_NAMESPACECODE = CoreModuleNamespaces.KFS;
        public static final String ACCOUNTING_REVIEWER_ROLE_NAME = "Accounting Reviewer";
        public static final String ORGANIZATION_REVIEWER_ROLE_NAME = "Organization Reviewer";
        public static final String ACCOUNTS_RECEIVABLE_COLLECTOR = "CGB Collector";
        public static final String KFS_USER_ROLE_NAME = "User";
        public static final String CONTRACTS_AND_GRANTS_PROCESSOR = "Contracts & Grants Processor";
        public static final String SUB_FUND_REVIEWER = "Sub-Fund Reviewer";
        public static final String ORGANIZATION_FUND_REVIEWER_ROLE_NAME = "Organization Fund Reviewer";
    }

    public class RouteLevelNames {
        public static final String ACCOUNT = "Account";
        public static final String ACCOUNTING_ORGANIZATION_HIERARCHY = "AccountingOrganizationHierarchy";
        public static final String ACCOUNT_REVIEW_FULL_EDIT = "AccountFullEdit";
        public static final String PROJECT_MANAGEMENT = "ProjectManagement";
        public static final String ORGANIZATION_HIERARCHY = "OrganizationHierarchy";
        public static final String PAYMENT_METHOD = "PaymentMethod";
        public static final String ORGANIZATION_FUND_REVIEW = "OrganizationFundReview";
    }

    public static class COAConstants {
        public static final String ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE = "A";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_ONLY_TEXT = "Organization Accounting Only";
        public static final String ORG_REVIEW_ROLE_ORG_ONLY_CODE = "O";
        public static final String ORG_REVIEW_ROLE_ORG_ONLY_TEXT = "Organization Only";
        public static final String ORG_REVIEW_ROLE_ORG_FUND_ONLY_CODE = "F";
        public static final String ORG_REVIEW_ROLE_ORG_FUND_ONLY_TEXT = "Organization Fund Only";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE = "B";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_BOTH_TEXT = "Both";
        public static final String ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT = "create delegation";

        public final static String DEFAULT_CHART_METHOD = "1";
        public final static String DEFAULT_PRIMARY_DEPT_METHOD = "2";
        public final static String DEFAULT_PRIMARY_DEPT_CHART_METHOD = "3";
        public final static String DEFAULT_COA_CODE = "UA";

    }

    public static class Authorization {
        public static String PAYMENT_METHOD_EDIT_MODE = "paymentMethodEditMode";
    }

    public static class GeneralErrorCorrectionDocumentConstants {
        public static final String GENERATE_ERROR_CERTIFICATION_STMT_ID = "GenerateErrorCertStmt";
    }

    public static class ErrorCertificationConstants {
        public static final int NUM_ERROR_CERT_FIELDS = 4;
    }

    public static class GeneralErrorCorrectionEditMode {
        public static final String ERROR_CERTIFICATE_TAB_ENTRY = "errorCertTabEntry";
    }

    public static class IncomeTypeConstants {
        public static final String TAX_NAMESPACE_CODE = "KFS-TAX";
        public static final String PAYEE_MASTER_EXTRACT_STEP = "PayeeMasterExtractStep";

        public static final String DEFAULT_NON_REPORTABLE_BOX = "NA"; // non-reportable
        public static final String INCOME_TYPE_NON_REPORTABLE_CODE = "NR"; // non-reportable
        public static final String PAYMENT_METHOD_AP_CREDIT_CARD = "C"; // non-reportable

        public static final String PARAMETER_1099_OBJECT_CODES_OVERRIDING_RESTRICTIONS = "1099_OBJECT_CODES_OVERRIDING_RESTRICTIONS";
        public static final String PARAMETER_1099_EXTRACT_OBJECT_LEVELS = "1099_EXTRACT_OBJECT_LEVELS";
        public static final String PARAMETER_1099_EXTRACT_OVERRIDE_PAYMENT_TYPE_CODE = "1099_EXTRACT_OVERRIDE_PMT_TYPE_CODE";
        public static final String PARAMETER_1099_OBJECT_CODES = "1099_OBJECT_CODES";

        public static class IncomeTypesAuthorization {
            public static String VIEW_INCOME_TYPES_EDIT_MODE = "viewIncomeTypes";
            public static String EDIT_INCOME_TYPES_EDIT_MODE = "editIncomeTypes";
        }

        public static class IncomeTypeAmountCodes {
            public static final String AMOUNT_CODE_A = "A"; // Crop Insurance
            public static final String AMOUNT_CODE_B = "B"; // Golden Parachute
            public static final String AMOUNT_CODE_C = "C"; // Legal Services
            public static final String AMOUNT_CODE_D = "D"; // Sec. 409A Deferrals
            public static final String AMOUNT_CODE_E = "E"; // Sec. 409A Inc
            public static final String AMOUNT_CODE_1 = "1"; // Rents
            public static final String AMOUNT_CODE_2 = "2"; // Royalties
            public static final String AMOUNT_CODE_3 = "3"; // Other Income
            public static final String AMOUNT_CODE_4 = "4"; // Fed. Income Tax Witheld
            public static final String AMOUNT_CODE_5 = "5"; // Fishing
            public static final String AMOUNT_CODE_6 = "6"; // Medical and Health Care
            public static final String AMOUNT_CODE_7 = "7"; // Nonemployee Compensation
            public static final String AMOUNT_CODE_8 = "8"; // Dividends
            public static List<String> VALID_AMOUNT_CODES = new ArrayList<String>();

            static {
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_A);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_B);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_C);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_D);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_E);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_1);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_2);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_3);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_4);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_5);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_6);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_7);
                VALID_AMOUNT_CODES.add(AMOUNT_CODE_8);
            }
        }
    }
    
    public static class PaymentSourceConstants {
        public static final String PAYMENT_METHOD_CHECK = "A";
    }
    
	public static final class ACHBankOfficeCodes {
		public static String MAIN = "O";
		public static String BRANCH = "B";
	}

	public static final class ACHBankTypeCodes {
		public static String FEDERAL_RESERVE = "0";
		public static String CUSTOMER_ROUTING_NBR = "1";
		public static String NEW_CUSTOMER_ROUTING_NBR = "2";
	}

	public static final class ACHFileConstants {
		public static final String ACH_BANK_FILE_TYPE_IDENTIFIER = "achBankInputFileType";
		public static final String ACH_BANK_LOADED_SUCCESSFULLY = "Bank record successfully loaded";
		public static final String ACH_BANK_INVALID_OFFICE_CD_ERROR = "Invalid office code - default value O used";
		public static final String ACH_BANK_INVALID_TYP_CD_ERROR = "Invalid record type code - default value 0 used";
		public static final String ACH_BANK_TYP_CD_ROUTING_NBR_ERROR = "New routing number not provided for record type code 2 - record not loaded";
		public static final String ACH_BANK_MISSING_NM_ERROR = "Bank name not provided - record not loaded";
		public static final String ACH_BANK_INCOMPLETE_ADDR_ERROR = "Incomplete bank address - record not loaded";
		public static final String ACH_BANK_INCOMPLETE_PHONE_ERROR = "Incomplete phone number - default value with zeros used";
		public static final String ACH_BANK_INVALID_INST_STAT_CD = "Invalid institution status code - default value 1 used";
		public static final String ACH_BANK_INVALID_DATA_VIEW_CD = "Invalid data view code - default value 1 used";

		public static final String ACH_PAYEE_ACCT_FILE_TYPE_IDENTIFIER = "achPayeeBankAcctInputFileType";
		public static final String ACH_PAYEE_ACCT_INVALID_ROUTING = "Bank routing number doesn't exist - record not loaded";
		public static final String ACH_PAYEE_ACCT_EMP_LOADED_SUCCESSFULLY = "Employee ACH Account record successfully loaded";
		public static final String ACH_PAYEE_ACCT_ENT_LOADED_SUCCESSFULLY = "Entity ACH Account record successfully loaded";
		public static final String ACH_PAYEE_ACCT_LOADED_SUCCESSFULLY = "ACH Account record successfully loaded";
		public static final String ACH_PAYEE_ACCT_EMP_UPDATED_SUCCESSFULLY = "Employee ACH Account recorded successfully updated";
		public static final String ACH_PAYEE_ACCT_ENT_UPDATED_SUCCESSFULLY = "Entity ACH Account recorded successfully updated";
		public static final String ACH_PAYEE_ACCT_UPDATED_SUCCESSFULLY = "ACH Account recorded successfully updated";
		public static final String ACH_PAYEE_ACCT_MISSING_PAYEE_ID_NBR_ERROR = "Payee ID number not provided - record not loaded";
		public static final String ACH_PAYEE_ACCT_MISSING_EMAIL = "Payee email address not provided - record not loaded";
		public static final String ACH_PAYEE_ACCT_MISSING_ROUTING_ERROR = "Routing number not provided";
		public static final String ACH_PAYEE_ACCT_MISSING_ACCT_NBR_ERROR = "Account number not provided";
		public static final String ACH_PAYEE_ACCT_MISSING_PAYEE_NM_ERROR = "Payee name not provided - record not loaded";
		public static final String ACH_PAYEE_ACCT_INVALID_PAYEE_ID_TYP_ERROR = "Invalid payee ID type";
		public static final String ACH_PAYEE_ACCT_MISSING_BANK_ACCT_TYP_ERROR = "Bank account type not provided - record not loaded";
		public static final String ACH_PAYEE_ACCT_INVALID_BANK_ACCT_TYP_ERROR = "Invalid bank account type code - record not loaded";
		public static final String ACH_PAYEE_ACCT_OVERRIDE_EMPL_ERROR = "Employee exists in override group - no update occurred";
		public static final String ACH_PAYEE_ACCT_NO_NAME_MATCH = "No matching payee for payee ID/type found - record not loaded";
		public static final String ACH_PAYEE_ACCT_INVALID_EXISTING_MATCH = "Invalid payee ID/type found, but record exists in ACH setup - no updates occurred";

	}

	public static final class ACHFileDefaultConstants {
		public static final String DEFAULT_BANK_NEW_ROUTING_NUMBER = "000000000";
		public static final String DEFAULT_BANK_PHONE_AREA_CODE = "000";
		public static final String DEFAULT_BANK_PHONE_PREFIX_NUMBER = "000";
		public static final String DEFAULT_BANK_PHONE_SUFFIX_NUMBER = "0000";
		public static final String DEFAULT_BANK_INSTITUTION_STATUS_CODE = "1";
		public static final String DEFAULT_BANK_DATA_VIEW_CODE = "1";
		
		public static final String DEFAULT_ACH_TRANSACTION_TYPE = "ACH";
	}

	public static final class ACHAcctTypeCodes {
		public static String CHECKING = "22";
		public static String SAVINGS = "32";
	}

    public static class ProcurementCardholder {
        public static final String PCDH_FILE_TYPE_IDENTIFIER = "procurementCardHolderInputFileType";
    }

    public static class BankTransactionsParameters {
        public static final String BANK_TFILE_DELIMITER = "BANK_TFILE_DELIMITER";
        public static final String BANK_TFILE_FIELD_POSITION = "BANK_TFILE_FIELD_POSITION";
        public static final String BANK_PARAMETER_DELIM = ";";
        public static final String ELECTRONIC_FUNDS_ACCOUNTS = "ELECTRONIC_FUNDS_ACCOUNTS";
        public static final String DEFAULT_BANK_BY_DOCUMENT_TYPE = "DEFAULT_BANK_BY_DOCUMENT_TYPE";
        public static final String CHECK_RECONCILIATION_BAI_TYPE_CODES ="CHECK_RECONCILIATION_BAI_TYPE_CODES";
        public static final String EXCLUDE_BAI_TYPE_CODES = "EXCLUDE_BAI_TYPE_CODES";
        public static final String EXCLUDE_BY_BAI_TYPE_AND_ACCOUNT = "EXCLUDE_BY_BAI_TYPE_AND_ACCOUNT";
        public static final String ACCOUNT_AND_OBJECT_CODE_BY_BAI_AND_BANK_ACCOUNT = "ACCOUNT_AND_OBJECT_CODE_BY_BAI_AND_BANK_ACCOUNT";
        public static final String DOCUMENT_TYPE_BY_BAI_TYPE_CODE = "DOCUMENT_TYPE_BY_BAI_TYPE_CODE";
        public static final String DEBIT_CREDIT_BY_BAI_TYPE_CODE = "DEBIT_CREDIT_BY_BAI_TYPE_CODE";
        public static final String DEFAULT_BANK = "DEFAULT_BANK";
        public static final String ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION = "ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION";
        public static final String DEFAULT_AD_OBJECT_CODE = "DEFAULT_AD_OBJECT_CODE";
        public static final String BOFA_NOTES_INDICATOR = "BOFA_NOTES_INDICATOR";
        public static final String BOFA_NOTES_REGEX_FORMAT = "BOFA_NOTES_REGEX_FORMAT";
        public static final String BOFA_CUST_REF_NUM_LENGTH = "BOFA_CUST_REF_NUM_LENGTH";
        public static final String BOFA_CUST_REF_NUM_REGEX_FORMAT = "BOFA_CUST_REF_NUM_REGEX_FORMAT";
        public static final String BOFA_CUST_REF_NUM_GROUP_INDEX = "BOFA_CUST_REF_NUM_GROUP_INDEX";
        public static final String BOFA_CC_ACCOUNT_NUM_SET = "BOFA_CC_ACCOUNT_NUM_SET";
        public static final String BOFA_CC_BAI_TYPE_CODE_SET = "BOFA_CC_BAI_TYPE_CODE_SET";
        public static final String BOFA_PARM_DELIMITER = "BOFA_PARAM_DELIMITER";
        public static final String VA_ACCOUNT_NUM_SET = "VA_ACCOUNT_NUM_SET";
        public static final String VA_BAI_TYPE_CODE_SET = "VA_BAI_TYPE_CODE_SET";
        public static final String VA_CUST_REF_NUM_SET = "VA_CUST_REF_NUM_SET";
        public static final String VA_NOTES_SIX_REGEX = "VA_NOTES_SIX_REGEX";
        public static final String VA_NOTES_SEVEN_REGEX = "VA_NOTES_SEVEN_REGEX";
        public static final String VA_NOTES_SEVEN_MASKING_REGEX = "VA_NOTES_SEVEN_MASKING_REGEX";
        public static final String VA_PARAM_DELIMITER = "VA_PARAM_DELIM";
        public static final String CR_STATUS_CLEARED_CODES = "CR_STATUS_CLRD_CODES";
        public static final String CR_STATUS_NAMESPACE = "KFS-CR";
        public static final String CR_STATUS_COMPONENT_CODE = "CheckReconciliationImportStep";
    }

    public static class BankTransactionFields {
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String BAI_TYPE = "baiType";
        public static final String AMOUNT = "amount";
        public static final String CUST_REF_NO = "custRefNo";
        public static final String VALUE_DATE = "valueDate";
        public static final String BANK_REFERENCE = "bankReference";
        public static final String DESC_TEXT6 = "descriptiveTxt6";
        public static final String DESC_TEXT7 = "descriptiveTxt7";
    }

    public static class BankTransactionConstants {
        public static final String BANK_TRANSACTIONS_FILE_TYPE_IDENTIFIER = "csvBankInputFileType";
        public static final String BANK_TRANSACTIONS_VALIDATED_FILE_TYPE_IDENTIFIER = "csvBankValidatedFileType";
        public static final String CHECK_RECON_FILE_TYPE_IDENTIFIER = "checkReconciliationFileType";
        public static final String TFILE_DATE_FORMAT = "yyMMdd";
        public static final String TFILE_NAME = "tfile_load";
        public static final String BANK_TRANSACTIONS_LOAD_BATCH_JOB_NAME = "documentCreationJob";
        public static final String DOCUMENT_TYPE_AD = "AD";
        public static final String DOCUMENT_TYPE_CCR = "CCR";
        public static final String DOCUMENT_TYPE_DI = "DI";
        public static final String BANK_TRANSACTION_ORIGIN_CODE = "BT";
        public static final String ACCT_LINE_DATE_FORMAT = "MMddyyyy";
        public static final int MAX_DESCRIPTION_LEN = 39;
        public static final int MAX_NUMBER_LEN = 7;
        public static final int MAX_BANK_REF_NBR = 10;
    }

    public enum BankTransactionDocumentType {
        AD("Advance Deposit"), CCR("Credit Card Receipt"), DI("Distribution of Income and Expense");

        private String description;

        BankTransactionDocumentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }

    }

}
