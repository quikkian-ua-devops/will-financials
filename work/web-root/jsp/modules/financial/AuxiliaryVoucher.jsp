<%--
 Copyright 2005-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp" %>

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialAuxiliaryVoucher" documentTypeName="AuxiliaryVoucherDocument" renderMultipart="true" showTabButtons="true">
		<%-- derive displayReadOnly value --%>
		<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />

  	   	<SCRIPT type="text/javascript">
		<!--
		    function submitForChangedType() {
        		document.forms[0].submit();
		    }
		//-->
	    </SCRIPT>

		<kfs:hiddenDocumentFields />

        <kfs:documentOverview editingMode="${KualiForm.editingMode}"/>
		<!-- AUXILIARY VOUCHER SPECIFIC FIELDS -->
		<kul:tab tabTitle="Auxiliary Voucher Details" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_AUXILIARY_VOUCHER_ERRORS}" >
	    	
	    	<div class="tab-container" align="center">
		<h3>Auxiliary Voucher Details</h3>
	    	<table cellpadding="0" class="datatable" summary="view/edit ad hoc recipients">
            <tbody>
              <tr>
                <th width="35%" class="bord-l-b">
                  <div align="right">
                    <kul:htmlAttributeLabel labelFor="selectedAccountingPeriod" attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.accountingPeriod}" useShortLabel="false" />
                  </div>
                </th>
                <td class="datacell-nowrap">
					<c:choose>
						<c:when test="${!readOnly}">
							<!-- REGISTERING EDITABLE FIELD -->
							${kfunc:registerEditableProperty(KualiForm, selectedAccountingPeriod)}
							<select id="selectedAccountingPeriod" name="selectedAccountingPeriod">
								<c:forEach items="${KualiForm.accountingPeriods}" var="accountingPeriod">
									<c:set var="accountingPeriodCompositeValue" value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
									<c:choose>
										<c:when test="${KualiForm.selectedAccountingPeriod==accountingPeriodCompositeValue}" >
											<option value='<c:out value="${accountingPeriodCompositeValue}"/>' selected="selected"><c:out value="${accountingPeriod.universityFiscalPeriodName}" /></option>
										</c:when>
										<c:otherwise>
											<option value='<c:out value="${accountingPeriodCompositeValue}" />'><c:out value="${accountingPeriod.universityFiscalPeriodName}" /></option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<c:out value="${KualiForm.accountingPeriod.universityFiscalPeriodName}" />
						</c:otherwise>
					</c:choose>
                </td>
              </tr>
              <tr>
                  <th width="35%" class="bord-l-b">
                      <div align="right">
                          <kul:htmlAttributeLabel attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.typeCode}" useShortLabel="false" />
                      </div>
                  </th>
                  <td class="datacell-nowrap">
         	          <kul:htmlControlAttribute
					    	attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.typeCode}"
                            property="document.typeCode"
                            readOnly="${readOnly}" 
                            readOnlyAlternateDisplay="${KualiForm.formattedAuxiliaryVoucherType}" 
                            onchange="submitForChangedType()"/>          
						<NOSCRIPT>
    						<html:submit value="select" alt="press this button to refresh the page after changing the voucher type." />
						</NOSCRIPT>                
                  </td>
              </tr>
              <c:choose>
                  <c:when test="${empty KualiForm.document.typeCode || KualiForm.document.typeCode == KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE}">
                  </c:when>                  
                  <c:otherwise>
                      <c:set var="reversalReadOnly" value="${readOnly}"/>
                      <c:if test="${!reversalReadOnly}">  <!--  if we're already readOnly b/c of authz permissions, then we want to stay that way -->
	                      <c:if test="${KualiForm.document.typeCode == KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE}">
						      <c:set var="reversalReadOnly" value="true"/>
	                      </c:if>
	                  </c:if>
                      <tr>
                          <kul:htmlAttributeHeaderCell
                                  attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.reversalDate}"
                                  horizontal="true"
                                  width="35%"
                                  />
                          <td class="datacell-nowrap">
                              <kul:htmlControlAttribute
                                      attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.reversalDate}"
                                      datePicker="true"
                                      property="document.reversalDate"
                                      readOnly="${reversalReadOnly}"
                                      readOnlyAlternateDisplay="${KualiForm.formattedReversalDate}"
                                      />
                          </td>
                      </tr>
                  </c:otherwise>
              </c:choose>
            </tbody>
          </table>
	    	</div>
		</kul:tab>
        <kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
			<sys:accountingLines>
				<sys:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
			</sys:accountingLines>
		</kul:tab>
		<gl:generalLedgerPendingEntries/>

		<kul:notes/>
						
		<kul:adHocRecipients />
			
		<kul:routeLog/>

		<kul:panelFooter/>

		<kfs:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
