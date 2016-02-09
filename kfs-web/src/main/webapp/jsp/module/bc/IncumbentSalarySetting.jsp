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
<%@ page import="org.kuali.kfs.sys.context.SpringContext"%>
<%@ page import="org.kuali.kfs.coa.service.AccountService"%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="readOnly" value="${KualiForm.viewOnlyEntry || KualiForm.salarySettingClosed}" />
<c:set var="accountsCanCrossCharts"
	value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />


<kul:page showDocumentInfo="false" htmlFormAction="budgetIncumbentSalarySetting" renderMultipart="true"
	showTabButtons="true" docTitle="${KualiForm.documentTitle}" transactionalDocument="false">

    <html:hidden property="mainWindow" />

    <bc:incumbentSalarySetting readOnly="${readOnly}" accountsCanCrossCharts="${accountsCanCrossCharts}"/>	
    
    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
	        <html:submit
	        	    styleClass="btn btn-green"
                    property="methodToCall.save"
                    title="save"
                    alt="save"
                    value="Save"/>
        </c:if>
    	    
	    <html:submit
       		    styleClass="btn btn-default"
                property="methodToCall.close"
                title="close"
                alt="close"
                value="Close"/>
    </div>

	<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
	<SCRIPT type="text/javascript">
	  subObjectCodeNameSuffix = ".financialSubObject.financialSubObjectCdshortNm";
	  var kualiForm = document.forms['KualiForm'];
	  var kualiElements = kualiForm.elements;
	</SCRIPT>
</kul:page>
