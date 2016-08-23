/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.coa.document;

import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.maintenance.Maintainable;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.Row;
import org.kuali.kfs.kns.web.ui.Section;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class overrides the getCoreSections method to provide specific field conversions for the postal code
 */
public class KualiOrgMaintainable extends FinancialSystemMaintainable {
    private static final long serialVersionUID = -3182120468758958991L;

    protected static final String KUALI_ORG_SECTION = "Edit Organization Code";

    /**
     * Provides special field conversions for the Org.organizationZipCode
     *
     * @see org.kuali.rice.kns.maintenance.Maintainable#getCoreSections(org.kuali.rice.kns.maintenance.Maintainable)
     * <p>
     * KRAD Conversion: Maintainable performs the logic to adds new fields to sections if
     * organization zip code field exists in the core sections.
     * The fields definitions are built here for the new section.  They are not in data dictionary.
     */
    @Override
    public List getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {

        boolean fieldFound = false;
        boolean sectionFound = false;

        // walk the sections
        List<Section> sections = super.getCoreSections(document, oldMaintainable);
        for (Section section : sections) {
            // if this is the section we're looking for
            if (section.getSectionTitle().equalsIgnoreCase(KUALI_ORG_SECTION)) {
                // mark that we found the section
                sectionFound = true;
                // walk the rows
                List rows = section.getRows();
                for (Row row : section.getRows()) {
                    // walk the fields
                    for (Field field : row.getFields()) {
                        // if this is the field we're looking for ...
                        if (field.getPropertyName().equalsIgnoreCase(KFSPropertyConstants.ORGANIZATION_ZIP_CODE)) {
                            // build the fieldConversions for the UserID field lookup
                            Map<String, String> fieldConversions = new HashMap<String, String>(3);
                            fieldConversions.put(KFSPropertyConstants.CODE, KFSPropertyConstants.ORGANIZATION_ZIP_CODE);
                            fieldConversions.put(KFSPropertyConstants.POSTAL_STATE_CODE, KFSPropertyConstants.ORGANIZATION_STATE_CODE);
                            fieldConversions.put(KFSPropertyConstants.POSTAL_CITY_NAME, KFSPropertyConstants.ORGANIZATION_CITY_NAME);

                            // add the fieldConversions, lookupParameters and the lookup class
                            field.setFieldConversions(fieldConversions);
                            // field.setLookupParameters(lookupParameters);
                            // field.setQuickFinderClassNameImpl(Person.class.getName());

                            // mark that we've found the field
                            fieldFound = true;
                            break;
                        }
                    }
                    // if we found the field, we don't need the rest of the rows
                    if (fieldFound) {
                        break;
                    }
                }
                break; // we've found the section, no need to look further
            }
        }

        // if the section no longer exists, fail loudly
        if (!sectionFound) {
            throw new RuntimeException("There is no longer a section titled '" + KUALI_ORG_SECTION + "'. " + "As a result, the lookup setup will not work as expected and the maintenance document will be broken.  The correct name needs to be set in the Constant in this class.");
        }
        // if the field was not found, fail loudly
        if (!fieldFound) {
            throw new RuntimeException("There is no longer a field titled '" + KFSPropertyConstants.ORGANIZATION_ZIP_CODE + "'. " + "As a result, the lookup setup will not work as expected and the maintenance document will be broken.  The correct name needs to be set in the KFSPropertyConstants class.");
        }

        return sections;
    }

    /**
     * Override to purge the parent org cache when an organization is saved.
     */
    @Override
    public void saveBusinessObject() {
        super.saveBusinessObject();
        SpringContext.getBean(OrganizationService.class).flushParentOrgCache();
    }
}

