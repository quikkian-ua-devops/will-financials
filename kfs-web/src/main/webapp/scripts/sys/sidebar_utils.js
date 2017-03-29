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
import KfsUtils from './utils.js';
import Link from '../components/general/link.jsx';

export function determinePanelClassName(expandedLinkGroup, label) {
    let panelClassName = "panel list-item";
    if (expandedLinkGroup === label) {
        panelClassName += " active";
    }
    return panelClassName;
};

export function convertLinks(links, type, backdoorId) {
    if (!links) {
        return "";
    }
    let backdoorIdAppender = KfsUtils.buildBackdoorIdAppender(backdoorId);
    return links.map((link, i) => {
        let target = null;
        if ((link.linkType !== 'kfs' && link.linkType !== 'report' ) || link.newTarget) {
            target = '_blank';
        }
        let url = link.linkType === 'rice' ? backdoorIdAppender(link.link) : link.link;
        return <Link key={type + "_" + i} url={url} label={link.label} className="list-group-item" target={target} click={stayOnPage}/>
    })
};

export function buildDisplayLinks(links, type, checkedLinkFilters, backdoorId) {
    let displayLinks = [];
    if (links && links[type] && checkedLinkFilters && checkedLinkFilters.indexOf(type) != -1) {
        displayLinks = convertLinks(links[type], type, backdoorId);
    }
    return displayLinks;
};

export function addHeading(links, type) {
    let newLinks = [];
    if (links.length > 0) {
        newLinks = newLinks.concat([<h4 key={type + "_label"}>{type}</h4>]).concat(links);
    }
    return newLinks;
};

export function determineSublinkClass(links, headingCount, expanded) {
    let sublinksClass = "sublinks collapse";
    // 1400px is the width at which links in 3rd column start to clip (unzoomed)
    let mq = window.matchMedia("screen and (min-width: 1400px)");
    if (links.length > (36 - headingCount)) {
        if (mq.matches) {
            sublinksClass += " col-3";
        } else {
            sublinksClass += " col-2";
        }
    } else if (links.length > (18 - headingCount)) {
        sublinksClass += " col-2";
    }
    if (expanded) {
        sublinksClass += " active";
    }
    return sublinksClass;
};

const SidebarUtils = {
    determinePanelClassName,
    buildDisplayLinks,
    addHeading,
    determineSublinkClass
};

export default SidebarUtils;
