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
package org.kuali.kfs.sys.service;

import org.kuali.rice.kim.api.identity.Person;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface InstitutionPreferencesService {
    Map<String, Object> findInstitutionPreferencesLinks(Person person, boolean useCache);

    Map<String, Object> findInstitutionPreferencesNoLinks();

    void saveInstitutionPreferences(String institutionId, String linkGroups);

    Map<String, Object> getAllLinkGroups();

    List<Map<String, String>> getMenu();

    List<Map<String, String>> saveMenu(String menu);

    Map<String, String> getLogo();

    Map<String, String> uploadLogo(InputStream uploadedInputStream, String filename);

    Map<String, String> saveLogo(String logo);

    void setInstitutionPreferencesCacheLength(int seconds);

    int getInstitutionPreferencesCacheLength();

    boolean hasConfigurationPermission(String principalName);
}
