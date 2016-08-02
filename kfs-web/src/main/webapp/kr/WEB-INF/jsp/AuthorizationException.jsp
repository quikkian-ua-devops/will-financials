<%--

    Copyright 2005-2015 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.kuali.kfs.krad.exception.AuthorizationException"%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<c:set var="parameters"
       value="<%=request.getAttribute(\"org.kuali.kfs.kns.web.struts.action.AuthorizationExceptionAction\")%>" />

<c:if test="${not empty parameters}">
	<c:set var="message" value="${parameters.message}" />
  <c:if test="${empty message}">
    <c:set var="exception" value='<%=request.getAttribute("org.apache.struts.action.EXCEPTION")%>'/>
    <c:set var="message" value="${exception['class'].name}" />
  </c:if>
</c:if>

<kul:page showDocumentInfo="false"
	      headerTitle="Authorization Exception"
	      docTitle="Authorization Exception Report"
	      transactionalDocument="false"
	      htmlFormAction="authorizationExceptionReport"
	      defaultMethodToCall="notify"
	      errorKey="*">

	<html:hidden property="message" write="false" value="${message}" />

    <div class="center">
        <strong>Error Message:</strong>
        ${message}
    </div>
</kul:page>