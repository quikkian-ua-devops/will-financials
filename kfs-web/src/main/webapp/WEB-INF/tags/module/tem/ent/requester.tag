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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<c:set var="documentAttributes" value="${DataDictionary.TravelEntertainmentDocument.attributes}" />
<c:set var="travelerAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="8" />

<h3>Payee (Person to be reimbursed)</h3>

<table cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<th>
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.eventTitle}" />
			</div>
		</th>
		<td class="datacell" colspan="3">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.eventTitle}" property="document.eventTitle" readOnly="${!fullEntryMode || KualiForm.eventHostandEventNameReadonly}" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.hostName}" />
			</div>
		</th>

		<td class="datacell" colspan="3">
			<div align="left">
				<kul:htmlControlAttribute attributeEntry="${documentAttributes.hostName}" property="document.hostName" readOnly="${!fullEntryMode || KualiForm.eventHostandEventNameReadonly}" />
			</div>
		</td>
	</tr>
	<c:if test="${fullEntryMode}">
		<tr>
			<c:choose>
				<c:when test="${lookupRequesterMode}">
					<th class="bord-l-b"><div align="right">Payee Lookup:</div>
					</th>
					<td class="datacell" >
						<div align="left">
							&nbsp;
							<kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TravelerProfileForLookup" lookupParameters="document.traveler.travelerTypeCode:travelerTypeCode" />

						</div>
					</td>
				</c:when>
				<c:otherwise>
					<th class="bord-l-b">&nbsp;</th><td class="datacell">&nbsp;</td>
				</c:otherwise>
			</c:choose>
			 <th class="bord-l-b">
	            <div align="right" >
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.hostAsPayee}" />
	           	</div>
            </th>
            <td class="datacell">
            	<kul:htmlControlAttribute attributeEntry="${documentAttributes.hostAsPayee}" property="document.hostAsPayee" readOnly="${!fullEntryMode || KualiForm.eventHostandEventNameReadonly}" onclick="javascript: copy_payeeToHostName('${KualiForm.document.traveler.firstName}', '${KualiForm.document.traveler.lastName}');" />
            </td>
		</tr>
	</c:if>
	<tr>
		<th>
			<div align="right">Payee Type Code:</div>
		</th>
		<td class="datacell" colspan="3">
			<div align="left">
				<kul:htmlControlAttribute attributeEntry="${DataDictionary.TravelerType.attributes.name}" property="document.traveler.travelerType.name" readOnly="true" />
			</div>
		</td>
	</tr>

	<c:if test="${KualiForm.document.traveler.travelerTypeCode == 'EMP'}">
		<tr>
			<th class="bord-l-b">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.principalId}" />
				</div>
			</th>
			<td class="datacell">
				<kul:htmlControlAttribute attributeEntry="${travelerAttributes.principalId}" property="document.traveler.principalId" readOnly="true" />
			</td>
			<th>
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.principalName}" readOnly="true" />
				</div>
			</th>
			<td class="datacell">
				<kul:htmlControlAttribute attributeEntry="${travelerAttributes.principalName}" property="document.traveler.principalName" readOnly="true" />
			</td>
		</tr>
	</c:if>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.firstName}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute attributeEntry="${travelerAttributes.firstName}" property="document.traveler.firstName" readOnly="true" />
		</td>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.lastName}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute attributeEntry="${travelerAttributes.lastName}" property="document.traveler.lastName" readOnly="true" />
		</td>
	</tr>
	<c:if test="${fullEntryMode}">
		<tr>
			<th class="bord-l-b"><div align="right">Address Lookup:</div>
			</th>
			<td class="datacell" colspan="3">
            	<div align="left">&nbsp;
			    	<kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TemProfileAddress" lookupParameters="document.traveler.principalId:principalId,document.traveler.customerNumber:customerNumber"
			    	fieldConversions="streetAddressLine1:document.traveler.streetAddressLine1,streetAddressLine2:document.traveler.streetAddressLine2,zipCode:document.traveler.zipCode,countryCode:document.traveler.countryCode,stateCode:document.traveler.stateCode,cityName:document.traveler.cityName" />
				</div>
			</td>
		</tr>
	</c:if>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine1}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute attributeEntry="${travelerAttributes.streetAddressLine1}" property="document.traveler.streetAddressLine1" readOnly="true" />
		</td>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine2}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute attributeEntry="${travelerAttributes.streetAddressLine2}" property="document.traveler.streetAddressLine2" readOnly="true" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.cityName}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute attributeEntry="${travelerAttributes.cityName}" property="document.traveler.cityName" readOnly="true" />
		</td>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.stateCode}" />
			</div>
		</th>
		<td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${travelerAttributes.stateCode}" property="document.traveler.stateCode" readOnly="true"
				tabindexOverride="${tabindexOverrideBase + 5}" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.countryCode}" />
			</div>
		</th>
		<td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${travelerAttributes.countryCode}" property="document.traveler.countryCode" readOnly="true"
				tabindexOverride="${tabindexOverrideBase + 4}" /></td>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.zipCode}" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${travelerAttributes.zipCode}"
				property="document.traveler.zipCode" readOnly="true" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.emailAddress}" />
			</div>
		</th>
		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${travelerAttributes.emailAddress}" property="document.traveler.emailAddress" readOnly="true" /></td>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.phoneNumber}" />
			</div></th>
		<td class="datacell">
			<kul:htmlControlAttribute attributeEntry="${travelerAttributes.phoneNumber}" property="document.traveler.phoneNumber" readOnly="true" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.liabilityInsurance}" />
			</div>
		</th>
		<td class="datacell" colspan="3">
			<div align="left">
				<kul:htmlControlAttribute attributeEntry="${travelerAttributes.liabilityInsurance}" property="document.traveler.liabilityInsurance" readOnly="${!fullEntryMode}" />
			</div>
		</td>
	</tr>
</table>
