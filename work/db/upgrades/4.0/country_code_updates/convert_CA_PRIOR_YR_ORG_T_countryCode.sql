
-- This script will migrate a column in a table from the former Rice country 
-- codes which were based on FIPS 10-4 (with some minor differences) to the new
-- Rice country codes which are based on ISO 3166-1.  This script may not 
-- properly execute on columns with a primary key or unique constraint as some
-- of the former codes have merged (i.e. - all US Minor Outlying Islands have 
-- been unified under a single code, UM).  This script also does not take any 
-- action on codes that are not part of the list of former Rice country codes.
--
-- Table Name: 	CA_PRIOR_YR_ORG_T
-- Column Name: ORG_CNTRY_CD
--
-- In order to avoid collisions between the former codes and the new codes, the 
-- codes are first changed to an interim, unique code.  Once that change is 
-- complete, they are changed to the new, correct code.
--
-- Change to temporary code
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='A0' where ORG_CNTRY_CD='AA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='A1' where ORG_CNTRY_CD='AC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='A3' where ORG_CNTRY_CD='AG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='A4' where ORG_CNTRY_CD='AJ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='A7' where ORG_CNTRY_CD='AN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='A9' where ORG_CNTRY_CD='AQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B1' where ORG_CNTRY_CD='AS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B2' where ORG_CNTRY_CD='AT';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B3' where ORG_CNTRY_CD='AU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B4' where ORG_CNTRY_CD='AV';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B5' where ORG_CNTRY_CD='AY';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B6' where ORG_CNTRY_CD='BA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B8' where ORG_CNTRY_CD='BC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='B9' where ORG_CNTRY_CD='BD';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C1' where ORG_CNTRY_CD='BF';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C2' where ORG_CNTRY_CD='BG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C3' where ORG_CNTRY_CD='BH';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C4' where ORG_CNTRY_CD='BK';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C5' where ORG_CNTRY_CD='BL';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C6' where ORG_CNTRY_CD='BM';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C7' where ORG_CNTRY_CD='BN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C8' where ORG_CNTRY_CD='BO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='C9' where ORG_CNTRY_CD='BP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='D0' where ORG_CNTRY_CD='BQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='D2' where ORG_CNTRY_CD='BS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='D4' where ORG_CNTRY_CD='BU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='D6' where ORG_CNTRY_CD='BX';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='D7' where ORG_CNTRY_CD='BY';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='D9' where ORG_CNTRY_CD='CB';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E0' where ORG_CNTRY_CD='CD';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E1' where ORG_CNTRY_CD='CE';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E2' where ORG_CNTRY_CD='CF';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E3' where ORG_CNTRY_CD='CG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E4' where ORG_CNTRY_CD='CH';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E5' where ORG_CNTRY_CD='CI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E6' where ORG_CNTRY_CD='CJ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E7' where ORG_CNTRY_CD='CK';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='E9' where ORG_CNTRY_CD='CN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='F1' where ORG_CNTRY_CD='CQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='F2' where ORG_CNTRY_CD='CR';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='F3' where ORG_CNTRY_CD='CS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='F4' where ORG_CNTRY_CD='CT';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='F7' where ORG_CNTRY_CD='CW';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='F9' where ORG_CNTRY_CD='CZ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='G0' where ORG_CNTRY_CD='DA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='G2' where ORG_CNTRY_CD='DO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='G3' where ORG_CNTRY_CD='DR';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='G6' where ORG_CNTRY_CD='EI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='G7' where ORG_CNTRY_CD='EK';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='G8' where ORG_CNTRY_CD='EN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='H0' where ORG_CNTRY_CD='ES';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='H2' where ORG_CNTRY_CD='EU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='H3' where ORG_CNTRY_CD='EZ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='H4' where ORG_CNTRY_CD='FA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='H5' where ORG_CNTRY_CD='FG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='I0' where ORG_CNTRY_CD='FP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='I1' where ORG_CNTRY_CD='FQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='I3' where ORG_CNTRY_CD='FS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='I4' where ORG_CNTRY_CD='GA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='I5' where ORG_CNTRY_CD='GB';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='I6' where ORG_CNTRY_CD='GE';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='I7' where ORG_CNTRY_CD='GG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='J0' where ORG_CNTRY_CD='GJ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='J1' where ORG_CNTRY_CD='GK';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='J3' where ORG_CNTRY_CD='GM';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='J4' where ORG_CNTRY_CD='GO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='J6' where ORG_CNTRY_CD='GQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='J9' where ORG_CNTRY_CD='GV';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='K1' where ORG_CNTRY_CD='GZ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='K2' where ORG_CNTRY_CD='HA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='K5' where ORG_CNTRY_CD='HO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='K6' where ORG_CNTRY_CD='HQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='K9' where ORG_CNTRY_CD='IC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='L4' where ORG_CNTRY_CD='IP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='L6' where ORG_CNTRY_CD='IS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='L8' where ORG_CNTRY_CD='IV';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='L9' where ORG_CNTRY_CD='IY';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='M0' where ORG_CNTRY_CD='IZ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='M1' where ORG_CNTRY_CD='JA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='M4' where ORG_CNTRY_CD='JN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='M6' where ORG_CNTRY_CD='JQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='M7' where ORG_CNTRY_CD='JU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N0' where ORG_CNTRY_CD='KN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N1' where ORG_CNTRY_CD='KQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N2' where ORG_CNTRY_CD='KR';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N3' where ORG_CNTRY_CD='KS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N4' where ORG_CNTRY_CD='KT';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N5' where ORG_CNTRY_CD='KU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N8' where ORG_CNTRY_CD='LE';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='N9' where ORG_CNTRY_CD='LG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O0' where ORG_CNTRY_CD='LH';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O1' where ORG_CNTRY_CD='LI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O2' where ORG_CNTRY_CD='LO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O3' where ORG_CNTRY_CD='LQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O4' where ORG_CNTRY_CD='LS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O5' where ORG_CNTRY_CD='LT';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O8' where ORG_CNTRY_CD='MA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='O9' where ORG_CNTRY_CD='MB';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='P0' where ORG_CNTRY_CD='MC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='P2' where ORG_CNTRY_CD='MF';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='P3' where ORG_CNTRY_CD='MG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='P4' where ORG_CNTRY_CD='MH';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='P5' where ORG_CNTRY_CD='MI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='P8' where ORG_CNTRY_CD='MN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='P9' where ORG_CNTRY_CD='MO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Q0' where ORG_CNTRY_CD='MP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Q1' where ORG_CNTRY_CD='MQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Q4' where ORG_CNTRY_CD='MU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Q6' where ORG_CNTRY_CD='MW';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='R0' where ORG_CNTRY_CD='NA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='R2' where ORG_CNTRY_CD='NE';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='R4' where ORG_CNTRY_CD='NG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='R5' where ORG_CNTRY_CD='NH';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='R6' where ORG_CNTRY_CD='NI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='S1' where ORG_CNTRY_CD='NS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='S2' where ORG_CNTRY_CD='NU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='S4' where ORG_CNTRY_CD='OC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='S5' where ORG_CNTRY_CD='PA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='S6' where ORG_CNTRY_CD='PC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='S8' where ORG_CNTRY_CD='PF';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='S9' where ORG_CNTRY_CD='PG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='T2' where ORG_CNTRY_CD='PM';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='T3' where ORG_CNTRY_CD='PO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='T4' where ORG_CNTRY_CD='PP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='T5' where ORG_CNTRY_CD='PS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='T6' where ORG_CNTRY_CD='PU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='T9' where ORG_CNTRY_CD='RM';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='U1' where ORG_CNTRY_CD='RP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='U2' where ORG_CNTRY_CD='RQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='U3' where ORG_CNTRY_CD='RS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='U6' where ORG_CNTRY_CD='SB';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='U7' where ORG_CNTRY_CD='SC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='U8' where ORG_CNTRY_CD='SE';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='U9' where ORG_CNTRY_CD='SF';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='V0' where ORG_CNTRY_CD='SG';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='V5' where ORG_CNTRY_CD='SN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='V7' where ORG_CNTRY_CD='SP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='V8' where ORG_CNTRY_CD='SR';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='V9' where ORG_CNTRY_CD='ST';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W0' where ORG_CNTRY_CD='SU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W1' where ORG_CNTRY_CD='SV';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W2' where ORG_CNTRY_CD='SW';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W4' where ORG_CNTRY_CD='SZ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W5' where ORG_CNTRY_CD='TC';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W6' where ORG_CNTRY_CD='TD';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W7' where ORG_CNTRY_CD='TE';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='W9' where ORG_CNTRY_CD='TI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X0' where ORG_CNTRY_CD='TK';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X1' where ORG_CNTRY_CD='TL';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X2' where ORG_CNTRY_CD='TN';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X3' where ORG_CNTRY_CD='TO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X4' where ORG_CNTRY_CD='TP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X5' where ORG_CNTRY_CD='TS';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X6' where ORG_CNTRY_CD='TU';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='X9' where ORG_CNTRY_CD='TX';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Y2' where ORG_CNTRY_CD='UK';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Y3' where ORG_CNTRY_CD='UP';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Y4' where ORG_CNTRY_CD='UR';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z0' where ORG_CNTRY_CD='VI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z1' where ORG_CNTRY_CD='VM';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z2' where ORG_CNTRY_CD='VQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z3' where ORG_CNTRY_CD='VT';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z4' where ORG_CNTRY_CD='WA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z5' where ORG_CNTRY_CD='WE';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z7' where ORG_CNTRY_CD='WI';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='Z8' where ORG_CNTRY_CD='WQ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='00' where ORG_CNTRY_CD='WZ';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='01' where ORG_CNTRY_CD='YM';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='02' where ORG_CNTRY_CD='YO';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='03' where ORG_CNTRY_CD='ZA';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='04' where ORG_CNTRY_CD='ZI';
-- Change to final code
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AW' where ORG_CNTRY_CD='A0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AG' where ORG_CNTRY_CD='A1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='DZ' where ORG_CNTRY_CD='A3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AZ' where ORG_CNTRY_CD='A4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AD' where ORG_CNTRY_CD='A7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AS' where ORG_CNTRY_CD='A9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AU' where ORG_CNTRY_CD='B1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AU' where ORG_CNTRY_CD='B2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AT' where ORG_CNTRY_CD='B3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AI' where ORG_CNTRY_CD='B4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AQ' where ORG_CNTRY_CD='B5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BH' where ORG_CNTRY_CD='B6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BW' where ORG_CNTRY_CD='B8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BM' where ORG_CNTRY_CD='B9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BS' where ORG_CNTRY_CD='C1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BD' where ORG_CNTRY_CD='C2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BZ' where ORG_CNTRY_CD='C3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BA' where ORG_CNTRY_CD='C4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BO' where ORG_CNTRY_CD='C5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MM' where ORG_CNTRY_CD='C6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BJ' where ORG_CNTRY_CD='C7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BY' where ORG_CNTRY_CD='C8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SB' where ORG_CNTRY_CD='C9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='D0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TF' where ORG_CNTRY_CD='D2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BG' where ORG_CNTRY_CD='D4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BN' where ORG_CNTRY_CD='D6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='BI' where ORG_CNTRY_CD='D7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KH' where ORG_CNTRY_CD='D9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TD' where ORG_CNTRY_CD='E0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LK' where ORG_CNTRY_CD='E1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CG' where ORG_CNTRY_CD='E2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CD' where ORG_CNTRY_CD='E3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CN' where ORG_CNTRY_CD='E4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CL' where ORG_CNTRY_CD='E5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KY' where ORG_CNTRY_CD='E6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CC' where ORG_CNTRY_CD='E7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KM' where ORG_CNTRY_CD='E9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MP' where ORG_CNTRY_CD='F1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AU' where ORG_CNTRY_CD='F2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CR' where ORG_CNTRY_CD='F3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CF' where ORG_CNTRY_CD='F4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CK' where ORG_CNTRY_CD='F7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CS' where ORG_CNTRY_CD='F9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='DK' where ORG_CNTRY_CD='G0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='DM' where ORG_CNTRY_CD='G2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='DO' where ORG_CNTRY_CD='G3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='IE' where ORG_CNTRY_CD='G6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GQ' where ORG_CNTRY_CD='G7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='EE' where ORG_CNTRY_CD='G8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SV' where ORG_CNTRY_CD='H0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TF' where ORG_CNTRY_CD='H2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CZ' where ORG_CNTRY_CD='H3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='FK' where ORG_CNTRY_CD='H4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GF' where ORG_CNTRY_CD='H5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PF' where ORG_CNTRY_CD='I0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='I1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TF' where ORG_CNTRY_CD='I3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GM' where ORG_CNTRY_CD='I4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GA' where ORG_CNTRY_CD='I5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='DE' where ORG_CNTRY_CD='I6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GE' where ORG_CNTRY_CD='I7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GD' where ORG_CNTRY_CD='J0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GG' where ORG_CNTRY_CD='J1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='DE' where ORG_CNTRY_CD='J3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TF' where ORG_CNTRY_CD='J4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GU' where ORG_CNTRY_CD='J6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GN' where ORG_CNTRY_CD='J9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PS' where ORG_CNTRY_CD='K1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='HT' where ORG_CNTRY_CD='K2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='HN' where ORG_CNTRY_CD='K5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='K6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='IS' where ORG_CNTRY_CD='K9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='FR' where ORG_CNTRY_CD='L4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='IL' where ORG_CNTRY_CD='L6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CI' where ORG_CNTRY_CD='L8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='NT' where ORG_CNTRY_CD='L9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='IQ' where ORG_CNTRY_CD='M0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='JP' where ORG_CNTRY_CD='M1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='NO' where ORG_CNTRY_CD='M4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='M6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TF' where ORG_CNTRY_CD='M7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KP' where ORG_CNTRY_CD='N0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='N1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KI' where ORG_CNTRY_CD='N2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KR' where ORG_CNTRY_CD='N3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CX' where ORG_CNTRY_CD='N4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KW' where ORG_CNTRY_CD='N5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LB' where ORG_CNTRY_CD='N8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LV' where ORG_CNTRY_CD='N9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LT' where ORG_CNTRY_CD='O0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LR' where ORG_CNTRY_CD='O1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SK' where ORG_CNTRY_CD='O2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='O3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LI' where ORG_CNTRY_CD='O4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LS' where ORG_CNTRY_CD='O5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MG' where ORG_CNTRY_CD='O8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MQ' where ORG_CNTRY_CD='O9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MO' where ORG_CNTRY_CD='P0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='YT' where ORG_CNTRY_CD='P2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MN' where ORG_CNTRY_CD='P3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MS' where ORG_CNTRY_CD='P4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MW' where ORG_CNTRY_CD='P5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MC' where ORG_CNTRY_CD='P8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MA' where ORG_CNTRY_CD='P9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MU' where ORG_CNTRY_CD='Q0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='Q1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='OM' where ORG_CNTRY_CD='Q4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='ME' where ORG_CNTRY_CD='Q6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AN' where ORG_CNTRY_CD='R0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='NU' where ORG_CNTRY_CD='R2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='NE' where ORG_CNTRY_CD='R4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='VU' where ORG_CNTRY_CD='R5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='NG' where ORG_CNTRY_CD='R6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SR' where ORG_CNTRY_CD='S1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='NI' where ORG_CNTRY_CD='S2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='ZZ' where ORG_CNTRY_CD='S4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PY' where ORG_CNTRY_CD='S5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PN' where ORG_CNTRY_CD='S6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='XP' where ORG_CNTRY_CD='S8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='XS' where ORG_CNTRY_CD='S9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PA' where ORG_CNTRY_CD='T2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PT' where ORG_CNTRY_CD='T3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PG' where ORG_CNTRY_CD='T4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PW' where ORG_CNTRY_CD='T5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GW' where ORG_CNTRY_CD='T6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='MH' where ORG_CNTRY_CD='T9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PH' where ORG_CNTRY_CD='U1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PR' where ORG_CNTRY_CD='U2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='RU' where ORG_CNTRY_CD='U3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PM' where ORG_CNTRY_CD='U6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='KN' where ORG_CNTRY_CD='U7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SC' where ORG_CNTRY_CD='U8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='ZA' where ORG_CNTRY_CD='U9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SN' where ORG_CNTRY_CD='V0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SG' where ORG_CNTRY_CD='V5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='ES' where ORG_CNTRY_CD='V7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='RS' where ORG_CNTRY_CD='V8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='LC' where ORG_CNTRY_CD='V9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SD' where ORG_CNTRY_CD='W0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SJ' where ORG_CNTRY_CD='W1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SE' where ORG_CNTRY_CD='W2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='CH' where ORG_CNTRY_CD='W4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='AE' where ORG_CNTRY_CD='W5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TT' where ORG_CNTRY_CD='W6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TF' where ORG_CNTRY_CD='W7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TJ' where ORG_CNTRY_CD='W9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TC' where ORG_CNTRY_CD='X0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TK' where ORG_CNTRY_CD='X1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TO' where ORG_CNTRY_CD='X2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TG' where ORG_CNTRY_CD='X3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='ST' where ORG_CNTRY_CD='X4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TN' where ORG_CNTRY_CD='X5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TR' where ORG_CNTRY_CD='X6';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='TM' where ORG_CNTRY_CD='X9';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='GB' where ORG_CNTRY_CD='Y2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UA' where ORG_CNTRY_CD='Y3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SU' where ORG_CNTRY_CD='Y4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='VG' where ORG_CNTRY_CD='Z0';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='VN' where ORG_CNTRY_CD='Z1';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='VI' where ORG_CNTRY_CD='Z2';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='VA' where ORG_CNTRY_CD='Z3';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='NA' where ORG_CNTRY_CD='Z4';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='PS' where ORG_CNTRY_CD='Z5';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='EH' where ORG_CNTRY_CD='Z7';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='UM' where ORG_CNTRY_CD='Z8';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='SZ' where ORG_CNTRY_CD='00';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='YE' where ORG_CNTRY_CD='01';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='YU' where ORG_CNTRY_CD='02';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='ZM' where ORG_CNTRY_CD='03';
UPDATE CA_PRIOR_YR_ORG_T SET ORG_CNTRY_CD='ZW' where ORG_CNTRY_CD='04';

