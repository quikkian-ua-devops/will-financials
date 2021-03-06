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
package org.kuali.rice.kim.api.jaxb;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * An XML adapter that converts between QualificationList objects and Map<String, String> objects.
 * Unmarshalled keys and values will automatically be trimmed if non-null.
 * <p>
 * <p>This adapter will throw an exception during unmarshalling if blank or duplicate keys are encountered.
 */
public class QualificationListAdapter extends XmlAdapter<QualificationList, Map<String, String>> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Map<String, String> unmarshal(QualificationList v) throws Exception {
        if (v != null) {
            NormalizedStringAdapter normalizedStringAdapter = new NormalizedStringAdapter();
            Map<String, String> map = new HashMap<String, String>();
            for (MapStringStringAdapter.StringMapEntry stringMapEntry : v.getQualifications()) {
                String tempKey = normalizedStringAdapter.unmarshal(stringMapEntry.getKey());
                if (StringUtils.isBlank(tempKey)) {
                    throw new UnmarshalException("Cannot create a qualification entry with a blank key");
                } else if (map.containsKey(tempKey)) {
                    throw new UnmarshalException("Cannot create more than one qualification entry with a key of \"" + tempKey + "\"");
                }
                map.put(tempKey, normalizedStringAdapter.unmarshal(stringMapEntry.getValue()));
            }
        }
        return null;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public QualificationList marshal(Map<String, String> v) throws Exception {
        return (v != null) ? new QualificationList(v) : null;
    }

}
