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
package org.kuali.kfs.sys.rest.application;

import com.sun.jersey.api.container.filter.LoggingFilter;
import org.kuali.kfs.sys.rest.resource.HealthResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("health")
public class HealthApplication extends Application {
    protected Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> clazzes = new HashSet<>();

    public HealthApplication() {
        singletons.add(new HealthResource());
        clazzes.add(LoggingFilter.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return clazzes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
