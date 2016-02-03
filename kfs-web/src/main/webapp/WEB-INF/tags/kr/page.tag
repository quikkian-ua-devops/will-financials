<%--
  ~ Copyright 2006-2011 The Kuali Foundation
  ~
  ~ Licensed under the Educational Community License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.opensource.org/licenses/ecl2.php
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="docTitle" required="true" description="The title to display for the page." %>
<%@ attribute name="docTitleClass" required="false" description="The class added to the title of the page." %>
<%@ attribute name="transactionalDocument" required="true" description="The name of the document type this document page is rendering." %>
<%@ attribute name="showDocumentInfo" required="false" description="Boolean value of whether to display the Document Type name and document type help on the page." %>
<%@ attribute name="headerMenuBar" required="false" description="HTML text for menu bar to display at the top of the page." %>
<%@ attribute name="headerTitle" required="false" description="The title of this page which will be displayed in the browser's header bar.  If left blank, docTitle will be used instead." %>
<%@ attribute name="htmlFormAction" required="false" description="The URL that the HTML form rendered on this page will be posted to." %>
<%@ attribute name="renderMultipart" required="false" description="Boolean value of whether the HTML form rendred on this page will be encoded to accept multipart - ie, uploaded attachment - input." %>
<%@ attribute name="showTabButtons" required="false" description="Whether to show the show/hide all tabs buttons." %>
<%@ attribute name="extraTopButtons" required="false" type="java.util.List" description="A List of org.kuali.kfs.kns.web.ui.ExtraButton objects to display at the top of the page." %>
<%@ attribute name="headerDispatch" required="false" description="Overrides the header navigation tab buttons to go directly to the action given here." %>
<%@ attribute name="lookup" required="false" description="indicates whether the lookup page specific page should be shown"%>

<%-- for non-lookup pages --%>
<%@ attribute name="headerTabActive" required="false" description="The name of the active header tab, if header navigation is used." %>
<%@ attribute name="feedbackKey" required="false" description="application resources key that contains feedback contact address only used when lookup attribute is false"%>
<%@ attribute name="defaultMethodToCall" required="false" description="The name of default methodToCall on the action for this page." %>
<%@ attribute name="errorKey" required="false" description="If present, this is the key which will be used to match errors that need to be rendered at the top of the page." %>
<%@ attribute name="auditCount" required="false" description="The number of audit errors displayed on this page." %>
<%@ attribute name="additionalScriptFiles" required="false" type="java.util.List" description="A List of JavaScript file names to have included on the page." %>
<%@ attribute name="documentWebScope" required="false" description="The scope this page - which is hard coded to session, making this attribute somewhat useless." %>
<%@ attribute name="maintenanceDocument" required="false" description="Boolean value of whether this page is rendering a maintenance document." %>
<%@ attribute name="sessionDocument" required="false" description="Unused." %>
<%@ attribute name="renderRequiredFieldsLabel" required = "false" description="Boolean value of whether to include a helpful note that the asterisk represents a required field - good for accessibility." %>
<%@ attribute name="alternativeHelp" required="false"%>
<%@ attribute name="renderInnerDiv" required="false"%>

<%-- Is the screen an inquiry? --%>
<c:set var="_isInquiry" value="${requestScope[Constants.PARAM_MAINTENANCE_VIEW_MODE] eq Constants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY}" />

<!DOCTYPE html>
<html:html>

	<c:if test="${empty headerTitle}">
		<c:set var="headerTitle" value="${docTitle}"/>
	</c:if>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
		<script type="text/javascript">var breadcrumbs = []</script>
		<c:if test="${not empty SESSION_TIMEOUT_WARNING_MILLISECONDS}">
			<script type="text/javascript">
				<!--
				setTimeout("alert('Your session will expire in ${SESSION_TIMEOUT_WARNING_MINUTES} minutes.')",'${SESSION_TIMEOUT_WARNING_MILLISECONDS}');
				// -->
			</script>
		</c:if>

		<script type="text/javascript">var jsContextPath = "${pageContext.request.contextPath}";</script>
		<title><bean:message key="app.title" /> :: ${headerTitle}</title>
		<c:forEach items="${fn:split(ConfigProperties.kns.css.files, ',')}"
				   var="cssFile">
			<c:if test="${fn:length(fn:trim(cssFile)) > 0}">
				<link href="${pageContext.request.contextPath}/${cssFile}"
					  rel="stylesheet" type="text/css" />
			</c:if>
		</c:forEach>
		<c:forEach items="${fn:split(ConfigProperties.kns.javascript.files, ',')}"
				   var="javascriptFile">
			<c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
				<script language="JavaScript" type="text/javascript"
						src="${pageContext.request.contextPath}/${javascriptFile}"></script>
			</c:if>
		</c:forEach>

		<script type="text/javascript">
			var jq = jQuery.noConflict();
		</script>

		<c:choose>
			<c:when test="${lookup}" >
				<c:if test="${not empty KualiForm.headerNavigationTabs}">
					<link href="kr/css/${KualiForm.navigationCss}" rel="stylesheet" type="text/css" />
				</c:if>

				<!-- allow for custom lookup calls -->
				<script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/kr/scripts/lookup.js"></script>

			</c:when>
			<c:otherwise>
				<c:forEach items="${additionalScriptFiles}" var="scriptFile" >
					<script language="JavaScript" type="text/javascript" src="${scriptFile}"></script>
				</c:forEach>
			</c:otherwise>
		</c:choose>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
		<link href='https://fonts.googleapis.com/css?family=Lato:300,400,700,900,400italic' rel='stylesheet' type='text/css'>
		<link href='${pageContext.request.contextPath}/css/newPortal.css' rel='stylesheet' type='text/css'>
		<link href='${pageContext.request.contextPath}/css/lookup.css' rel='stylesheet' type='text/css'>
		<c:if test="${param.mode ne 'modal'}">
			<script src="${pageContext.request.contextPath}/scripts/jquery.min.js"></script>
			<script src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
			<script src="${pageContext.request.contextPath}/scripts/remodal.min.js"></script>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/remodal.min.css">
		</c:if>
	</head>
	<c:choose>
		<c:when test="${lookup}" >
			<body onload="placeFocus();
			<c:if test='<%= jspContext.findAttribute("KualiForm") != null %>'>
				<c:if test='<%= jspContext.findAttribute("KualiForm").getClass() == org.kuali.kfs.kns.web.struts.form.LookupForm.class %>'>
					<c:out value ="${KualiForm.lookupable.extraOnLoad}" />
				</c:if>
			</c:if>
			">
		</c:when>
		<c:otherwise>
			<body onKeyPress="return isReturnKeyAllowed('${Constants.DISPATCH_REQUEST_PARAMETER}.' , event);">
		</c:otherwise>
	</c:choose>

		<kul:pageBody showDocumentInfo="${showDocumentInfo}" docTitle="${docTitle}" docTitleClass="${docTitleClass}"
					  htmlFormAction="${htmlFormAction}" transactionalDocument="${transactionalDocument}"
					  renderMultipart="${renderMultipart}" showTabButtons="${showTabButtons}" headerDispatch="${headerDispatch}"
					  defaultMethodToCall="${defaultMethodToCall}" lookup="${lookup}" extraTopButtons="${extraTopButtons}"
					  headerMenuBar="${headerMenuBar}" headerTabActive="${headerTabActive}" alternativeHelp="${alternativeHelp}"
					  feedbackKey="${feedbackKey}" errorKey="${errorKey}" auditCount="${auditCount}"
					  documentWebScope="${documentWebScope}" maintenanceDocument="${maintenanceDocument}"
					  renderRequiredFieldsLabel="${renderRequiredFieldsLabel}" renderInnerDiv="${renderInnerDiv}">

			<div id="page-content">
                <jsp:doBody/>
            </div>
		</kul:pageBody>

		<c:if test="${lookup}" >
			<kul:modal/>
		</c:if>
	</body>

</html:html>
