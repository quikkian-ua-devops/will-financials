/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function updateObjectName(objField) {
    // we want the base label - and the object field is in the detail section
    var elPrefix = findElPrefix(objField.name);
    var coaCode = getElementValue(elPrefix + ".chartOfAccountsCode");
    var objectCode = getElementValue(objField.name);
    var nameFieldName = elPrefix + ".financialEliminationsObject.financialObjectCodeName";
    if (coaCode != "" && objectCode != "") {
        var dwrReply = {
            callback: function (data) {
                if (data != null && typeof data == 'object') {
                    setRecipientValue(nameFieldName, data.financialObjectCodeName);
                } else {
                    setRecipientValue(nameFieldName, wrapError("object not found"), true);
                }
            },
            errorHandler: function (errorMessage) {
                window.status = errorMessage;
                setRecipientValue(nameFieldName, wrapError("object not found"), true);
            }
        };
        ObjectCodeService.getByPrimaryIdForCurrentYear(coaCode, objectCode, dwrReply);
    } else if (objectCode == "") {
        clearRecipients(nameFieldName);
    } else if (coaCode == "") {
        setRecipientValue(nameFieldName, wrapError('chart code is empty'), true);
    } else if (fiscalYear == "") {
        setRecipientValue(nameFieldName, wrapError('fiscal year is missing'), true);
    }
}
