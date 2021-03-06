<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Administrative Transactions" />
<div class="body">
	<c:if test="${ConfigProperties.module.capital.asset.enabled == 'true'}">
  		<strong>Capital Asset Builder</strong>
  		<ul class="chan">
	 		<li><portal:portalLink displayTitle="true" title="Capital Asset Builder AP Transactions" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  	 		<li><portal:portalLink displayTitle="true" title="Capital Asset Builder GL Transactions" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  		</ul>

  		<strong>Capital Asset Management</strong>
  		<ul class="chan">
     		<li><portal:portalLink displayTitle="true" title="Asset Manual Payment" url="${ConfigProperties.application.url}/camsAssetPayment.do?methodToCall=docHandler&command=initiate&docTypeName=MPAY" /></li>
  		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
  		<strong>Effort Certification</strong>
  		<ul class="chan">
	 		<li><portal:portalLink displayTitle="true" title="Effort Certification Recreate" url="${ConfigProperties.application.url}/effortCertificationRecreate.do?methodToCall=docHandler&command=initiate&docTypeName=ECD" /></li>
  		</ul>
	</c:if>
	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Cash Management" url="${ConfigProperties.application.url}/financialCashManagement.do?methodToCall=docHandler&command=initiate&docTypeName=CMD" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Correction Process" url="${ConfigProperties.application.url}/generalLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GLCP" /></li>									
		<li><portal:portalLink displayTitle="true" title="Journal Voucher" url="${ConfigProperties.application.url}/financialJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=JV" /></li>
		<li><portal:portalLink displayTitle="true" title="Non-Check Disbursement" url="${ConfigProperties.application.url}/financialNonCheckDisbursement.do?methodToCall=docHandler&command=initiate&docTypeName=ND" /></li>
    </ul>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">    
    	<strong>Labor Distribution</strong><br />
    	<ul class="chan">
    		<li><portal:portalLink displayTitle="true" title="Labor Journal Voucher" url="${ConfigProperties.application.url}/laborJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=LLJV" /></li>
			<li><portal:portalLink displayTitle="true" title="Labor Ledger Correction Process" url="${ConfigProperties.application.url}/laborLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=LLCP" /></li>
    	</ul>    
	</c:if>    
	<strong>Prorate Journal Entry</strong>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Journal Entry Sets"    url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.prje.businessobject.PRJESet&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Journal Entry Types"          url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.prje.businessobject.PRJEType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Journal Entry Base Accounts"  url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.prje.businessobject.PRJEBaseAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Journal Entry Base Objects"   url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.prje.businessobject.PRJEBaseObject&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Journal Entry Account Lines"  url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.prje.businessobject.PRJEAccountLine&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	<strong>1099 Process</strong>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Payer" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.tax.businessobject.Payer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Payee"   url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.tax.businessobject.Payee&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Payment" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.tax.businessobject.Payment&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Extract History" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.tax.businessobject.ExtractHistory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Payee 1099 Forms" url="${ConfigProperties.application.url}/taxPayeeSearch.do" /></li>
		<li><portal:portalLink displayTitle="true" title="1099 Exception Report" url="${ConfigProperties.application.url}/taxElectronicFilingReport.do" /></li>
	</ul>
</div>
<channel:portalChannelBottom />
