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
package org.kuali.kfs.core.framework.persistence.ojb.conversion;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.rice.core.api.CoreApiServiceLocator;

import java.security.GeneralSecurityException;

/**
 * This class calls core service to encrypt values going to the database and decrypt values coming back from the database.
 *
 *
 */

public class OjbKualiEncryptDecryptFieldConversion implements FieldConversion {
    private static final long serialVersionUID = 2450111778124335242L;

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        Object converted = source;

        try {
            //check if the encryption service is enable before using it
            if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                converted = CoreApiServiceLocator.getEncryptionService().encrypt(converted);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to encrypt value to db: " + e.getMessage());
        }

        return converted;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        String converted = "";
        if (source != null) {
            converted = source.toString();
        }

        try {
            //check if the encryption service is enable before using it
            if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                converted = CoreApiServiceLocator.getEncryptionService().decrypt(converted);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to decrypt value from db: " + e.getMessage());
        }

        return converted;
    }
}
