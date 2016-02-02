<!--
- The Kuali Financial System, a comprehensive financial management system for higher education.
-
- Copyright 2005-2014 The Kuali Foundation
-
- This program is free software: you can redistribute it and/or modify
- it under the terms of the GNU Affero General Public License as
- published by the Free Software Foundation, either version 3 of the
- License, or (at your option) any later version.
-
- This program is distributed in the hope that it will be useful,
- but WITHOUT ANY WARRANTY; without even the implied warranty of
- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
- GNU Affero General Public License for more details.
-
- You should have received a copy of the GNU Affero General Public License
- along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<div class="remodal-overlay"></div>
<div class="remodal-wrapper">
    <div id="remodal" class="remodal" data-remodal-id="modal" data-remodal-options="hashTracking: false">
        <div class="remodal-content"></div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        $(document).on('click', 'a[data-remodal-target="modal"]', function(event) {
            event.preventDefault();
            var myModal = $('#remodal');

            var modalBody = myModal.find('.remodal-content');
            var href = $(event.target).attr('href');
            var title = $(event.target).attr('data-label') || $.trim($(event.target).text());
            modalBody.load(href, function(response, status, xhr) {
                if ( status == "error" ) {
                    var msg = "Sorry but there was an error: ";
                    var html = '<div class="fullwidth inquirymodal body"><main class="content">';
                        html += '<div class="modal-header"><div id="breadcrumbs"></div><button type="button" data-remodal-action="close" class="close remodal-close"><span aria-hidden="true">&times;</span></button></div>';
                        html += '<div id="view_div"><div class="inquiry"><div class="main-panel">';
                        html += '<div class="headerarea-small"><h2>Error</h2></div>';
                        html += '<div style="padding: 30px 0;">' + msg + xhr.status + " " + xhr.statusText + '</div>';
                        html += '</div></div></div>';
                        html += '</main></div>';
                    modalBody.html(html);
                    myModal.remodal();
                } else {
                    myModal.remodal();

                    // if we just clicked one of the crumbs then pop everything off the stack on top of it
                    var stackIndex = $(event.target).attr("data-stack-index");
                    if (stackIndex > -1) {
                        breadcrumbs = breadcrumbs.slice(0, stackIndex);
                    }

                    breadcrumbs.push({title: title, href: href});

                    var crumbs = '';
                    for (var i = 0; i < breadcrumbs.length; i++) {
                        if (i < breadcrumbs.length - 1 ) {
                            crumbs += '<a href="' + breadcrumbs[i].href + '" title="' + breadcrumbs[i].title + '" data-remodal-target="modal" data-stack-index="' + i + '">';
                            crumbs +=       breadcrumbs[i].title;
                            crumbs += '</a>';
                            crumbs += '<span class="glyphicon glyphicon-chevron-right"></span>';
                        } else {
                            crumbs += breadcrumbs[i].title;
                        }
                    }
                    $('#breadcrumbs').html(crumbs);
                }
            });
        });

        $(document).on('closed', '.remodal', function () {
            breadcrumbs = [];
            $('#remodal .remodal-content').html('');
        });
    });
</script>