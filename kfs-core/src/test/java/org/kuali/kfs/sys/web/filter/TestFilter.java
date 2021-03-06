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
package org.kuali.kfs.sys.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class TestFilter implements Filter {
    public static boolean initCalled;
    public static String test1Parameter;
    public static String test2Parameter;
    public static boolean doFilterCalled;
    public static boolean destroyCalled;

    public TestFilter() {
        initCalled = false;
        doFilterCalled = false;
        destroyCalled = false;
        test1Parameter = null;
        test2Parameter = null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initCalled = true;
        test1Parameter = filterConfig.getInitParameter("test1");
        test2Parameter = filterConfig.getInitParameter("test2");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilterCalled = true;
    }

    @Override
    public void destroy() {
        destroyCalled = true;
    }
}
