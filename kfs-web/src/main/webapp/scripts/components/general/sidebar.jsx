/*
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
import React from 'react';
import { render } from 'react-dom';
import { convertLinks, addHeading, determineSublinkClass } from '../../sys/sidebar_utils.js';
import UserPrefs from '../../sys/user_preferences.js';
import KfsUtils from '../../sys/utils.js';
import LinkFilter from './LinkFilter.jsx';
import LinkGroupSublinks from './LinkGroupSublinks.jsx';
import LinkGroup from './LinkGroup.jsx';
import SidebarWaiting from './SidebarWaiting.jsx';
import SidebarError from './SidebarError.jsx';

var Sidebar = React.createClass({
    getInitialState() {
        let sidebarOut = !$('#sidebar').hasClass('collapsed');

        return {
            loadingData: true,
            loadError: false,
            principalName: "",
            institutionPreferences: {},
            userPreferences: {
                checkedLinkFilters: ["activities", "reference", "administration"]
            },
            expandedLinkGroup: "",
            expandedSearch: false,
            search: '',
            searchResults: undefined,
            backdoorId: '',
            sidebarOut: sidebarOut
        };
    },
    componentWillMount() {
        let thisComponent = this;
        let found = false;

        UserPrefs.getBackdoorId((backdoorId) => {
            UserPrefs.getPrincipalName(function (principalName) {
                thisComponent.setState({principalName: principalName});
                let preferencesString = null;
                try {
                    preferencesString = localStorage.getItem("institutionPreferences");
                } catch(err) {
                    // Ignore the error, some browsers don't support localStorage
                }
                if (preferencesString != null) {
                    let sessionId = KfsUtils.getKualiSessionId();
                    let prefs = JSON.parse(preferencesString);
                    if ((prefs.sessionId == sessionId) && (prefs.principalName == principalName)) {
                        found = true;
                        thisComponent.setState({
                            loadingData: false,
                            loadError: false,
                            backdoorId: backdoorId,
                            institutionPreferences: prefs
                        });
                    } else {
                        try {
                            localStorage.removeItem("institutionPreferences");
                        } catch(err) {
                            // Ignore the error, some browsers don't support localStorage
                        }
                    }
                }

                if (!found) {
                    let institutionLinksPath = KfsUtils.getUrlPathPrefix() + "sys/api/v1/preferences/institution-links/" + principalName;
                    KfsUtils.ajaxCall({
                        url: institutionLinksPath,
                        dataType: 'json',
                        cache: false,
                        type: 'GET',
                        success: function (preferences) {
                            thisComponent.setState({
                                loadingData: false,
                                loadError: false,
                                backdoorId: backdoorId,
                                institutionPreferences: preferences
                            });
                            preferences.sessionId = KfsUtils.getKualiSessionId();
                            try {
                                localStorage.setItem("institutionPreferences", JSON.stringify(preferences));
                            } catch(err) {
                                // Ignore the error, some browsers don't support localStorage
                            }
                        }.bind(this),
                        error: function (xhr, status, err) {
                            console.error(status, err.toString());
                            thisComponent.setState({loadError: true});
                        }.bind(this)
                    });
                }
            }, function () {
                console.error("Error retrieving principalName");
                thisComponent.setState({loadError: true});
            })
        }, (status, message) => {console.error(status, message.toString());});

        UserPrefs.getUserPreferences(function (userPreferences) {
            thisComponent.setState({userPreferences: userPreferences});
        }, function (error) {
            console.log("error getting preferences: " + error);
            thisComponent.setState({loadError: true});
        });
    },
    componentDidUpdate() {
        let newClass;
        let activePanelId = KfsUtils.buildKeyFromLabel(this.state.expandedLinkGroup) + "-menu";
        let activePanel = $('#' + activePanelId);

        if (!activePanel.parent().hasClass('search') && activePanel.offset()) {
            let panelHeight = activePanel.outerHeight();
            if ($('#sidebar-scroll').outerHeight() < ($('#linkgroups').outerHeight() + $('#sidebar-scroll>div.refresh').outerHeight(true))) {
                let sidebarMiddle = ($('#sidebar-scroll').outerHeight() + 170) / 2;
                let topPosition = sidebarMiddle - (panelHeight / 2);
                activePanel.css('top', topPosition);
                activePanel.addClass('floating');
            } else {
                let activeLink = $('#linkgroups .active');
                let sidebarHeight = $('#sidebar>div').outerHeight();
                let distanceFromTop = activeLink.offset().top - $('#sidebar>div').offset().top;
                let distanceFromBottom = sidebarHeight - distanceFromTop;
                let centerY = (activeLink.outerHeight() / 2 + panelHeight / 2);
                if (distanceFromBottom < distanceFromTop && distanceFromBottom < panelHeight) {
                    if (distanceFromTop > distanceFromBottom && distanceFromBottom > centerY) {
                        newClass = 'flowCenter';
                    } else {
                        newClass = 'flowUp';
                    }
                } else if (distanceFromBottom < panelHeight && distanceFromBottom > centerY) {
                    newClass = 'flowCenter';
                }

                if (newClass) {
                    activePanel.addClass(newClass);
                    if (newClass === 'flowCenter') {
                        let centeredTop = (distanceFromTop + activeLink.outerHeight() / 2 - panelHeight / 2);
                        if (centeredTop < 0) {
                            centeredTop = 10;
                        }
                        activePanel.css('top', centeredTop);
                    } else if (newClass === 'flowUp') {
                        activePanel.css('bottom', distanceFromBottom - activeLink.outerHeight() + 1);
                    }
                } else {
                    activePanel.css('top', distanceFromTop);
                }
            }
        }
        if (activePanel) {
            activePanel.scrollTop(0);
        }
    },
    modifyLinkFilter(type) {
        let userPreferences = this.state.userPreferences;
        let newChecked = userPreferences.checkedLinkFilters;
        let index = newChecked.indexOf(type);
        if (index === -1) {
            newChecked.push(type);
        } else {
            newChecked.splice(index, 1);
        }
        this.setState({userPreferences: userPreferences});
        UserPrefs.putUserPreferences(userPreferences);
    },
    toggleLinkGroup(label, sublinksId) {
        if (this.state.expandedLinkGroup === label) {
            this.setState({expandedLinkGroup: ""});
            let sublinks = $('#' + sublinksId);
            sublinks.removeClass('flowUp flowCenter');
            sublinks.css({'bottom': 'inherit', 'top': 'inherit'});
            $('#content-overlay').removeClass('visible');
            $('html').off('click','**');
        } else {
            this.setState({expandedLinkGroup: label});
            $('#content-overlay').addClass('visible');
            let sidebar = this;
            $('html').on('click',function(event) {
                if (!$(event.target).closest('.sublinks').length &&
                    !$(event.target).closest('li.panel.active').length &&
                    !$(event.target).closest('#linkFilter').length) {
                    let sublinks = $('#' + sublinksId);
                    sublinks.removeClass('flowUp flowCenter');
                    sublinks.css({'bottom': 'inherit', 'top': 'inherit'});
                    $('#content-overlay').removeClass('visible');
                    sidebar.setState({expandedLinkGroup: ""});
                }
            });
        }
    },
    toggleSidebar() {
        $('#menu-toggle').toggleClass('rotated');
        $('#sidebar').toggleClass('collapsed');
        $('main.content').toggleClass('fullwidth');
        if (!this.state.sidebarOut) {
            $('li.search>input').focus();
        }
        this.setState({ sidebarOut: !this.state.sidebarOut, expandedLinkGroup: "" });
    },
    refreshLinks() {
        let thisComponent = this;
        let institutionLinksPath = KfsUtils.getUrlPathPrefix() + "sys/api/v1/preferences/institution-links/" + thisComponent.state.principalName;

        thisComponent.setState({loadingData: true});

        KfsUtils.ajaxCall({
            url: institutionLinksPath,
            dataType: 'json',
            cache: false,
            type: 'GET',
            beforeSend: function(xhr) {
                xhr.setRequestHeader('cache-control', 'must-revalidate');
            },
            success: function (preferences) {
                thisComponent.setState({
                    loadingData: false,
                    loadError: false,
                    institutionPreferences: preferences
                });
                preferences.sessionId = KfsUtils.getKualiSessionId();
                try {
                    localStorage.setItem("institutionPreferences", JSON.stringify(preferences));
                } catch(err) {
                    // Ignore the error, some browsers don't support localStorage
                }
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(status, err.toString());
                thisComponent.setState({loadError: true});
            }.bind(this)
        });
    },
    autocompleteSearch(event) {
        let searchString = event.target.value;
        let expandedSearch = searchString.length > 2;

        let newState = {'search': searchString, 'expandedSearch': expandedSearch, expandedLinkGroup: ""};

        if (!expandedSearch) {
            $('#content-overlay').removeClass('visible');
            $('html').off('click','**');
        } else {
            $('#content-overlay').addClass('visible');
            $('html').on('click', event => {
                if (!$(event.target).closest('li.panel.active').length && !$(event.target).closest('#linkFilter').length) {
                    $('li.panel.active').removeClass('active');
                    $('#content-overlay').removeClass('visible');
                    this.setState({expandedSearch: false});
                }
            });

            let results = {};
            let lowerSearchString = searchString.toLowerCase();
            this.state.institutionPreferences.linkGroups.forEach(linkGroup => {
                let groupResults = [];
                let groupLinks = linkGroup.links;
                for (let groupLinkType of Object.keys(groupLinks)) {
                    let linksOfType = groupLinks[groupLinkType];
                    let filteredLinks = linksOfType.filter(link => {
                        return link.label.toLowerCase().indexOf(lowerSearchString) != -1;
                    }).map(link => {
                        let newLink = $.extend(true, {}, link);
                        let searchPattern = new RegExp('('+searchString+')', 'ig');
                        let splitLabel = newLink.label.split(searchPattern);
                        newLink.label = splitLabel.map(piece => piece.toLowerCase() === lowerSearchString ? <strong>{piece}</strong> : piece);
                        return newLink;
                    });
                    groupResults = groupResults.concat(filteredLinks);
                }
                if (groupResults.length > 0) {
                    results[linkGroup.label] = groupResults;
                }
            });

            newState.searchResults = results;
        }

        this.setState(newState);
    },
    clearSearch() {
        this.refs.searchBox.getDOMNode().focus();
        this.setState({'search': '', 'searchResults': undefined, 'expandedSearch': false});
        $('#content-overlay').removeClass('visible');
        $('html').off('click','**');
    },
    closeSearch() {
        $('li.search.panel.active div.sublinks.active').removeClass('active');
        $('li.search.panel.active').removeClass('active');
        $('#content-overlay').removeClass('visible');
        $('html').off('click','**');
        this.setState({expandedSearch: false});
    },
    render() {
        if ( this.state.sidebarOut && this.state.loadError ) {
            return (
                <SidebarError/>
            );
        }
        if ( this.state.sidebarOut && this.state.loadingData ) {
            return (
                <SidebarWaiting/>
            );
        }

        let rootPath = KfsUtils.getUrlPathPrefix();
        let linkGroups = [];
        let linkGroupSublinks = [];
        if (this.state.institutionPreferences.linkGroups) {
            let groups = this.state.institutionPreferences.linkGroups;
            for (let i = 0; i < groups.length; i++) {
                linkGroups.push(
                    <LinkGroup key={i}
                        group={groups[i]}
                        handleClick={this.toggleLinkGroup}
                        checkedLinkFilters={this.state.userPreferences.checkedLinkFilters}
                        expandedLinkGroup={this.state.expandedLinkGroup}/>
                );

                linkGroupSublinks.push(
                    <LinkGroupSublinks key={i}
                               group={groups[i]}
                               handleClick={this.toggleLinkGroup}
                               checkedLinkFilters={this.state.userPreferences.checkedLinkFilters}
                               backdoorId={this.state.backdoorId}
                               expandedLinkGroup={this.state.expandedLinkGroup}/>
                );
            }
        }

        let menuToggleClassName = "glyphicon glyphicon-menu-left";
        if (this.state.sidebarOut === false) {
            menuToggleClassName += " rotated";
            $('#sidebar').addClass('collapsed');
        }

        let navSearchClass = "search list-item panel";
        if (this.state.expandedSearch) {
            navSearchClass += " active";
        }

        let searchResultsClass;
        let searchResults = undefined;
        if (this.state.searchResults && Object.keys(this.state.searchResults).length > 0) {
            let finalLinks = [];
            let groupCount = 0;
            for (let resultGroup of Object.keys(this.state.searchResults)) {
                let displayLinks = convertLinks(this.state.searchResults[resultGroup], 'navSearch' + resultGroup, this.state.backdoorId);
                finalLinks = finalLinks.concat(addHeading(displayLinks, resultGroup));
                groupCount++;
            }
            searchResults = finalLinks;

            if (groupCount > 0) {
                groupCount--;
            }

            searchResultsClass = determineSublinkClass(finalLinks, groupCount, this.state.expandedSearch);
        } else if (this.state.expandedSearch) {
            searchResults = [];
            searchResults.push(<div key="no-search-results" className="center">No results found</div>);

            searchResultsClass = "sublinks collapse active";
        }

        if (searchResults) {
            searchResults.push(<button key="close-search-button" type="button" className="close" onClick={this.closeSearch}><span aria-hidden="true">&times;</span></button>);
        }

        let homeClick = null;
        if (typeof stayOnPage === 'function') {
            homeClick = stayOnPage;
        }

        return (
            <div>
                <ul id="filters" className="nav list-group">
                    <li id="home-item">
                        <span id="home">
                            <a href={rootPath} onClick={homeClick}><span className="fa fa-home home-icon"></span>Home</a>
                        </span>
                        <span id="menu-toggle" className={menuToggleClassName} onClick={this.toggleSidebar}></span>
                    </li>
                    <li className={navSearchClass}>
                        <input type="search" placeholder="Search" onChange={this.autocompleteSearch} value={this.state.search} ref="searchBox" onFocus={this.autocompleteSearch} />
                        <span className="glyphicon glyphicon-remove remove" onClick={this.clearSearch}></span>
                        <div className={searchResultsClass}>
                            <div className="links-container">
                                {searchResults}
                            </div>
                        </div>
                    </li>
                    <li className="list-item"><LinkFilter checkedLinkFilters={this.state.userPreferences.checkedLinkFilters} modifyLinkFilter={this.modifyLinkFilter} /></li>
                </ul>
                <div id="sidebar-scroll">
                    <ul id="linkgroups" className="nav list-group">
                        {linkGroups}
                    </ul>
                    <div className="refresh">
                        Missing something? <a href="#d" onClick={this.refreshLinks}><span> Refresh Menu </span></a> to make sure your permissions are up to date.
                    </div>
                </div>
                <div>
                    {linkGroupSublinks}
                </div>
            </div>
        )
    }
});

render(
    <Sidebar/>,
    document.getElementById('sidebar')
);

export default Sidebar;
