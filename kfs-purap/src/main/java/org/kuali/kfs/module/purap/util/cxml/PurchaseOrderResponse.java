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
package org.kuali.kfs.module.purap.util.cxml;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderResponse extends B2BShoppingCartBase {

    private List errors = new ArrayList();

    public void addPOResponseErrorMessage(String errorText) {
        if (StringUtils.isNotEmpty(errorText)) {
            errors.add(errorText);
        }
    }

    public List getPOResponseErrorMessages() {

        if (!isSuccess()) {
            return errors;
        } else {
            return null;
        }
    }

    public String toString() {

        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("StatusCode", getStatusCode());
        toString.append("StatusText", getStatusText());
        toString.append("isSuccess", isSuccess());
        toString.append("Errors", getPOResponseErrorMessages());

        return toString.toString();
    }
}
