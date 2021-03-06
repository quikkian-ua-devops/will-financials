--
-- The Kuali Financial System, a comprehensive financial management system for higher education.
--
-- Copyright 2005-2017 Kuali, Inc.
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

/* Oracle */
alter table TEM_ACCT_LINES_T drop constraint TEM_ACCT_LINES_TR1;
alter table TEM_ACCT_LINES_T drop constraint TEM_ACCT_LINES_TR2;
alter table TEM_ACCT_LINES_T drop constraint TEM_ACCT_LINES_TR3;
alter table TEM_ACCT_LINES_T drop constraint TEM_ACCT_LINES_TR7;
alter table TEM_ACCT_LINES_T drop constraint TEM_ACCT_LINES_TR9;
alter table TEM_CREDIT_CARD_AGENCY_T drop constraint TEM_CREDIT_CARD_AGENCY_TR1;
alter table TEM_CREDIT_CARD_AGENCY_T drop constraint TEM_CREDIT_CARD_AGENCY_TR2;
alter table TEM_CREDIT_CARD_AGENCY_T drop constraint TEM_CREDIT_CARD_AGENCY_TR3;
alter table TEM_CREDIT_CARD_STAGING_T drop constraint TEM_CREDIT_CARD_STAGING_TR1;
alter table TEM_EM_CONT_T drop constraint TEM_EM_CONT_TR1;
alter table TEM_EM_CONT_T drop constraint TEM_EM_CONT_TR2;
alter table TEM_ENT_DOC_T drop constraint TEM_ENT_DOC_TR1;
alter table TEM_ENT_DOC_T drop constraint TEM_ENT_DOC_TR3;
alter table TEM_ENT_DOC_T drop constraint TEM_ENT_DOC_TR4;
alter table TEM_ENT_DOC_T drop constraint TEM_ENT_DOC_TR5;
alter table TEM_EXP_TYP_FIN_OBJ_CD_T drop constraint TEM_EXP_TYP_FIN_OBJ_CD_TR1;
alter table TEM_GRP_TRVLR_T drop constraint TEM_GRP_TRVLR_TR1;
alter table TEM_HIST_EXP_T drop constraint TEM_HIST_EXP_TR1;
alter table TEM_MILEAGE_RT_T drop constraint TEM_MILEAGE_RT_TR1;
alter table TEM_PER_DIEM_EXP_T drop constraint TEM_PER_DIEM_EXP_TR1;
alter table TEM_PER_DIEM_EXP_T drop constraint TEM_PER_DIEM_EXP_TR2;
alter table TEM_PER_DIEM_EXP_T drop constraint TEM_PER_DIEM_EXP_TR3;
alter table TEM_PROFILE_ACCOUNT_T drop constraint TEM_PROFILE_ACCOUNT_TR1;
alter table TEM_PROFILE_ARRANGER_T drop constraint TEM_PROFILE_ARRANGER_TR1;
alter table TEM_PROFILE_EM_CONT_T drop constraint TEM_PROFILE_EM_CONT_TR1;
alter table TEM_PROFILE_T drop constraint TEM_PROFILE_TR1;
alter table TEM_RELO_DOC_T drop constraint TEM_RELO_DOC_TR1;
alter table TEM_RELO_DOC_T drop constraint TEM_RELO_DOC_TR3;
alter table TEM_RELO_DOC_T drop constraint TEM_RELO_DOC_TR4;
alter table TEM_RELO_DOC_T drop constraint TEM_RELO_DOC_TR5;
alter table TEM_RELO_DOC_T drop constraint TEM_RELO_DOC_TR6;
alter table TEM_SPCL_CRCMSNCS_T drop constraint TEM_SPCL_CRCMSNCS_TR1;
alter table TEM_TRANS_MD_DTL_T drop constraint TEM_TRANS_MD_DTL_TR1;
alter table TEM_TRAVELER_DTL_T drop constraint TEM_TRAVELER_DTL_TR1;
alter table TEM_TRAVELER_DTL_T drop constraint TEM_TRAVELER_DTL_TR2;
alter table TEM_TRIP_TYP_T drop constraint TEM_TRIP_TYP_TR1;
alter table TEM_TRVL_AUTH_AMEND_DOC_T drop constraint TEM_TRVL_AUTH_AMEND_DOC_TR1;
alter table TEM_TRVL_AUTH_AMEND_DOC_T drop constraint TEM_TRVL_AUTH_AMEND_DOC_TR2;
alter table TEM_TRVL_AUTH_AMEND_DOC_T drop constraint TEM_TRVL_AUTH_AMEND_DOC_TR3;
alter table TEM_TRVL_AUTH_CLOSE_DOC_T drop constraint TEM_TRVL_AUTH_CLOSE_DOC_TR1;
alter table TEM_TRVL_AUTH_CLOSE_DOC_T drop constraint TEM_TRVL_AUTH_CLOSE_DOC_TR2;
alter table TEM_TRVL_AUTH_CLOSE_DOC_T drop constraint TEM_TRVL_AUTH_CLOSE_DOC_TR3;
alter table TEM_TRVL_AUTH_DOC_T drop constraint TEM_TRVL_AUTH_DOC_TR1;
alter table TEM_TRVL_AUTH_DOC_T drop constraint TEM_TRVL_AUTH_DOC_TR2;
alter table TEM_TRVL_AUTH_DOC_T drop constraint TEM_TRVL_AUTH_DOC_TR3;
alter table TEM_TRVL_AUTH_DOC_T drop constraint TEM_TRVL_AUTH_DOC_TR4;
alter table TEM_TRVL_EXP_T drop constraint TEM_TRVL_EXP_TR1;
alter table TEM_TRVL_EXP_T drop constraint TEM_TRVL_EXP_TR4;
alter table TEM_TRVL_PMT_T drop constraint TEM_TRVL_PMT_TR1;
alter table TEM_TRVL_REIMB_DOC_T drop constraint TEM_TRVL_REIMB_DOC_TR1;
alter table TEM_TRVL_REIMB_DOC_T drop constraint TEM_TRVL_REIMB_DOC_TR2;
alter table TEM_TRVL_REIMB_DOC_T drop constraint TEM_TRVL_REIMB_DOC_TR3;
alter table TEM_TRVL_REIMB_DOC_T drop constraint TEM_TRVL_REIMB_DOC_TR4;

/* MySql */
alter table TEM_ACCT_LINES_T drop foreign key TEM_ACCT_LINES_TR1;
alter table TEM_ACCT_LINES_T drop foreign key TEM_ACCT_LINES_TR2;
alter table TEM_ACCT_LINES_T drop foreign key TEM_ACCT_LINES_TR3;
alter table TEM_ACCT_LINES_T drop foreign key TEM_ACCT_LINES_TR7;
alter table TEM_ACCT_LINES_T drop foreign key TEM_ACCT_LINES_TR9;
alter table TEM_CREDIT_CARD_AGENCY_T drop foreign key TEM_CREDIT_CARD_AGENCY_TR1;
alter table TEM_CREDIT_CARD_AGENCY_T drop foreign key TEM_CREDIT_CARD_AGENCY_TR2;
alter table TEM_CREDIT_CARD_AGENCY_T drop foreign key TEM_CREDIT_CARD_AGENCY_TR3;
alter table TEM_CREDIT_CARD_STAGING_T drop foreign key TEM_CREDIT_CARD_STAGING_TR1;
alter table TEM_EM_CONT_T drop foreign key TEM_EM_CONT_TR1;
alter table TEM_EM_CONT_T drop foreign key TEM_EM_CONT_TR2;
alter table TEM_ENT_DOC_T drop foreign key TEM_ENT_DOC_TR1;
alter table TEM_ENT_DOC_T drop foreign key TEM_ENT_DOC_TR3;
alter table TEM_ENT_DOC_T drop foreign key TEM_ENT_DOC_TR4;
alter table TEM_ENT_DOC_T drop foreign key TEM_ENT_DOC_TR5;
alter table TEM_EXP_TYP_FIN_OBJ_CD_T drop foreign key TEM_EXP_TYP_FIN_OBJ_CD_TR1;
alter table TEM_GRP_TRVLR_T drop foreign key TEM_GRP_TRVLR_TR1;
alter table TEM_HIST_EXP_T drop foreign key TEM_HIST_EXP_TR1;
alter table TEM_MILEAGE_RT_T drop foreign key TEM_MILEAGE_RT_TR1;
alter table TEM_PER_DIEM_EXP_T drop foreign key TEM_PER_DIEM_EXP_TR1;
alter table TEM_PER_DIEM_EXP_T drop foreign key TEM_PER_DIEM_EXP_TR2;
alter table TEM_PER_DIEM_EXP_T drop foreign key TEM_PER_DIEM_EXP_TR3;
alter table TEM_PROFILE_ACCOUNT_T drop foreign key TEM_PROFILE_ACCOUNT_TR1;
alter table TEM_PROFILE_ARRANGER_T drop foreign key TEM_PROFILE_ARRANGER_TR1;
alter table TEM_PROFILE_EM_CONT_T drop foreign key TEM_PROFILE_EM_CONT_TR1;
alter table TEM_PROFILE_T drop foreign key TEM_PROFILE_TR1;
alter table TEM_RELO_DOC_T drop foreign key TEM_RELO_DOC_TR1;
alter table TEM_RELO_DOC_T drop foreign key TEM_RELO_DOC_TR3;
alter table TEM_RELO_DOC_T drop foreign key TEM_RELO_DOC_TR4;
alter table TEM_RELO_DOC_T drop foreign key TEM_RELO_DOC_TR5;
alter table TEM_RELO_DOC_T drop foreign key TEM_RELO_DOC_TR6;
alter table TEM_SPCL_CRCMSNCS_T drop foreign key TEM_SPCL_CRCMSNCS_TR1;
alter table TEM_TRANS_MD_DTL_T drop foreign key TEM_TRANS_MD_DTL_TR1;
alter table TEM_TRAVELER_DTL_T drop foreign key TEM_TRAVELER_DTL_TR1;
alter table TEM_TRAVELER_DTL_T drop foreign key TEM_TRAVELER_DTL_TR2;
alter table TEM_TRIP_TYP_T drop foreign key TEM_TRIP_TYP_TR1;
alter table TEM_TRVL_AUTH_AMEND_DOC_T drop foreign key TEM_TRVL_AUTH_AMEND_DOC_TR1;
alter table TEM_TRVL_AUTH_AMEND_DOC_T drop foreign key TEM_TRVL_AUTH_AMEND_DOC_TR2;
alter table TEM_TRVL_AUTH_AMEND_DOC_T drop foreign key TEM_TRVL_AUTH_AMEND_DOC_TR3;
alter table TEM_TRVL_AUTH_CLOSE_DOC_T drop foreign key TEM_TRVL_AUTH_CLOSE_DOC_TR1;
alter table TEM_TRVL_AUTH_CLOSE_DOC_T drop foreign key TEM_TRVL_AUTH_CLOSE_DOC_TR2;
alter table TEM_TRVL_AUTH_CLOSE_DOC_T drop foreign key TEM_TRVL_AUTH_CLOSE_DOC_TR3;
alter table TEM_TRVL_AUTH_DOC_T drop foreign key TEM_TRVL_AUTH_DOC_TR1;
alter table TEM_TRVL_AUTH_DOC_T drop foreign key TEM_TRVL_AUTH_DOC_TR2;
alter table TEM_TRVL_AUTH_DOC_T drop foreign key TEM_TRVL_AUTH_DOC_TR3;
alter table TEM_TRVL_AUTH_DOC_T drop foreign key TEM_TRVL_AUTH_DOC_TR4;
alter table TEM_TRVL_EXP_T drop foreign key TEM_TRVL_EXP_TR1;
alter table TEM_TRVL_EXP_T drop foreign key TEM_TRVL_EXP_TR4;
alter table TEM_TRVL_PMT_T drop foreign key TEM_TRVL_PMT_TR1;
alter table TEM_TRVL_REIMB_DOC_T drop foreign key TEM_TRVL_REIMB_DOC_TR1;
alter table TEM_TRVL_REIMB_DOC_T drop foreign key TEM_TRVL_REIMB_DOC_TR2;
alter table TEM_TRVL_REIMB_DOC_T drop foreign key TEM_TRVL_REIMB_DOC_TR3;
alter table TEM_TRVL_REIMB_DOC_T drop foreign key TEM_TRVL_REIMB_DOC_TR4;


/* Mysql and Oracle */
drop table TEM_ACCOMM_TYP_T;
drop table TEM_ACCT_DOC_REL_T;
drop table TEM_ACCT_LINES_T;
drop table TEM_ADV_PMNT_RSN_T;
drop table TEM_AGENCY_SRVC_FEE_T;
drop table TEM_AGENCY_STAGING_T;
drop table TEM_AIRFARE_SRC_T;
drop table TEM_ATTENDEE_T;
drop table TEM_CC_IMP_EXP_CLR_DTL_T;
drop table TEM_CC_IMP_EXP_CLR_T;
drop table TEM_CLASS_SVC_T;
drop table TEM_CONT_REL_TYP_T;
drop table TEM_CORP_CARD_APP_DOC_T;
drop table TEM_CORP_CARD_PSEUDO_NUM_T;
drop table TEM_CREDIT_CARD_AGENCY_T;
drop table TEM_CREDIT_CARD_STAGING_T;
drop table TEM_CTS_CARD_APP_DOC_T;
drop table TEM_EM_CONT_T;
drop table TEM_ENT_DOC_T;
drop table TEM_ENT_PURPOSE_T;
drop table TEM_EXP_TYP_FIN_OBJ_CD_T;
drop table TEM_EXP_TYP_T;
drop table TEM_GRP_TRVLR_T;
drop table TEM_HELD_ENCUM_ENTRY_T;
drop table TEM_HIST_EXP_T;
drop table TEM_JOB_CLASS_T;
drop table TEM_MILEAGE_RT_T;
drop table TEM_PER_DIEM_EXP_T;
drop table TEM_PER_DIEM_MIE_BREAK_DOWN_T;
drop table TEM_PER_DIEM_REGION_T;
drop table TEM_PER_DIEM_T;
drop table TEM_PRI_DEST_T;
drop table TEM_PROFILE_ACCOUNT_T;
drop table TEM_PROFILE_ADDR_T;
drop table TEM_PROFILE_ARRANGER_T;
drop table TEM_PROFILE_EM_CONT_T;
drop table TEM_PROFILE_T;
drop table TEM_RELO_DOC_T;
drop table TEM_RELO_REASON_T;
drop table TEM_SPCL_CRCMSNCS_Q_T;
drop table TEM_SPCL_CRCMSNCS_T;
drop table TEM_TAX_RAM_DOC_T;
drop table TEM_TRANS_MD_DTL_T;
drop table TEM_TRANS_MD_T;
drop table TEM_TRAVELER_DTL_T;
drop table TEM_TRAVELER_TYP_T;
drop table TEM_TRIP_TYP_T;
drop table TEM_TRP_ACCT_INFO_T;
drop table TEM_TRVL_ADV_T;
drop table TEM_TRVL_ARRANGER_DOC_T;
drop table TEM_TRVL_AUTH_AMEND_DOC_T;
drop table TEM_TRVL_AUTH_CLOSE_DOC_T;
drop table TEM_TRVL_AUTH_DOC_T;
drop table TEM_TRVL_CARD_TYP_T;
drop table TEM_TRVL_EXP_T;
drop table TEM_TRVL_PMT_T;
drop table TEM_TRVL_REIMB_DOC_T;

/* MySql */
drop table TEM_ACCT_DOC_REL_ID_SEQ;
drop table TEM_AGENCY_STAGING_ID_SEQ;
drop table TEM_ATTENDEE_ID_SEQ;
drop table TEM_CORP_CARD_PSEUDO_NUM_SEQ;
drop table TEM_CREDIT_CARD_STAGING_ID_SEQ;
drop table TEM_EM_CONT_ID_SEQ;
drop table TEM_EXP_TYP_FIN_OJB_CD_ID_SEQ;
drop table TEM_GRP_TRVLR_ID_SEQ;
drop table TEM_HIST_EXP_ID_SEQ;
drop table TEM_MILEAGE_RT_ID_SEQ;
drop table TEM_PER_DIEM_EXP_ID_SEQ;
drop table TEM_PER_DIEM_ID_SEQ;
drop table TEM_PRI_DEST_ID_SEQ;
drop table TEM_PROFILE_ACCOUNT_ID_SEQ;
drop table TEM_PROFILE_ARRANGER_ID_SEQ;
drop table TEM_PROFILE_EM_CONT_ID_SEQ;
drop table TEM_PROFILE_ID_SEQ;
drop table TEM_SPCL_CRCMSNCS_Q_ID_SEQ;
drop table TEM_SPCL_CRCMSNCS_S;
drop table TEM_TRAVELER_DTL_ID_SEQ;
drop table TEM_TRP_ACCT_INFO_ID_SEQ;
drop table TEM_TRVL_EXP_ID_SEQ;
drop table TRVL_ID_SEQ;

/* Oracle */
drop sequence TEM_ACCT_DOC_REL_ID_SEQ;
drop sequence TEM_AGENCY_STAGING_ID_SEQ;
drop sequence TEM_ATTENDEE_ID_SEQ;
drop sequence TEM_CORP_CARD_PSEUDO_NUM_SEQ;
drop sequence TEM_CREDIT_CARD_STAGING_ID_SEQ;
drop sequence TEM_EM_CONT_ID_SEQ;
drop sequence TEM_EXP_TYP_FIN_OJB_CD_ID_SEQ;
drop sequence TEM_GRP_TRVLR_ID_SEQ;
drop sequence TEM_HIST_EXP_ID_SEQ;
drop sequence TEM_MILEAGE_RT_ID_SEQ;
drop sequence TEM_PER_DIEM_EXP_ID_SEQ;
drop sequence TEM_PER_DIEM_ID_SEQ;
drop sequence TEM_PRI_DEST_ID_SEQ;
drop sequence TEM_PROFILE_ACCOUNT_ID_SEQ;
drop sequence TEM_PROFILE_ARRANGER_ID_SEQ;
drop sequence TEM_PROFILE_EM_CONT_ID_SEQ;
drop sequence TEM_PROFILE_ID_SEQ;
drop sequence TEM_SPCL_CRCMSNCS_Q_ID_SEQ;
drop sequence TEM_SPCL_CRCMSNCS_S;
drop sequence TEM_TRAVELER_DTL_ID_SEQ;
drop sequence TEM_TRP_ACCT_INFO_ID_SEQ;
drop sequence TEM_TRVL_EXP_ID_SEQ;
drop sequence TRVL_ID_SEQ;
