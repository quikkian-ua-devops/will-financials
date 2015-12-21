<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<c:set var="temExtension" value="${DataDictionary.ExpenseTypeObjectCode.attributes}" />
<c:set var="isTA" value="${KualiForm.isTravelAuthorizationDoc}" />

<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType" value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType" value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType" value="${KualiForm.docTypeName}" />
  	
	<div class="tab-container" align="center">
	    <h3>${KualiForm.expenseLabel}</h3>
	    <table class="datatable standard" summary="Actual Expenses">
			<tr>
                <th></th>
				<td colspan="11" class="tab-subhead">* All fields required if section is used</td>
			</tr>

            <tem-exp:actualExpenseHeader detailObject="${KualiForm.newActualExpenseLine}"/>

	        <c:if test="${fullEntryMode}">
				<tem-exp:actualExpenseLine
                        expense="newActualExpenseLine"
                        detailObject="${KualiForm.newActualExpenseLine}"
                        highlight="${true}" />
	        </c:if>

			<logic:iterate indexId="ctr" name="KualiForm" property="document.actualExpenses" id="currentLine">
				<c:set var="lineCounter" value="${ctr + 1}" />
                <c:choose>
                    <c:when test="${fullEntryMode}">
                        <c:set var="highlight" value="${ctr % 2 != 0}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="highlight" value="${ctr % 2 == 0}"/>
                    </c:otherwise>
                </c:choose>

	        	<tem-exp:actualExpenseLine
                        lineNumber="${lineCounter}"
                        expense="document.actualExpenses[${ctr}]"
                        detailObject="${KualiForm.document.actualExpenses[ctr]}"
                        highlight="${highlight}"/>

				<tr class="${highlight ? 'highlight' : ''}">
                    <td colspan="11">
			  			<kul:subtab
                                lookedUpCollectionName="expenseDetails${ctr}"
                                width="${tableWidth}"
			  				    subTabTitle="${KualiForm.expenseLabel} Details - ${KualiForm.document.actualExpenses[ctr].expenseTypeObjectCode.expenseType.name} - ${lineCounter}"
			  				    noShowHideButton="false"
			  				    open="${KualiForm.document.actualExpenses[ctr].defaultTabOpen}">

			  				<table class="datatable standard">
								<c:set var="detailLineCounter" value="0" />
							    <tr>
							    	<th colspan="1">&nbsp;</th>
							        <td colspan="11">
	          							<table cellpadding="0" cellspacing="0" class="datatable">
	          								<tem-exp:actualExpenseDetailHeader detailObject="${KualiForm.document.actualExpenses[ctr]}" />
	          								<c:if test="${fullEntryMode}">
												<tem-exp:actualExpenseDetailLine 
                                                        lineNumber="${ctr}"
                                                        detail="newActualExpenseLines[${ctr}]"
                                                        detailObject="${KualiForm.document.actualExpenses[ctr]}"
                                                        parentObject="${KualiForm.document.actualExpenses[ctr]}"
                                                        highlight="${true}"/>
											</c:if>
											<logic:iterate indexId="ctrDetail" name="currentLine" property="expenseDetails" id="currentLineDetails">
												<c:set var="lineCounterDetails" value="${lineCounterDetails + 1}" />

                                                <c:choose>
                                                    <c:when test="${fullEntryMode}">
                                                        <c:set var="highlight" value="${ctrDetail % 2 != 0}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="highlight" value="${ctrDetail % 2 == 0}"/>
                                                    </c:otherwise>
                                                </c:choose>

												<tem-exp:actualExpenseDetailLine
                                                        detail="document.actualExpenses[${ctr}].expenseDetails[${ctrDetail}]"
                                                        lineNumber="${ctr}"
                                                        detailLineNumber="${ctrDetail}"
                                                        detailObject="${KualiForm.document.actualExpenses[ctr].expenseDetails[ctrDetail]}"
                                                        parentObject="${KualiForm.document.actualExpenses[ctr]}"
                                                        highlight="${highlight}"/>
											</logic:iterate>
	          							</table>
	          						</td>
	          					</tr>
	          				</table>
	          			</kul:subtab>
	          		</td>
	          	</tr>
			</logic:iterate>
		</table>
	</div>	
		          									
		          									
				
