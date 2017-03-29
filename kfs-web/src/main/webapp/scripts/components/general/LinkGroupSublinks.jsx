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
import { buildDisplayLinks, addHeading, determineSublinkClass } from '../../sys/sidebar_utils.js';

var LinkGroupSublinks = React.createClass({
    render() {
        let label = this.props.group.label;
        let id = KfsUtils.buildKeyFromLabel(label);

        let activitiesLinks = buildDisplayLinks(this.props.group.links, 'activities', this.props.checkedLinkFilters, this.props.backdoorId);
        let referenceLinks = buildDisplayLinks(this.props.group.links, 'reference', this.props.checkedLinkFilters, this.props.backdoorId);
        let administrationLinks = buildDisplayLinks(this.props.group.links, 'administration', this.props.checkedLinkFilters, this.props.backdoorId);

        let links = addHeading(activitiesLinks, 'Activities');
        links = links.concat(addHeading(referenceLinks, 'Reference'));
        links = links.concat(addHeading(administrationLinks, 'Administration'));

        let headingCount = links.length - (activitiesLinks.length + referenceLinks.length + administrationLinks.length);
        if (headingCount > 0) {
            headingCount--;
        }

        let sublinksClass = determineSublinkClass(links, headingCount, this.props.expandedLinkGroup === label);

        if (links.length > 0) {
            return (
                <div id={id + "-menu"} className={sublinksClass}>
                    <h3>{label}</h3>
                    <div className="links-container">
                        {links}
                    </div>
                    <button type="button" className="close" onClick={this.props.handleClick.bind(null, label, id + '-menu')}><span aria-hidden="true">&times;</span></button>
                </div>
            )
        } else {
            return null
        }
    }
});

export default LinkGroupSublinks;
