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
package org.kuali.kfs.module.ar.businessobject;

import java.io.ByteArrayOutputStream;

/**
 * Wraps an output report PDF and metadata about it
 */
public class ReportPDFHolder {
    private ByteArrayOutputStream reportBytes;
    private String contentDisposition;

    public ReportPDFHolder(ByteArrayOutputStream baos, String contentDisposition) {
        this.reportBytes = baos;
        this.contentDisposition = contentDisposition;
    }

    public ByteArrayOutputStream getReportBytes() {
        return reportBytes;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }
}
