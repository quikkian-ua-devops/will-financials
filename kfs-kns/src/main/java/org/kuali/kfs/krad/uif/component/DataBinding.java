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
package org.kuali.kfs.krad.uif.component;


/**
 * Components that bind to a model (hold model data) should implement this
 * interface
 * <p>
 * <p>
 * Provides access to the <code>BindingInfo</code> object for the component that
 * contains binding configuration
 * </p>
 */
public interface DataBinding {

    /**
     * Returns the <code>BindingInfo</code> instance that is configured for the
     * component
     *
     * @return BindingInfo
     * @see BindingInfo
     */
    public BindingInfo getBindingInfo();

    /**
     * Setter for the binding info instance
     *
     * @param bindingInfo
     */
    public void setBindingInfo(BindingInfo bindingInfo);

    /**
     * Name of the property (relative to the parent object) the component binds
     * to
     *
     * @return String property name
     */
    public String getPropertyName();

}
