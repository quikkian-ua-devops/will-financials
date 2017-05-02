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

/* Financials */
DELETE FROM krcr_parm_t WHERE nmspc_cd IN ('KFS-TEM');
DELETE FROM krcr_cmpnt_t WHERE nmspc_cd IN ('KFS-TEM');
DELETE FROM krcr_nmspc_t WHERE nmspc_cd IN ('KFS-TEM');

UPDATE krcr_parm_t SET val = 'A=OEXP,NORE,FINA;B=SERV,ADV,COMP,OEXP,PHON,PRIN,R&M,RESA,S&E,TAXP,UTIL,S&S;C=SERV;E=ADV,CREX,DEBT,OEXP,R&M,SASV,SERV,SUPL;F=ACPA,AR,BENF,C&G,DEBT,GIFT,INSS,INVR,OLIA,OTHR,OTHS,TAXP;G=CAP,PHON,RESA,S&E,SASV,TAXP,UTIL;H=SERV,SASV,BENF,CREX,S&E;K=COSV,CREX,SERV;L=CAP,R&M,COSV,S&E,RESV;N=TRAV,S&E,OEXP;P=TRAV,S&E,OEXP;R=ADV,S&E,COSV;T=RENT;W=SASV,OEXP,PRIN,RESA,S&E,INV,CAP;Y=R&M;Z=TAXP,ACPA,TAX,S&E,OLIA,DEBT,CASH,CREX' WHERE parm_nm = 'VALID_OBJECT_LEVELS_BY_PAYMENT_REASON';

UPDATE krcr_parm_t SET val = 'A=V;B=V,E,VSV,VRV;C=V,E,VSP;D=V;E=V;F=V,E,VRV;G=V;H=V;K=V,VRF;L=V;M=V,E;N=V;P=V,E;R=V;T=V;W=V;X=V;Z=E' WHERE parm_nm = 'VALID_PAYEE_TYPES_BY_PAYMENT_REASON';
UPDATE fp_dv_pmt_reas_t SET dv_pmt_reas_nm = 'Rental Payments' WHERE dv_pmt_reas_cd = 'T';
UPDATE fp_dv_pmt_reas_t SET dv_pmt_reas_actv_ind = 'N' WHERE dv_pmt_reas_cd = 'V';

UPDATE ar_cust_typ_t SET row_actv_ind = 'N' WHERE cust_typ_desc = 'Traveler';

UPDATE pdp_cust_prfl_t SET actv_ind = 'N' WHERE sbunt_cd = 'TEM';

ALTER TABLE fp_dv_trvl_co_nm_t DROP COLUMN FRGN_CMPNY;
ALTER TABLE fp_dv_exp_typ_t DROP COLUMN FIN_OBJECT_CD;

DELETE FROM pdp_payee_typ_t WHERE payee_typ_cd = 'C';

/* Rice */
DELETE FROM krim_pnd_role_perm_t WHERE perm_id in ('KFS10282','KFS10321','KFS10299','KFS10285','KFS10286','KFS10544','KFS10518','KFS10738','KFS10735','KFS10721','KFS10722','KFS10723','KFS10338','KFS10340','KFS10330','KFS10733','KFS10315','KFS10332','KFS10307','KFS10310','KFS10309','KFS10311','KFS10308','KFS10296','KFS10295','KFS10298','KFS10297','KFS10290','KFS10292','KFS10291','KFS10294','KFS10293','KFS10283','KFS10302','KFS10342','KFS10341','KFS10804','KFS10323','KFS10451','KFS10454','KFS10668','KFS10730','KFS10322','KFS10336','KFS10325','KFS10785','KFS10326','KFS10790','KFS10503','KFS10508','KFS10320','KFS10318','KFS10762','KFS10331','KFS10319','KFS10288','KFS10329','KFS10513','KFS10398','KFS10397','KFS10401','KFS10399','KFS10402','KFS10403','KFS10405','KFS10404','KFS10400','KFS10333','KFS10334','KFS10335','KFS10304','KFS10337','KFS10339','KFS10328','KFS10312','KFS10284','KFS10287','KFS10316','KFS10317','KFS10313','KFS10314','KFS10773','KFS10779','KFS10387','KFS10767','KFS10746','KFS10470','KFS10754','KFS10798','KFS10445','KFS10698','KFS10758','KFS10327','KFS10372','KFS10373');
DELETE FROM krim_pnd_role_perm_t WHERE perm_id in ('KFS10282','KFS10321','KFS10299','KFS10285','KFS10286','KFS10544','KFS10518','KFS10738','KFS10735','KFS10721','KFS10722','KFS10723','KFS10338','KFS10340','KFS10330','KFS10733','KFS10315','KFS10332','KFS10307','KFS10310','KFS10309','KFS10311','KFS10308','KFS10296','KFS10295','KFS10298','KFS10297','KFS10290','KFS10292','KFS10291','KFS10294','KFS10293','KFS10283','KFS10302','KFS10342','KFS10341','KFS10804','KFS10323','KFS10451','KFS10454','KFS10668','KFS10730','KFS10322','KFS10336','KFS10325','KFS10785','KFS10326','KFS10790','KFS10503','KFS10508','KFS10320','KFS10318','KFS10762','KFS10331','KFS10319','KFS10288','KFS10329','KFS10513','KFS10398','KFS10397','KFS10401','KFS10399','KFS10402','KFS10403','KFS10405','KFS10404','KFS10400','KFS10333','KFS10334','KFS10335','KFS10304','KFS10337','KFS10339','KFS10328','KFS10312','KFS10284','KFS10287','KFS10316','KFS10317','KFS10313','KFS10314','KFS10773','KFS10779','KFS10387','KFS10767','KFS10746','KFS10470','KFS10754','KFS10798','KFS10445','KFS10698','KFS10758','KFS10327','KFS10372','KFS10373');
DELETE FROM krim_pnd_role_rsp_t WHERE rsp_id in ('KFS10380','KFS10350','KFS10354','KFS10353','KFS10345','KFS10687','KFS10347','KFS10344','KFS10356','KFS10343','KFS10346','KFS10363','KFS10706','KFS10549');
DELETE FROM krim_pnd_role_perm_t WHERE role_id in ('KFS10712','KFS10261','KFS10258','KFS10264','KFS10266','KFS10256','KFS10265','KFS10686','KFS10257','KFS10268','KFS10255','KFS10267','KFS10263','KFS10260','KFS10262');
DELETE FROM krim_pnd_role_rsp_t WHERE role_id in ('KFS10712','KFS10261','KFS10258','KFS10264','KFS10266','KFS10256','KFS10265','KFS10686','KFS10257','KFS10268','KFS10255','KFS10267','KFS10263','KFS10260','KFS10262');
DELETE FROM krim_perm_attr_data_t WHERE perm_id in ('KFS10282','KFS10321','KFS10299','KFS10285','KFS10286','KFS10544','KFS10518','KFS10738','KFS10735','KFS10721','KFS10722','KFS10723','KFS10338','KFS10340','KFS10330','KFS10733','KFS10315','KFS10332','KFS10307','KFS10310','KFS10309','KFS10311','KFS10308','KFS10296','KFS10295','KFS10298','KFS10297','KFS10290','KFS10292','KFS10291','KFS10294','KFS10293','KFS10283','KFS10302','KFS10342','KFS10341','KFS10804','KFS10323','KFS10451','KFS10454','KFS10668','KFS10730','KFS10322','KFS10336','KFS10325','KFS10785','KFS10326','KFS10790','KFS10503','KFS10508','KFS10320','KFS10318','KFS10762','KFS10331','KFS10319','KFS10288','KFS10329','KFS10513','KFS10398','KFS10397','KFS10401','KFS10399','KFS10402','KFS10403','KFS10405','KFS10404','KFS10400','KFS10333','KFS10334','KFS10335','KFS10304','KFS10337','KFS10339','KFS10328','KFS10312','KFS10284','KFS10287','KFS10316','KFS10317','KFS10313','KFS10314','KFS10773','KFS10779','KFS10387','KFS10767','KFS10746','KFS10470','KFS10754','KFS10798','KFS10445','KFS10698','KFS10758','KFS10327','KFS10372','KFS10373');
DELETE FROM krim_rsp_attr_data_t WHERE rsp_id in ('KFS10380','KFS10350','KFS10354','KFS10353','KFS10345','KFS10687','KFS10347','KFS10344','KFS10356','KFS10343','KFS10346','KFS10363','KFS10706','KFS10549');
DELETE FROM krim_role_perm_t WHERE perm_id in ('KFS10282','KFS10321','KFS10299','KFS10285','KFS10286','KFS10544','KFS10518','KFS10738','KFS10735','KFS10721','KFS10722','KFS10723','KFS10338','KFS10340','KFS10330','KFS10733','KFS10315','KFS10332','KFS10307','KFS10310','KFS10309','KFS10311','KFS10308','KFS10296','KFS10295','KFS10298','KFS10297','KFS10290','KFS10292','KFS10291','KFS10294','KFS10293','KFS10283','KFS10302','KFS10342','KFS10341','KFS10804','KFS10323','KFS10451','KFS10454','KFS10668','KFS10730','KFS10322','KFS10336','KFS10325','KFS10785','KFS10326','KFS10790','KFS10503','KFS10508','KFS10320','KFS10318','KFS10762','KFS10331','KFS10319','KFS10288','KFS10329','KFS10513','KFS10398','KFS10397','KFS10401','KFS10399','KFS10402','KFS10403','KFS10405','KFS10404','KFS10400','KFS10333','KFS10334','KFS10335','KFS10304','KFS10337','KFS10339','KFS10328','KFS10312','KFS10284','KFS10287','KFS10316','KFS10317','KFS10313','KFS10314','KFS10773','KFS10779','KFS10387','KFS10767','KFS10746','KFS10470','KFS10754','KFS10798','KFS10445','KFS10698','KFS10758','KFS10327','KFS10372','KFS10373');
DELETE FROM krim_role_rsp_t WHERE rsp_id in ('KFS10380','KFS10350','KFS10354','KFS10353','KFS10345','KFS10687','KFS10347','KFS10344','KFS10356','KFS10343','KFS10346','KFS10363','KFS10706','KFS10549');
DELETE FROM krim_perm_t WHERE perm_id in ('KFS10282','KFS10321','KFS10299','KFS10285','KFS10286','KFS10544','KFS10518','KFS10738','KFS10735','KFS10721','KFS10722','KFS10723','KFS10338','KFS10340','KFS10330','KFS10733','KFS10315','KFS10332','KFS10307','KFS10310','KFS10309','KFS10311','KFS10308','KFS10296','KFS10295','KFS10298','KFS10297','KFS10290','KFS10292','KFS10291','KFS10294','KFS10293','KFS10283','KFS10302','KFS10342','KFS10341','KFS10804','KFS10323','KFS10451','KFS10454','KFS10668','KFS10730','KFS10322','KFS10336','KFS10325','KFS10785','KFS10326','KFS10790','KFS10503','KFS10508','KFS10320','KFS10318','KFS10762','KFS10331','KFS10319','KFS10288','KFS10329','KFS10513','KFS10398','KFS10397','KFS10401','KFS10399','KFS10402','KFS10403','KFS10405','KFS10404','KFS10400','KFS10333','KFS10334','KFS10335','KFS10304','KFS10337','KFS10339','KFS10328','KFS10312','KFS10284','KFS10287','KFS10316','KFS10317','KFS10313','KFS10314','KFS10773','KFS10779','KFS10387','KFS10767','KFS10746','KFS10470','KFS10754','KFS10798','KFS10445','KFS10698','KFS10758','KFS10327','KFS10372','KFS10373');
DELETE FROM krim_rsp_t WHERE rsp_id in ('KFS10380','KFS10350','KFS10354','KFS10353','KFS10345','KFS10687','KFS10347','KFS10344','KFS10356','KFS10343','KFS10346','KFS10363','KFS10706','KFS10549');
DELETE FROM krim_role_perm_t WHERE role_id in ('KFS10712','KFS10261','KFS10258','KFS10264','KFS10266','KFS10256','KFS10265','KFS10686','KFS10257','KFS10268','KFS10255','KFS10267','KFS10263','KFS10260','KFS10262');
DELETE FROM krim_role_rsp_t WHERE role_id in ('KFS10712','KFS10261','KFS10258','KFS10264','KFS10266','KFS10256','KFS10265','KFS10686','KFS10257','KFS10268','KFS10255','KFS10267','KFS10263','KFS10260','KFS10262');
DELETE FROM krim_role_document_t WHERE role_id in ('KFS10712','KFS10261','KFS10258','KFS10264','KFS10266','KFS10256','KFS10265','KFS10686','KFS10257','KFS10268','KFS10255','KFS10267','KFS10263','KFS10260','KFS10262');
DELETE FROM krim_role_mbr_attr_data_t WHERE role_mbr_id in ('KFS10270','KFS10702','KFS10271','KFS10272','KFS10273','KFS10268','KFS10269','KFS10274','KFS10275','KFS10279','KFS10281','KFS10280','KFS10278','KFS10277','KFS10276','KFS10693','KFS10795');
DELETE FROM krim_role_rsp_actn_t WHERE role_mbr_id in ('KFS10270','KFS10702','KFS10271','KFS10272','KFS10273','KFS10268','KFS10269','KFS10274','KFS10275','KFS10279','KFS10281','KFS10280','KFS10278','KFS10277','KFS10276','KFS10693','KFS10795');
DELETE FROM krim_role_rsp_actn_t WHERE role_rsp_id in ('KFS10000','KFS10001','KFS10002','KFS10003','KFS10005','KFS10008','KFS10011','KFS10012','KFS10014','KFS10021','KFS10385','KFS10554','KFS10692','KFS10711');
DELETE FROM krim_role_mbr_t WHERE role_id in ('KFS10712','KFS10261','KFS10258','KFS10264','KFS10266','KFS10256','KFS10265','KFS10686','KFS10257','KFS10268','KFS10255','KFS10267','KFS10263','KFS10260','KFS10262');
DELETE FROM krim_role_t WHERE role_id in ('KFS10712','KFS10261','KFS10258','KFS10264','KFS10266','KFS10256','KFS10265','KFS10686','KFS10257','KFS10268','KFS10255','KFS10267','KFS10263','KFS10260','KFS10262');

DELETE FROM krlc_st_t WHERE obj_id like 'KFS-TEM%';
DELETE FROM krcr_nmspc_t WHERE nmspc_cd IN ('KFS-TEM');