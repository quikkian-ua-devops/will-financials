/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.fixture;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionDetail;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.batch.dataaccess.impl.FiscalYearMakerImpl;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.businessobject.WireCharge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum FiscalYearMakerFixture {

    OPTIONS(SystemOptions.class),
    ORGANIZATION_REVERSION_DETAIL(OrganizationReversionDetail.class, ObjectCode.class, OrganizationReversion.class),
    ORGANIZATION_REVERSION(OrganizationReversion.class),
    SUB_OBJECT_CODE(SubObjectCode.class, ObjectCode.class),
    OBJECT_CODE(ObjectCode.class),
    //    LABOR_OBJECT(LaborObject.class, ObjectCode.class),
    OFFSET_DEFINITION(OffsetDefinition.class, ObjectCode.class),
    //    BENEFITS_CALCULATION(BenefitsCalculation.class),
//    POSITION_OBJECT_BENEFIT(PositionObjectBenefit.class, BenefitsCalculation.class),
    ACCOUNTING_PERIOD(AccountingPeriod.class),
    INDIRECT_COST_RECOVERY_RATE_DETAIL(IndirectCostRecoveryRateDetail.class),
    WIRE_CHARGE(WireCharge.class, ObjectCode.class),
    //    EFFORT_CERTIFICATION_REPORT_EARN_PAYGROUP(EffortCertificationReportEarnPaygroup.class),
//    EFFORT_CERTIFICATION_REPORT_POSITION(EffortCertificationReportPosition.class, EffortCertificationReportDefinition.class),
//    EFFORT_CERTIFICATION_REPORT_DEFINITION(EffortCertificationReportDefinition.class, AccountingPeriod.class),
//    SYSTEM_INFORMATION(SystemInformation.class, ObjectCode.class, SubObjectCode.class),
    UNIVERSITY_DATE(UniversityDate.class),
    OPTIONS1(SystemOptions.class, ObjectCode.class),
    //    OBJECT_CODE1(ObjectCode.class, LaborObject.class),
//    LABOR_OBJECT1(LaborObject.class, SubObjectCode.class),
    WIRE_CHARGE1(WireCharge.class, ObjectCode.class, WireCharge.class);//,
    //MISSING_PARENT_FYM(ObjectCode.class, Account.class);

    public Class<? extends FiscalYearBasedBusinessObject> businessObjectClass;
    public Set<Class<? extends FiscalYearBasedBusinessObject>> parentClasses;

    private FiscalYearMakerFixture(Class<? extends FiscalYearBasedBusinessObject> businessObjectClass, Class<? extends FiscalYearBasedBusinessObject>... parentClasses) {
        this.businessObjectClass = businessObjectClass;
        this.parentClasses = new HashSet<Class<? extends FiscalYearBasedBusinessObject>>(Arrays.asList(parentClasses));
    }

    public FiscalYearMaker createFiscalYearMaker() {
        FiscalYearMakerImpl fiscalYearMaker = new FiscalYearMakerImpl();
        fiscalYearMaker.setBusinessObjectClass(this.businessObjectClass);
        fiscalYearMaker.setParentClasses(this.parentClasses);

        return fiscalYearMaker;
    }

    /**
     * @return list of FiscalYearMaker implementations with valid parent-child configuration
     */
    public static List<FiscalYearMaker> getFiscalYearMakerList_valid() {
        List<FiscalYearMaker> fiscalYearMakers = new ArrayList<FiscalYearMaker>();

        fiscalYearMakers.add(OPTIONS.createFiscalYearMaker());
        fiscalYearMakers.add(ORGANIZATION_REVERSION_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(ORGANIZATION_REVERSION.createFiscalYearMaker());
        fiscalYearMakers.add(SUB_OBJECT_CODE.createFiscalYearMaker());
        fiscalYearMakers.add(OBJECT_CODE.createFiscalYearMaker());
//        fiscalYearMakers.add(LABOR_OBJECT.createFiscalYearMaker());
        fiscalYearMakers.add(OFFSET_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(BENEFITS_CALCULATION.createFiscalYearMaker());
//        fiscalYearMakers.add(POSITION_OBJECT_BENEFIT.createFiscalYearMaker());
        fiscalYearMakers.add(ACCOUNTING_PERIOD.createFiscalYearMaker());
        fiscalYearMakers.add(INDIRECT_COST_RECOVERY_RATE_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(WIRE_CHARGE.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_EARN_PAYGROUP.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_POSITION.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(SYSTEM_INFORMATION.createFiscalYearMaker());
        fiscalYearMakers.add(UNIVERSITY_DATE.createFiscalYearMaker());

        return fiscalYearMakers;
    }

    /**
     * @return list of FiscalYearMaker implementations with parent-child configuration that contains a missing parent configuration
     */
    public static List<FiscalYearMaker> getFiscalYearMakerList_missingParent() {
        List<FiscalYearMaker> fiscalYearMakers = new ArrayList<FiscalYearMaker>();

        fiscalYearMakers.add(ORGANIZATION_REVERSION_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(ORGANIZATION_REVERSION.createFiscalYearMaker());
        fiscalYearMakers.add(SUB_OBJECT_CODE.createFiscalYearMaker());
//        fiscalYearMakers.add(LABOR_OBJECT.createFiscalYearMaker());
        fiscalYearMakers.add(OFFSET_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(BENEFITS_CALCULATION.createFiscalYearMaker());
//        fiscalYearMakers.add(POSITION_OBJECT_BENEFIT.createFiscalYearMaker());
        fiscalYearMakers.add(ACCOUNTING_PERIOD.createFiscalYearMaker());
        fiscalYearMakers.add(INDIRECT_COST_RECOVERY_RATE_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(WIRE_CHARGE.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_EARN_PAYGROUP.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_POSITION.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(SYSTEM_INFORMATION.createFiscalYearMaker());
        fiscalYearMakers.add(UNIVERSITY_DATE.createFiscalYearMaker());

        return fiscalYearMakers;
    }

    /**
     * @return list of FiscalYearMaker implementations with a null business object class
     */
    public static List<FiscalYearMaker> getFiscalYearMakerList_nullBusinessObjectClass() {
        List<FiscalYearMaker> fiscalYearMakers = new ArrayList<FiscalYearMaker>();

        FiscalYearMakerImpl fiscalYearMaker = (FiscalYearMakerImpl) WIRE_CHARGE.createFiscalYearMaker();
        fiscalYearMaker.setBusinessObjectClass(null);

        fiscalYearMakers.add(fiscalYearMaker);
//        fiscalYearMakers.add(SYSTEM_INFORMATION.createFiscalYearMaker());

        return fiscalYearMakers;
    }

    /**
     * @return list of FiscalYearMaker implementations with two fiscal year maker implementations for the same business object class
     */
    public static List<FiscalYearMaker> getFiscalYearMakerList_duplicateBusinessObjectClass() {
        List<FiscalYearMaker> fiscalYearMakers = new ArrayList<FiscalYearMaker>();

        fiscalYearMakers.add(OPTIONS.createFiscalYearMaker());
        fiscalYearMakers.add(OBJECT_CODE.createFiscalYearMaker());
//        fiscalYearMakers.add(BENEFITS_CALCULATION.createFiscalYearMaker());
        fiscalYearMakers.add(ACCOUNTING_PERIOD.createFiscalYearMaker());
        fiscalYearMakers.add(OBJECT_CODE.createFiscalYearMaker());

        return fiscalYearMakers;
    }

    /**
     * @return list of FiscalYearMaker implementations with parent-child configuration that contains a circular reference
     */
    public static List<FiscalYearMaker> getFiscalYearMakerList_circular1() {
        List<FiscalYearMaker> fiscalYearMakers = new ArrayList<FiscalYearMaker>();

        fiscalYearMakers.add(OPTIONS1.createFiscalYearMaker());
        fiscalYearMakers.add(ORGANIZATION_REVERSION_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(ORGANIZATION_REVERSION.createFiscalYearMaker());
        fiscalYearMakers.add(SUB_OBJECT_CODE.createFiscalYearMaker());
        fiscalYearMakers.add(OBJECT_CODE.createFiscalYearMaker());
//        fiscalYearMakers.add(LABOR_OBJECT.createFiscalYearMaker());
        fiscalYearMakers.add(OFFSET_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(BENEFITS_CALCULATION.createFiscalYearMaker());
//        fiscalYearMakers.add(POSITION_OBJECT_BENEFIT.createFiscalYearMaker());
        fiscalYearMakers.add(ACCOUNTING_PERIOD.createFiscalYearMaker());
        fiscalYearMakers.add(INDIRECT_COST_RECOVERY_RATE_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(WIRE_CHARGE.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_EARN_PAYGROUP.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_POSITION.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(SYSTEM_INFORMATION.createFiscalYearMaker());
        fiscalYearMakers.add(UNIVERSITY_DATE.createFiscalYearMaker());

        return fiscalYearMakers;
    }

    /**
     * @return list of FiscalYearMaker implementations with parent-child configuration that contains a circular reference
     */
    public static List<FiscalYearMaker> getFiscalYearMakerList_circular2() {
        List<FiscalYearMaker> fiscalYearMakers = new ArrayList<FiscalYearMaker>();

        fiscalYearMakers.add(OPTIONS.createFiscalYearMaker());
        fiscalYearMakers.add(ORGANIZATION_REVERSION_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(ORGANIZATION_REVERSION.createFiscalYearMaker());
        fiscalYearMakers.add(SUB_OBJECT_CODE.createFiscalYearMaker());
//        fiscalYearMakers.add(OBJECT_CODE1.createFiscalYearMaker());
//        fiscalYearMakers.add(LABOR_OBJECT1.createFiscalYearMaker());
        fiscalYearMakers.add(OFFSET_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(BENEFITS_CALCULATION.createFiscalYearMaker());
//        fiscalYearMakers.add(POSITION_OBJECT_BENEFIT.createFiscalYearMaker());
        fiscalYearMakers.add(ACCOUNTING_PERIOD.createFiscalYearMaker());
        fiscalYearMakers.add(INDIRECT_COST_RECOVERY_RATE_DETAIL.createFiscalYearMaker());
        fiscalYearMakers.add(WIRE_CHARGE1.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_EARN_PAYGROUP.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_POSITION.createFiscalYearMaker());
//        fiscalYearMakers.add(EFFORT_CERTIFICATION_REPORT_DEFINITION.createFiscalYearMaker());
//        fiscalYearMakers.add(SYSTEM_INFORMATION.createFiscalYearMaker());
        fiscalYearMakers.add(UNIVERSITY_DATE.createFiscalYearMaker());

        return fiscalYearMakers;
    }

}
