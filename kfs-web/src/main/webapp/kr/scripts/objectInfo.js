/*
 * Copyright 2005-2015 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function loadUserInfo( userIdFieldName, universalIdFieldName, userNameFieldName ) {
	var userId = dwr.util.getValue( userIdFieldName );

	if (userId == "") {
		clearRecipients( universalIdFieldName, "" );
		clearRecipients( userNameFieldName, "" );
	} else {
		var dwrReply = {
			callback:function(data) {
				if ( data != null && typeof data == 'object' ) {
					if ( universalIdFieldName != null && universalIdFieldName != "" ) {
						setRecipientValue( universalIdFieldName, data.principalId );
					}
					if ( userNameFieldName != null && userNameFieldName != "" ) {
						setRecipientValue( userNameFieldName, data.name );
					} else {
						// guess the DIV name
						divName = userIdFieldName.replace( ".principalName", ".name.div" );
						dwr.util.setValue( divName, data.name );
					}
				} else {
					if ( universalIdFieldName != null && universalIdFieldName != "" ) {
						setRecipientValue( universalIdFieldName, "" );
					}
					if ( userNameFieldName != null && userNameFieldName != "" ) {
						setRecipientValue( userNameFieldName, wrapError( "person not found" ), true );
					} else {
						// guess the DIV name
						divName = userIdFieldName.replace( ".principalName", ".name.div" );
						dwr.util.setValue( divName, wrapError( "person not found" ), { escapeHtml:false} );
					}
				}
			},
			errorHandler:function( errorMessage ) {
				window.status = errorMessage;
				if ( universalIdFieldName != null && universalIdFieldName != "" ) {
					setRecipientValue( universalIdFieldName, "" );
				}
				if ( userNameFieldName != null && userNameFieldName != "" ) {
					setRecipientValue( userNameFieldName, wrapError( "person not found" ), true );
				} else {
					// guess the DIV name
					divName = userIdFieldName.replace( ".principalName", ".name.div" );
					dwr.util.setValue( divName, wrapError( "person not found" ), { escapeHtml:false} );
				}
			}
		};
		PersonService.getPersonByPrincipalName( userId, dwrReply );
	}
}

