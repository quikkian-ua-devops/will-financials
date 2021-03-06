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
package org.kuali.kfs.sys.identity;

import org.kuali.kfs.kns.kim.role.RoleTypeServiceBase;

/**
 * The "default" Rice type service (org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase) we were using for
 * FO delegation is no longer a DelegationTypeService, so this is an alternative we can use to avoid
 * having error messages in the log.
 */
public class DefaultRoleTypeServiceImpl extends RoleTypeServiceBase {

}
