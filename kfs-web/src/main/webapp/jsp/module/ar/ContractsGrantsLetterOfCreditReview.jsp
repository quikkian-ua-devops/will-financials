<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2017 Kuali, Inc.
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

<kul:documentPage showDocumentInfo="true"
                  documentTypeName="ContractsGrantsLetterOfCreditReviewDocument"
                  htmlFormAction="arContractsGrantsLetterOfCreditReview"
                  renderMultipart="true" showTabButtons="true">

    <c:set var="readOnly"
           value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>
    <c:set var="displayInitTab"
           value="${KualiForm.editingMode['displayInitTab']}" scope="request"/>

    <sys:hiddenDocumentFields isFinancialDocument="false"/>


    <!-- Display 1st screen -->
    <c:if test="${displayInitTab}">
        <ar:contractsGrantsLetterOfCreditReviewInit/>
        <c:set var="globalButtonTabIndex" value="15"/>
    </c:if>

    <!-- Display 2nd screen -->
    <c:if test="${not displayInitTab}">
        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>

        <ar:contractsGrantsLetterOfCreditReviewGeneral/>


        <ar:contractsGrantsLetterOfCreditReviewDetails
                invPropertyName="document.ccaReviewDetails[${ctr}]"/>
        <kul:notes/>
        <kul:adHocRecipients/>
        <kul:routeLog/>
        <kul:superUserActions/>
    </c:if>

    <c:set var="extraButtons" value="${KualiForm.extraButtons}"
           scope="request"/>
    <kul:documentControls transactionalDocument="true"
                          extraButtons="${extraButtons}"
                          suppressRoutingControls="${displayInitTab}"
                          tabindex="${globalButtonTabIndex}"/>

</kul:documentPage>
