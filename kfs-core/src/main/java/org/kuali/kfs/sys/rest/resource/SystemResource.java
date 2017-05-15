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
package org.kuali.kfs.sys.rest.resource;

import org.kuali.rice.core.api.config.property.ConfigContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This resource will have system related endpoints.
 */
@Path("/system")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SystemResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SystemResource.class);

    @GET
    @Path("/environment")
    public Response getEnvironment() {
        LOG.debug("getEnvironment() started");

        return Response.ok(new Environment()).build();
    }

    class Environment {
        public boolean getProdMode() {
            return ConfigContext.getCurrentContextConfig().isProductionEnvironment();
        }

        public String getEnvironment() {
            return ConfigContext.getCurrentContextConfig().getEnvironment();
        }
    }
}
