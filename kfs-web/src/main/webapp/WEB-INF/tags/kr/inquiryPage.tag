<%--
 Copyright 2007 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="isMaintenance" value="false" />
<c:set var="showDocumentInfo" value="false" />
<c:set var="docTitle" value="${kualiInquirable.title}" />
<c:set var="htmlFormAction" value="inquiry" />
<c:set var="renderMultipart" value="" />
<c:set var="showTabButtons" value="true" />
<c:set var="defaultMethodToCall" value="" />
<c:set var="additionalScriptFiles" value="" />
<c:set var="lookup" value="true" />
<c:set var="headerMenuBar" value="${kualiInquirable.htmlMenuBar}" />
<c:set var="headerTitle" value="Inquiry" />

<kul:page showDocumentInfo="${showDocumentInfo}" docTitle="${docTitle}"
	htmlFormAction="${htmlFormAction}" transactionalDocument="false"
	renderMultipart="${renderMultipart}" showTabButtons="${showTabButtons}"
	defaultMethodToCall="${defaultMethodToCall}" additionalScriptFiles="${additionalScriptFiles}"
	lookup="${lookup}" headerMenuBar="${headerMenuBar}" headerTitle="${headerTitle}">

	<div>
		<%-- settting FieldSections to KualiForm.sections --%>
		<c:set var="FieldSections" value="${KualiForm.sections}" />
		<div class="headerarea-small" id="headerarea-small">
			<h1>${kualiInquirable.title}</h1>
		</div>
	</div>
		<html:hidden property="businessObjectClassName"/>
		<c:forEach items="${KualiForm.inquiryPrimaryKeys}" var="primaryKey">
			<input type="hidden" name="previousPkValue_${primaryKey.key}" value="<c:out value="${primaryKey.value}"/>"/>
		</c:forEach>
		<c:forEach items="${KualiForm.inactiveRecordDisplay}" var="entry">
			<input type="hidden" name="${Constants.INACTIVE_RECORD_DISPLAY_PARAM_PREFIX}${entry.key}" value="${entry.value}"/>
		</c:forEach>
						
		<%-- Show the information about the business object. --%>
		<c:forEach items="${FieldSections}" var="section">
		  <%-- call helper tag to look ahead through fields for old to new changes, and highlight tab if so --%>
          <kul:checkTabHighlight rows="${section.rows}" addHighlighting="false" />
		  
		  <kul:tab tabTitle="${section.sectionTitle}" defaultOpen="${section.defaultOpen}" tabErrorKey="${section.errorKey}" highlightTab="${tabHighlight}" transparentBackground="${firstTab}" extraButtonSource="${section.extraButtonSource}"> 
		    <div class="tab-container" align="center">
		      <table width="100%" cellpadding=0 cellspacing=0 class="datatable">
				  <kul:inquiryRowDisplay rows="${section.rows}" numberOfColumns="${section.numberOfColumns}" />
			  </table>
	        </div>
		  </kul:tab>
		</c:forEach>
		<kul:panelFooter />


		
		<kul:inquiryControls />


</kul:page>
