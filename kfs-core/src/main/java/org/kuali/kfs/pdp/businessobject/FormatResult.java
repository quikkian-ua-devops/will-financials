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
/*
 * Created on Aug 17, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.krad.bo.TransientBusinessObjectBase;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

import java.util.LinkedHashMap;


public class FormatResult extends TransientBusinessObjectBase implements Comparable {
    private Integer procId;
    private boolean pymtAttachment;
    private boolean pymtSpecialHandling;
    private boolean processImmediate;
    private CustomerProfile cust;
    private int payments;
    private KualiDecimal amount;
    private DisbursementType disbursementType;
    private int beginDisbursementNbr;
    private int endDisbursementNbr;
    private KualiInteger sortGroup;


    public FormatResult() {
        super();
        amount = KualiDecimal.ZERO;
        payments = 0;
    }

    public FormatResult(Integer p, CustomerProfile c) {
        procId = p;
        cust = c;
        amount = KualiDecimal.ZERO;
        payments = 0;
    }

    public KualiInteger getSortGroupId() {

        return sortGroup;
    }

    public KualiInteger getSortGroupOverride() {
        return sortGroup;
    }

    public void setSortGroupOverride(KualiInteger sortGroup) {
        this.sortGroup = sortGroup;
    }

    public boolean isProcessImmediate() {
        return processImmediate;
    }

    public void setProcessImmediate(boolean processImmediate) {
        this.processImmediate = processImmediate;
    }

    public boolean isPymtAttachment() {
        return pymtAttachment;
    }

    public void setPymtAttachment(boolean pymtAttachment) {
        this.pymtAttachment = pymtAttachment;
    }

    public boolean isPymtSpecialHandling() {
        return pymtSpecialHandling;
    }

    public void setPymtSpecialHandling(boolean pymtSpecialHandling) {
        this.pymtSpecialHandling = pymtSpecialHandling;
    }

    public int getBeginDisbursementNbr() {
        return beginDisbursementNbr;
    }

    public void setBeginDisbursementNbr(int beginDisbursementNbr) {
        this.beginDisbursementNbr = beginDisbursementNbr;
    }

    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    public int getEndDisbursementNbr() {
        return endDisbursementNbr;
    }

    public void setEndDisbursementNbr(int endDisbursementNbr) {
        this.endDisbursementNbr = endDisbursementNbr;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public CustomerProfile getCust() {
        return cust;
    }

    public void setCust(CustomerProfile cust) {
        this.cust = cust;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public Integer getProcId() {
        return procId;
    }

    public void setProcId(Integer procId) {
        this.procId = procId;
    }

    public String getSortString() {
        StringBuffer sb = new StringBuffer();
        if (getDisbursementType() != null) {
            if (PdpConstants.DisbursementTypeCodes.CHECK.equals(getDisbursementType().getCode())) {
                sb.append("B");
            } else {
                sb.append("A");
            }
        } else {
            sb.append("A");
        }
        sb.append(getSortGroupId());
        sb.append(cust.getChartCode());
        sb.append(cust.getUnitCode());
        sb.append(cust.getSubUnitCode());
        return sb.toString();
    }

    public int compareTo(Object a) {
        FormatResult f = (FormatResult) a;

        return this.getSortString().compareTo(f.getSortString());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FormatResult)) {
            return false;
        }
        FormatResult o = (FormatResult) obj;
        return new EqualsBuilder().append(procId, o.getProcId()).append(getSortGroupId(), o.getSortGroupId()).append(cust, o.getCust()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(7, 3).append(procId).append(getSortGroupId()).append(cust).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("procId", procId).append("sortGroupId", getSortGroupId()).append("cust", cust).toString();
    }


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(PdpPropertyConstants.FormatResult.PROC_ID, this.procId);

        return m;
    }

    public String getSortGroupName() {
        PaymentGroupService paymentGroupService = SpringContext.getBean(PaymentGroupService.class);
        String sortGroupName = paymentGroupService.getSortGroupName(sortGroup.intValue());
        return sortGroupName;
    }

    public void setSortGroupName() {

    }
}
