<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2016 The Kuali Foundation
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

<c:set var="title">
    <c:out value="${pageContext.request.getParameter('title')}"/>
</c:set>
<c:if test="${empty title}">
    <c:set var="title" value="Remote View"/>
</c:if>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Financials :: <c:out value="${title}"/></title>
</head>
<body>
  <kul:page docTitle="" showDocumentInfo="false" transactionalDocument="false" renderInnerDiv="true">
    <iframe src="<c:out value="${pageContext.request.getParameter('url')}"/>" name="remote-iframe" title="Financials Remote View" style="height: calc(100% - 60px);" width="100%" frameborder="0"></iframe>
  </kul:page>
</body>
</html>
