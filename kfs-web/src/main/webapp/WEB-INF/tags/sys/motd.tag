<%--
 Copyright 2005-2016 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<c:set var="motd" value="<%= (new org.kuali.kfs.sys.businessobject.defaultvalue.MessageOfTheDayFinder()).getValue() %>"/>
<c:if test="${!empty motd}">
	<div class="main-panel">
		<div class="headerarea-small" title="Message of the Day" tabindex="-1">
			<h2>Message of the Day</h2>
		</div>
		
		<div class="tab-container motd">
			<c:out value="${motd}"/>
		</div>
	</div>
</c:if>