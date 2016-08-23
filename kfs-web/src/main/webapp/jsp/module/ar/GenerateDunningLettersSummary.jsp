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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:page headerTitle="Generate Dunning Letters Summary"
          transactionalDocument="false" showDocumentInfo="false"
          htmlFormAction="arGenerateDunningLettersSummary"
          docTitle="Generate Dunning Letters Summary">

    <div>
        <c:choose>
            <c:when test="${KualiForm.dunningLettersGenerated}">
                <html:submit
                        value="Return"
                        styleClass="btn btn-default" property="methodToCall.cancel"
                        title="return" alt="return"/>
            </c:when>
            <c:otherwise>
                <ar:documentSummaryResults lookupResultsProperty="generateDunningLettersLookupResults" lookupResultTitleProperties="proposalNumber" tabTitleName="Award">
                    <ar:generateDunningLettersSummaryResultContent/>
                </ar:documentSummaryResults>
                <html:submit
                        value="Create"
                        styleClass="btn btn-default"
                        property="methodToCall.generateDunningLetters.number${KualiForm.lookupResultsSequenceNumber}"
                        title="generate_DunningLetters" alt="generate_DunningLetters" onclick="excludeSubmitRestriction=true"/>
                <html:submit
                        value="Cancel"
                        styleClass="btn btn-default" property="methodToCall.cancel"
                        title="cancel" alt="cancel"/>
            </c:otherwise>
        </c:choose>
    </div>
</kul:page>
