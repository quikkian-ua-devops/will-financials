#
# The Kuali Financial System, a comprehensive financial management system for higher education.
#
# Copyright 2005-2017 Kuali, Inc.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

FROM tomcat:8.5.13-jre8-alpine

RUN apk add --no-cache curl

RUN curl -k -o $CATALINA_HOME/lib/mysql-connector-java-5.1.36.jar http://nexus.kuali.org/service/local/repositories/central/content/mysql/mysql-connector-java/5.1.36/mysql-connector-java-5.1.36.jar

COPY kfs-web/target/kfs-web.war $CATALINA_HOME/webapps/kfs-dev.war

COPY kfs-core/src/main/config/demo/rice.keystore $CATALINA_HOME/conf/

EXPOSE 8080
