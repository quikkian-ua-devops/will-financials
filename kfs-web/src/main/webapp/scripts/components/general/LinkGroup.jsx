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
import React from 'react';
import KfsUtils from '../../sys/utils.js';
import { determinePanelClassName } from '../../sys/sidebar_utils.js';

var LinkGroup = React.createClass({
    render() {
        let label = this.props.group.label;
        let id = KfsUtils.buildKeyFromLabel(label);
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label);

        let links = this.props.group.links;
        let linksCount = 0;
        this.props.checkedLinkFilters.forEach(function(filter) {
            if (links[filter]) {
                linksCount += links[filter].length;
            }
        });

        if (linksCount > 0) {
            return (
                <li className={panelClassName}>
                    <a href="#d" onClick={this.props.handleClick.bind(null, label, id + '-menu')}>
                        <span>{label}</span>
                    </a>
                </li>
            )
        } else {
            return null
        }
    }
});

export default LinkGroup;
