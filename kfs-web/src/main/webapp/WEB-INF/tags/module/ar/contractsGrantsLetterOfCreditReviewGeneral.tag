<%--

    The Kuali Financial System, a comprehensive financial management system for higher education.

    Copyright 2005-2017 Kuali, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>


<c:set var="documentAttributes" value="${DataDictionary.ContractsGrantsLetterOfCreditReviewDocument.attributes}" />

<kul:tab tabTitle="General" defaultOpen="true">
	<div class="tab-container" align=center>
		<h3>General</h3>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="General Section">

			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}" />
					</div>
				</th>
				<td><a
					href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.LetterOfCreditFundGroup&letterOfCreditFundGroupCode=${KualiForm.document.letterOfCreditFundGroupCode}"
					target="_blank"> <kul:htmlControlAttribute attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}"
							property="document.letterOfCreditFundGroupCode" readOnly="true" />
				</a></td>
			</tr>
			<c:if test="${! empty KualiForm.document.letterOfCreditFundCode}">
				<tr>
					<th align=right valign=middle class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${documentAttributes.letterOfCreditFundCode}" />
						</div>
					</th>

					<td align=left valign=middle class="datacell" style="width: 50%;"><a
						href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.LetterOfCreditFund&letterOfCreditFundCode=${KualiForm.document.letterOfCreditFundCode}"
						target="_blank"> <kul:htmlControlAttribute attributeEntry="${documentAttributes.letterOfCreditFundCode}"
								property="document.letterOfCreditFundCode" readOnly="true" />
					</a></td>
				</tr>
			</c:if>
		</table>
	</div>
</kul:tab>
