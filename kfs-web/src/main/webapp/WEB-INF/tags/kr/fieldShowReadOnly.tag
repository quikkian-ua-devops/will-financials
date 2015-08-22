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

<%@ attribute name="field" required="true" type="org.kuali.rice.kns.web.ui.Field" description="The field to render as read only." %>
<%@ attribute name="addHighlighting" required="false"  description="boolean indicating if this field should be highlighted (to indicate old/new change)" %>
<%@ attribute name="isLookup" required="false" description="boolean indicating if this is a Lookup Screen" %>

<%-- Put the .div span around the link instead of vice versa,
    so that if JavaScript changes the .div contents there is no misleading link. --%>
<span id="${field.propertyName}.div">
    <c:choose>
        <c:when test="${not (empty field.inquiryURL.href || empty field.propertyValue)}">
            <%--<a title="<c:out value="${field.inquiryURL.title}"/>" href="<c:out value="${field.inquiryURL.href}&mode=${param.mode}"/>" target="_blank">--%>

             <a href="<c:out value="${field.inquiryURL.href}&mode=modal"/>" title="<c:out value="${field.inquiryURL.title}"/>" data-remodal-target="modal">
                 <kul:readonlyfield addHighlighting="${addHighlighting}" field="${field}" isLookup="${isLookup}" />
             </a>
             <a href="<c:out value="${field.inquiryURL.href}&mode=standalone"/>" target='_blank' title="Open in new tab" class="new-window">
                 <span class="glyphicon glyphicon-new-window"></span>
             </a>
        </c:when>
        <c:otherwise>
            <kul:readonlyfield addHighlighting="${addHighlighting}" field="${field}" isLookup="${isLookup}" />
        </c:otherwise>
    </c:choose>
</span>
