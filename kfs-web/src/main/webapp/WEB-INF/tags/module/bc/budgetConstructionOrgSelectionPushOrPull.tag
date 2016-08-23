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

<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
  <c:set var="tabTitle" value="Push Down Candidates"/>
</c:if>

<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
  <c:set var="tabTitle" value="Pull Up Candidates"/>
</c:if>

<kul:tab tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="reportSel">
	<div class="tab-container" id="G02" style="display: block;">
        <table class="standard">
      		<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
                <tr>
                    <td width="200" class="right">
                        <html:submit
                                property="methodToCall.performShowPullUpBudgetDocs"
                                title="Show Documents"
                                alt="Show Documents"
                                styleClass="btn btn-default"
                                value="View"/>
                    </td>
                    <td>List Pullup Candidate Documents </td>
                </tr>
            </c:if>
            <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
                <tr>
                    <td width="200" class="right">
                        <html:submit
                                property="methodToCall.performShowPushDownBudgetDocs"
                                title="Show Documents"
                                alt="Show Documents"
                                styleClass="btn btn-default"
                                value="View"/>
                    </td>
                    <td>List Pushdown Candidate Documents </td>
                </tr>
            </c:if>
      </table>
  </div>
</kul:tab>
